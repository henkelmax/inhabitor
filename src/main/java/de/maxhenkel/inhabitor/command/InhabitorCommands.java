package de.maxhenkel.inhabitor.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.maxhenkel.inhabitor.Inhabitor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class InhabitorCommands {

    public static final String INHABITOR_COMMAND = "inhabitor";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal(INHABITOR_COMMAND).requires(stack -> stack.hasPermission(Inhabitor.CONFIG.inhabitorCommandPermissionLevel.get()));

        literalBuilder.then(Commands.literal("add")
                .then(Commands.argument("from", ColumnPosArgument.columnPos())
                        .then(Commands.argument("to", ColumnPosArgument.columnPos())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(context -> addInhabitedTime(context, true))))));

        literalBuilder.then(Commands.literal("set")
                .then(Commands.argument("from", ColumnPosArgument.columnPos())
                        .then(Commands.argument("to", ColumnPosArgument.columnPos())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(context -> addInhabitedTime(context, true))))));

        dispatcher.register(literalBuilder);
    }

    public static int addInhabitedTime(CommandContext<CommandSourceStack> context, boolean add) {
        ChunkPos from = ColumnPosArgument.getColumnPos(context, "from").toChunkPos();
        ChunkPos to = ColumnPosArgument.getColumnPos(context, "to").toChunkPos();

        int fromX = Math.min(from.x, to.x);
        int fromZ = Math.min(from.z, to.z);
        int toX = Math.max(from.x, to.x);
        int toZ = Math.max(from.z, to.z);

        int amount = IntegerArgumentType.getInteger(context, "amount");

        int totalChunkCount = ((toX - fromX) + 1) * ((toZ - fromZ) + 1);

        AtomicInteger x = new AtomicInteger(fromX);
        AtomicInteger z = new AtomicInteger(fromZ);
        AtomicInteger count = new AtomicInteger(0);
        AtomicLong lastUpdate = new AtomicLong(0L);

        MinecraftServer server = context.getSource().getLevel().getServer();

        AtomicReference<Runnable> taskReference = new AtomicReference<>();

        Runnable task = () -> {
            int chunkX = x.getAndIncrement();
            int chunkZ = z.get();

            if (chunkX > toX) {
                chunkX = fromX;
                x.set(fromX + 1);
                chunkZ = z.incrementAndGet();
                if (chunkZ > toZ) {
                    if (count.get() > 0) {
                        context.getSource().sendSuccess(Component.literal("Successfully updated inhabitedTime for %s chunks".formatted(count.get())), false);
                    } else {
                        context.getSource().sendFailure(Component.literal("Did not update any chunks"));
                    }
                    return;
                }
            }

            ChunkAccess chunk = context.getSource().getLevel().getChunk(chunkX, chunkZ);
            if (chunk != null) {
                if (add) {
                    chunk.setInhabitedTime(chunk.getInhabitedTime() + amount);
                } else {
                    chunk.setInhabitedTime(amount);
                }
                chunk.setUnsaved(true);
                count.incrementAndGet();
                if (System.currentTimeMillis() - lastUpdate.get() > 1000L) {
                    lastUpdate.set(System.currentTimeMillis());
                    context.getSource().sendSuccess(Component.literal("Updated %s/%s chunks (%s%%)".formatted(count.get(), totalChunkCount, (int) ((((float) count.get()) / ((float) totalChunkCount)) * 100F))), false);
                }
            }

            server.execute(taskReference.get());
        };

        taskReference.set(task);
        server.execute(task);
        return 1;
    }

}
