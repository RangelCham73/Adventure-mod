package com.rangelcham.adventuremod.quests;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class QuestKeyBind {
    public static final KeyMapping QUESTS_OPEN_SCREEN = new KeyMapping(
            "key.adventuremod.quests",
            GLFW.GLFW_KEY_J,
            KeyMapping.Category.INVENTORY);

    public static void register(RegisterKeyMappingsEvent event)
    {
        event.register(QUESTS_OPEN_SCREEN);

    }
}
