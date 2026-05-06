package com.rangelcham.adventuremod.custom.effect;

import com.rangelcham.adventuremod.abilities.dash.DashHandler;
import com.rangelcham.adventuremod.nbt.PlayerEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DashEffect extends MobEffect {
    public DashEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            DashHandler.unlockedDash = true;
            PlayerEventHandler.save(player);
        }
        return super.applyEffectTick(level, entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}

