package com.rangelcham.adventuremod.nbt;

import com.mojang.brigadier.CommandDispatcher;
import com.rangelcham.adventuremod.AdventureMod;
import com.rangelcham.adventuremod.abilities.dash.DashHandler;
import com.rangelcham.adventuremod.abilities.doublejump.DoubleJumpHandler;
import com.rangelcham.adventuremod.custom.command.QuestCommand;
import com.rangelcham.adventuremod.quests.data.QuestsHandler;
import com.rangelcham.adventuremod.quests.model.Quest;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import java.util.Optional;

@EventBusSubscriber(modid = AdventureMod.MODID)
public class PlayerEventHandler {
    private static final String INFO_KEY = "info";
    private static final String ACTIVE_KEY = "active";
    private static final String COMPLETED_KEY = "completed";
    private static final String STEP_KEY = "currentStep";
    private static final String DOUBLE_JUMP_KEY = "hasDoubleJump";
    private static final String DASH_KEY = "hasDash";

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        QuestsHandler.quests.init();
        if ((event.getEntity() instanceof ServerPlayer player)) {
            load(player);
        }
        if ((event.getEntity() instanceof LocalPlayer player)) {// debug
            load(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(EntityLeaveLevelEvent event) {
        if ((event.getEntity() instanceof LocalPlayer player)) {
            save(player);
        }
        if ((event.getEntity() instanceof ServerPlayer player)) {// debug
            save(player);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        QuestCommand.register(dispatcher);
    }

    public static void save(Player player) {
        CompoundTag tag = player.getPersistentData();
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean(DOUBLE_JUMP_KEY, DoubleJumpHandler.unlockedDoubleJump);
        compoundTag.putBoolean(DASH_KEY, DashHandler.unlockedDash);

        for (Quest quest : QuestsHandler.quests.getAll()) {
            CompoundTag questTag = new CompoundTag();
            questTag.putBoolean(ACTIVE_KEY, quest.active);
            questTag.putBoolean(COMPLETED_KEY, quest.completed);
            questTag.putInt(STEP_KEY, quest.currentStep);

            compoundTag.put(quest.id, questTag);

        }
        tag.put(INFO_KEY, compoundTag);
        player.getPersistentData().put(INFO_KEY, compoundTag);
    }

    public static void load(Player player) {
        CompoundTag tag = player.getPersistentData();
        Optional<CompoundTag> optional = tag.getCompound(INFO_KEY);
        if (optional.isPresent()) {
            player.sendSystemMessage(Component.literal("NBT de habilidades: " + optional));

            CompoundTag abilityTagCopy = optional.get();
            if (abilityTagCopy.contains(DOUBLE_JUMP_KEY)) {
                Optional<Boolean> doubleJump = abilityTagCopy.getBoolean(DOUBLE_JUMP_KEY);
                if (doubleJump.isPresent() && doubleJump.get()) {
                    DoubleJumpHandler.unlockedDoubleJump = true;
                }
            }
            if (abilityTagCopy.contains(DASH_KEY)) {
                Optional<Boolean> dash = abilityTagCopy.getBoolean(DASH_KEY);
                if (dash.isPresent() && dash.get()) {
                    DashHandler.unlockedDash = true;
                }
            }

            CompoundTag questsTag = optional.get();
            for (String key : questsTag.keySet()) {
                Optional<CompoundTag> questTag = questsTag.getCompound(key);
                Quest quest = QuestsHandler.quests.get(key);

                if (quest != null && questTag.isPresent()) {
                    CompoundTag questTagCopy = optional.get();
                    questTagCopy.getBoolean(ACTIVE_KEY).ifPresent(value -> quest.active = value);
                    questTagCopy.getBoolean(COMPLETED_KEY).ifPresent(value -> quest.completed = value);
                    questTagCopy.getInt(STEP_KEY).ifPresent(value -> quest.currentStep = value);

                    QuestsHandler.quests.update(key, quest);
                }
            }
        }
    }
}