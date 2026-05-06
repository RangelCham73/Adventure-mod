package com.rangelcham.adventuremod.custom.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.rangelcham.adventuremod.quests.data.QuestsHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class QuestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal("quest")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(
                            Commands.argument("id", StringArgumentType.string())
                                .then(
                                    Commands.argument("step", IntegerArgumentType.integer(0 , 10))
                                    .executes(cs -> execute(
                                            cs.getSource(),
                                            StringArgumentType.getString(cs, "id"),
                                            IntegerArgumentType.getInteger(cs, "step")
                                    ))
                                )
                        )
        );
    }
    private static int execute(CommandSourceStack command, String id, int step) {
        QuestsHandler.doStep(command, id, step);
        command.sendSuccess(() -> Component.literal(id + " " + step), true);
        return Command.SINGLE_SUCCESS;
    }
}
