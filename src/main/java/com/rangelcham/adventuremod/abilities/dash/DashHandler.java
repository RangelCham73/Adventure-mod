package com.rangelcham.adventuremod.abilities.dash;

import com.rangelcham.adventuremod.AdventureMod;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = AdventureMod.MODID)
public class DashHandler {

    private static final int DASH_COOLDOWN_TICKS = 40; // 2 segundos (40 ticks)
    private static int dashCooldown = 0;

    public static boolean unlockedDash = false;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!unlockedDash) return;
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        if (dashCooldown > 0) {
            dashCooldown--;
            return;
        }

        if (DashKeybind.DASH_ACTION.isDown()) {
            performDash(player);
            dashCooldown = DASH_COOLDOWN_TICKS;
        }
    }

    private static void performDash(LocalPlayer player) {
        Vec3 lookVec = player.getLookAngle().multiply(1, 0, 1).normalize(); // Solo movimiento horizontal
        Vec3 dashVelocity = lookVec.scale(1.2);
        player.setDeltaMovement(dashVelocity);

        // Sonidos mágicos
        player.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 1.0F, 1.2F);
        player.playSound(SoundEvents.BREEZE_CHARGE, 1.0F, 1.0F);

        // Partículas de efecto
        double px = player.getX();
        double py = player.getY() + 0.5;
        double pz = player.getZ();

        for (int i = 0; i < 25; i++) {
            double offsetX = (Math.random() - 0.5);
            double offsetY = (Math.random() - 0.3);
            double offsetZ = (Math.random() - 0.5);

            // END_ROD mágica
            player.level().addParticle(ParticleTypes.END_ROD,
                    px + offsetX * 2, py + offsetY * 2, pz + offsetZ * 2,
                    lookVec.x * 0.2, 0.0, lookVec.z * 0.2);

            // Fragmentos de amatista
            player.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.AMETHYST_CLUSTER.defaultBlockState()),
                    px + offsetX * 3, py + offsetY * 3, pz + offsetZ * 3,
                    lookVec.x * 5.0, 0.0, lookVec.z * 5.0);
        }
    }
}
