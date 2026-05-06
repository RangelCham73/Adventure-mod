package com.rangelcham.adventuremod.quests;

import com.rangelcham.adventuremod.quests.data.QuestsHandler;
import com.rangelcham.adventuremod.quests.model.Quest;
import com.rangelcham.adventuremod.quests.model.QuestType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;

import java.util.*;

public class QuestScreen extends Screen {
    private int scrollOffset = 0;

    private final List<Component> missionLines = new ArrayList<>();
    private final Map<QuestType, Button> filterButtons = new HashMap<>();
    public List<QuestType> activeFilters = new ArrayList<>(List.of(QuestType.values()));

    private static final List<QuestType> QUEST_ORDER = List.of(
        QuestType.PRINCIPAL,
            QuestType.SECUNDARY,
            QuestType.TASK,
            QuestType.HUNT,
            QuestType.TRESURE,
            QuestType.COMPLETE
    );

    private static final int LINE_HEIGHT = 10;
    private static final int PADDING_TOP = 20;
    private static final int BOX_WIDTH = 42;

    // Colores constantes
    private static final int COLOR_TITLE = 0xE5C07B;       // dorado suave
    private static final int COLOR_MAIN_QUEST = 0xE5C07B;  // dorado (historia principal)
    private static final int COLOR_SIDE_QUEST = 0x61AFEF;  // azul (misiones secundarias)
    private static final int COLOR_HUNT = 0xE06C75;        // rojo suave (caza / combate)
    private static final int COLOR_TREASURE = 0xD19A66;    // naranja (exploración / loot)
    private static final int COLOR_TASK = 0x98C379;        // verde (tareas simples)
    private static final int COLOR_DESC = 0xABB2BF;        // gris claro
    private static final int COLOR_LABEL = 0x5C6370;       // gris oscuro
    private static final int COLOR_STEP_DONE = 0x98C379;   // verde suave
    private static final int COLOR_STEP_TODO = 0x7F848E;   // gris neutro
    private static final int COLOR_COMPLETE = 0xC678DD;    // morado épico
    private static final int COLOR_WHITE = 0XFFFFFF;
    private static final int COLOR_BLACK = 0x777777;
    private static final int COLOR_GOLD = 0XFFF300;
    private static final int COLOR_SILVER = 0XD6D6D6;
    private static final int COLOR_COPPER = 0XFF9D36;

