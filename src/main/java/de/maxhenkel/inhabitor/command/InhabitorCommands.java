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
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public class InhabitorCommands {

    public static final String INHABITOR_COMMAND = "inhabitor";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal(INHABITOR_COMMAND).requires(stack -> stack.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(Inhabitor.CONFIG.inhabitorCommandPermissionLevel.get()))));

        literalBuilder.then(Commands.literal("add")
                .then(Commands.argument("from", ColumnPosArgument.columnPos())
                        .then(Commands.argument("to", ColumnPosArgument.columnPos())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(context -> addInhabitedTime(context, true))))));

        literalBuilder.then(Commands.literal("set")
                .then(Commands.argument("from", ColumnPosArgument.columnPos())
                        .then(Commands.argument("to", ColumnPosArgument.columnPos())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(context -> addInhabitedTime(context, false))))));

        literalBuilder.then(Commands.literal("get")
                .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                        .executes(context -> {
                            ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                            ChunkAccess chunk = context.getSource().getLevel().getChunk(pos.x, pos.z);
                            if (chunk == null) {
                                context.getSource().sendFailure(Component.literal("DCould not get chunk"));
                                return 0;
                            }
                            context.getSource().sendSuccess(() -> Component.literal("Chunk inhabited time: %s".formatted(chunk.getInhabitedTime())), false);
                            return 1;
                        })));

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

        long lastUpdate = 0L;
        int count = 0;
        for (int chunkZ = fromZ; chunkZ <= toZ; chunkZ++) {
            for (int chunkX = fromX; chunkX <= toX; chunkX++) {
                ChunkAccess chunk = context.getSource().getLevel().getChunk(chunkX, chunkZ);
                if (chunk != null) {
                    if (add) {
                        chunk.setInhabitedTime(chunk.getInhabitedTime() + amount);
                    } else {
                        chunk.setInhabitedTime(amount);
                    }
                    chunk.markUnsaved();
                    count++;
                    long time = System.currentTimeMillis();
                    if (time - lastUpdate > 1000L) {
                        lastUpdate = time;
                        int finalCount = count;
                        context.getSource().sendSuccess(() -> Component.literal("Updated %s/%s chunks (%s%%)".formatted(finalCount, totalChunkCount, (int) ((((float) finalCount) / ((float) totalChunkCount)) * 100F))), false);
                    }
                }
            }
        }

        if (count > 0) {
            int finalCount = count;
            context.getSource().sendSuccess(() -> Component.literal("Successfully updated inhabitedTime for %s chunks".formatted(finalCount)), false);
        } else {
            context.getSource().sendFailure(Component.literal("Did not update any chunks"));
        }

        return 1;
    }

}
