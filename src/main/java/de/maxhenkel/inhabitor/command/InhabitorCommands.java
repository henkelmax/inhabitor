package de.maxhenkel.inhabitor.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.chunk.LevelChunk;

public class InhabitorCommands {

    public static final String INHABITOR_COMMAND = "inhabitor";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal(INHABITOR_COMMAND).requires(stack -> stack.hasPermission(2));

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
        ColumnPos from = ColumnPosArgument.getColumnPos(context, "from");
        ColumnPos to = ColumnPosArgument.getColumnPos(context, "to");

        int fromX = Math.min(from.x(), to.x());
        int fromZ = Math.min(from.z(), to.z());
        int toX = Math.max(from.x(), to.x());
        int toZ = Math.max(from.z(), to.z());

        int chunkCount = 0;
        for (int x = fromX; x <= toX; x++) {
            for (int z = fromZ; z <= toZ; z++) {
                LevelChunk chunk = context.getSource().getLevel().getChunk(x, z);
                if (chunk != null) {
                    if (add) {
                        chunk.setInhabitedTime(chunk.getInhabitedTime() + IntegerArgumentType.getInteger(context, "amount"));
                    } else {
                        chunk.setInhabitedTime(IntegerArgumentType.getInteger(context, "amount"));
                    }
                    chunk.setUnsaved(true);
                    chunkCount++;
                }
            }
        }

        if (chunkCount > 0) {
            context.getSource().sendSuccess(Component.literal("Successfully updated inhabitedTime for %s chunks".formatted(chunkCount)), false);
        } else {
            context.getSource().sendFailure(Component.literal("Did not update any chunks"));
        }

        return 1;
    }

}
