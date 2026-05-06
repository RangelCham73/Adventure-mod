package com.rangelcham.adventuremod.quests.model;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    public final String id;
    public boolean completed;
    public boolean active;
    public int currentStep;
    public final String description;
    public QuestType type;
    public final String title;
    public final List<String> steps;
    public final int gold;
    public final int silver;
    public final int copper;

    public Quest(String id, boolean completed, boolean active, int currentStep, String description, QuestType type, String title, List<String> steps, int gold, int silver, int copper) {
        this.id = id;
        this.completed = completed;
        this.active = active;
        this.currentStep = currentStep;
        this.description = description;
        this.type = type;
        this.title = title;
        this.steps = new ArrayList<>(steps);
        this.gold = gold;
        this.silver = silver;
        this.copper = copper;
    }
}
