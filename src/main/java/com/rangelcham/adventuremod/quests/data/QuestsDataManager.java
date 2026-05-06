package com.rangelcham.adventuremod.quests.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rangelcham.adventuremod.quests.model.Quest;
import com.rangelcham.adventuremod.quests.model.QuestType;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestsDataManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<Quest>>(){}.getType();

    private File file;

    public List<Quest> quests = new ArrayList<>();

    public void init() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getSingleplayerServer() == null) return;

        File worldFolder = mc.gameDirectory;
        String worldName = mc.getSingleplayerServer()
                .getWorldData()
                .getLevelName();

        this.file = new File(worldFolder,
                "saves/" + worldName + "/data/adventuremod/quests.json");

        loadOrCreate();
    }

    private void loadOrCreate() {

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            quests = getDefaultQuests();
            save();
            return;
        }

        load();
    }

    public void load() {
        try (Reader reader = new FileReader(file)) {
            List<Quest> loaded = GSON.fromJson(reader, LIST_TYPE);

            if (loaded != null) {
                quests = loaded;
            }

        } catch (Exception e) {
            e.printStackTrace();
            quests = getDefaultQuests();
        }
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(quests, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Quest> getDefaultQuests() {
        return new ArrayList<>(List.of(
                new Quest("main_1", false, true, 1,
                        "Una extraña energía emana de la torre en ruinas al norte del bosque.",
                        QuestType.PRINCIPAL,
                        "La Torre Oscura",
                        new ArrayList<>(List.of(
                                "Habla con el anciano del pueblo",
                                "Encuentra la torre en el bosque",
                                "Explora el interior"
                        )),
                        10, 0, 0
                )
        ));
    }

    public List<Quest> getAll() {
        return quests;
    }

    public Map<QuestType, List<Quest>> getInOrder(List<QuestType> activeFilters) {
        Map<QuestType, List<Quest>> inOrder = new HashMap<>();

        for (Quest quest : quests) {
            if (!quest.active) continue;
            if (!activeFilters.contains(quest.type)) continue;

            inOrder
                .computeIfAbsent(quest.type, k -> new ArrayList<>())
                .add(quest);
        }

        return inOrder;
    }

    public Quest get(String id) {
        for (Quest quest : quests) {
            if (quest.id.equals(id)) {
                return quest;
            }
        }
        return null;
    }

    public void update(String id, Quest questUpdated) {
        for (Quest quest : quests) {
            if (quest.id.equals(id)) {
                quest.completed = questUpdated.completed;
                quest.currentStep = questUpdated.currentStep;
                quest.active = questUpdated.active;
            }
        }
    }
}
