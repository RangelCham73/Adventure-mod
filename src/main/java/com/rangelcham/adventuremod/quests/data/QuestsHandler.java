package com.rangelcham.adventuremod.quests.data;

import com.rangelcham.adventuremod.AdventureMod;
import com.rangelcham.adventuremod.nbt.PlayerEventHandler;
import com.rangelcham.adventuremod.quests.QuestKeyBind;
import com.rangelcham.adventuremod.quests.QuestScreen;
import com.rangelcham.adventuremod.quests.model.Quest;
import com.rangelcham.adventuremod.quests.model.QuestType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = AdventureMod.MODID)
public class QuestsHandler {

    public static QuestsDataManager quests = new QuestsDataManager();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        // Solo corre en el cliente
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        if (QuestKeyBind.QUESTS_OPEN_SCREEN.isDown() && mc.screen == null) {
            QuestScreen screen = new QuestScreen();
            var filters = screen.activeFilters;
            filters.remove(QuestType.COMPLETE);
            screen.setMissions(quests.getInOrder(filters));
            mc.setScreen(screen);
        }
    }

    public static void doStep(CommandSourceStack command, String id, int step) {
        Quest quest = quests.get(id);
        if (quest != null) {
            if (quest.active && step > 0) {
                quest.currentStep = step;
                if (quest.currentStep == quest.steps.size()) {
                    completeQuest(quest);
                }
            } else {
                quest.active = true;
            }
            PlayerEventHandler.save(command.getPlayer());
        }
    }

    private static void completeQuest(Quest quest) {
        quest.completed = true;
        quest.type = QuestType.COMPLETE;
    }
}