    public QuestScreen() {
        super(Component.literal("Misiones"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int x = 20;

        activeFilters.remove(QuestType.COMPLETE);

        // FILTROS (UI fija)
        for (QuestType type : QuestType.values()) {

            Button btn = Button.builder(Component.literal(type.getDisplayName()), b -> {
                        toggleFilter(type);
                        refreshMissions();
                        updateFilterButtons();
                    })
                    .pos(x, 20)
                    .size(90, 20)
                    .build();

            filterButtons.put(type, btn);
            this.addRenderableWidget(btn);

            boolean active = activeFilters.contains(type);

            btn.setMessage(Component.literal(type.getDisplayName())
                    .withStyle(active
                            ? Style.EMPTY.withColor(TextColor.fromRgb(COLOR_WHITE))
                            : Style.EMPTY.withColor(TextColor.fromRgb(COLOR_BLACK))));

            x += 95;
        }

        // BOTÓN CERRAR
        this.addRenderableWidget(
                Button.builder(Component.literal("Cerrar"), b -> this.onClose())
                        .pos(centerX - 50, this.height - 30)
                        .size(100, 20)
                        .build()
        );
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= verticalAmount * 10;

        int contentHeight = missionLines.size() * LINE_HEIGHT;
        int visibleHeight = this.height - PADDING_TOP - 60;
        int maxScroll = Math.max(0, contentHeight - visibleHeight + 20);

        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int top = PADDING_TOP + 30;
        int bottom = this.height - 40;

        graphics.enableScissor(0, top, this.width, bottom);

        int y = top - scrollOffset;

        for (Component line : missionLines) {

            graphics.drawScrollingString(
                    graphics.textRenderer(),
                    this.font,
                    line,
                    0,
                    this.width,
                    y
            );

            y += LINE_HEIGHT;
        }

        graphics.disableScissor();
    }

    private void toggleFilter(QuestType type) {
        if (activeFilters.contains(type)) {
            activeFilters.remove(type);
        } else {
            activeFilters.add(type);
        }
    }

    private void refreshMissions() {
        Map<QuestType, List<Quest>> all = QuestsHandler.quests.getInOrder(activeFilters); // o tu fuente real
        setMissions(all);
    }

    private void updateFilterButtons() {
        for (QuestType type : QuestType.values()) {
            Button btn = filterButtons.get(type);
            if (btn == null) continue;

            boolean active = activeFilters.contains(type);

            btn.setMessage(Component.literal(type.getDisplayName())
                    .withStyle(active
                            ? Style.EMPTY.withColor(TextColor.fromRgb(COLOR_WHITE))
                            : Style.EMPTY.withColor(TextColor.fromRgb(COLOR_BLACK))));
        }
    }

    public void setMissions(Map<QuestType, List<Quest>> quests) {
        missionLines.clear();
        for (QuestType type : QUEST_ORDER) {
            List<Quest> questList = quests.get(type);
            if (questList == null || questList.isEmpty()) continue;

            int colorType;
            switch (type) {
                case PRINCIPAL: colorType = COLOR_MAIN_QUEST; break;
                case SECUNDARY: colorType = COLOR_SIDE_QUEST; break;
                case HUNT: colorType = COLOR_HUNT; break;
                case TRESURE: colorType = COLOR_TREASURE; break;
                case TASK: colorType = COLOR_TASK; break;
                case COMPLETE: colorType = COLOR_COMPLETE; break;
                default: colorType = COLOR_DESC; break;
            }
            Style style = Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(colorType));
            String header = "╔═══ " + type.getDisplayName() + " ";
            int headerCount = header.length();
            header += repeat('═', BOX_WIDTH - headerCount);

            // Tipo
            missionLines.add(Component.literal(header).withStyle(style));
            missionLines.add(Component.literal("║").withStyle(style));

            for (Quest quest : questList) {
                //Titulo
                missionLines.add(Component.literal("║").withStyle(style)
                            .append(Component.literal("  ✦ " + quest.title)
                                .withStyle(Style.EMPTY
                                    .withBold(true)
                                    .withColor(TextColor.fromRgb(COLOR_TITLE)))));

                // Descripcion
                missionLines.add(Component.literal("║").withStyle(style)
                            .append(Component.literal("    "  + quest.description)
                                .withStyle(Style.EMPTY
                                    .withItalic(true)
                                    .withColor(TextColor.fromRgb(COLOR_DESC)))));

                // Barra de progreso
                int total = quest.steps.size();
                int done = quest.currentStep;

                String bar = "    [" +
                        "█".repeat(done) +
                        "░".repeat(Math.max(0, total - done)) +
                        "]";

                missionLines.add(Component.literal("║").withStyle(style)
                            .append(Component.literal(bar)
                                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(COLOR_LABEL)))));

                // Pasos
                for (int i = 0; i < quest.steps.size(); i++) {
                    boolean isDone = i < quest.currentStep;
                    String prefix = isDone ? "✔ " : "• ";
                    int color = isDone ? COLOR_STEP_DONE : COLOR_STEP_TODO;

                    missionLines.add(Component.literal("║").withStyle(style)
                                .append(Component.literal("      " + prefix + " " + quest.steps.get(i))
                                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)))));
                }

                // Recompensa
                missionLines.add(buildReward(quest, style));
                missionLines.add(Component.literal("║").withStyle(style));
            }
            String footer = "╚" + repeat('═', BOX_WIDTH - 5);
            missionLines.add(Component.literal(footer).withStyle(style));
        }
    }

    private Component buildReward(Quest quest, Style style) {
        Style rewardStyle = Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(COLOR_WHITE));

        Component gold = Component.literal(String.valueOf(quest.gold)).withStyle(rewardStyle)
            .append(" 🟠 ").withStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(COLOR_GOLD)));
        Component silver = Component.literal(String.valueOf(quest.silver)).withStyle(rewardStyle)
            .append(" 🟠 ").withStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(COLOR_SILVER)));
        Component copper = Component.literal(String.valueOf(quest.copper)).withStyle(rewardStyle)
            .append(" 🟠 ").withStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(COLOR_COPPER)));

        return Component.literal("║     ").withStyle(style)
                          .append(gold)
                          .append(silver)
                          .append(copper);
    }

    private String repeat(char c, int count) {
        return String.valueOf(c).repeat(count);
    }
}
