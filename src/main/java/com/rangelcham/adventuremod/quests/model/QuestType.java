package com.rangelcham.adventuremod.quests.model;

public enum QuestType {
    PRINCIPAL("PRINCIPAL"),
    SECUNDARY("SECUNDARIA"),
    HUNT("CAZA"),
    TRESURE("TESORO"),
    TASK("TAREA"),
    COMPLETE("COMPLETADO");

    private final String displayName;

    QuestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
