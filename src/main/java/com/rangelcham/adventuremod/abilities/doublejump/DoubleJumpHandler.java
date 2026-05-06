package com.rangelcham.adventuremod.abilities.doublejump;

import com.rangelcham.adventuremod.AdventureMod;
import net.minecraft.client.Minecraft;
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
public class DoubleJumpHandler {

    private static boolean isInAir = false;
    private static boolean canDoubleJump = false;
    private static boolean jumpReleased = true;
    private static boolean preventFallDamage = false;
    public static boolean unlockedDoubleJump = false;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!unlockedDoubleJump || !(event.getEntity() instanceof LocalPlayer player)) return;

        boolean isJumpKeyDown = Minecraft.getInstance().options.keyJump.isDown();

        if (player.onGround()) {
            resetStateOnGround(player);
        } else if (!isInAir) {
            isInAir = true;
            canDoubleJump = true;
            jumpReleased = false;
        }

        if (!isJumpKeyDown) {
            jumpReleased = true;
        } else if (canDoubleJump && jumpReleased) {
            performDoubleJump(player);
            canDoubleJump = false;
            preventFallDamage = true;
            player.fallDistance = 0.0F;
        }
    }

    private static void resetStateOnGround(LocalPlayer player) {
        isInAir = false;
        canDoubleJump = false;
        jumpReleased = true;
        if (preventFallDamage) {
            player.fallDistance = 0.0F;
            preventFallDamage = false;
        }
    }

    private static void performDoubleJump(LocalPlayer player) {
        Vec3 currentMotion = player.getDeltaMovement();
        player.setDeltaMovement(currentMotion.x, 0.6, currentMotion.z);
        player.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 1.0F, 1.0F);

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        for (int i = 0; i < 25; i++) {
            double dx = (Math.random() - 0.5) * 0.2;
            double dz = (Math.random() - 0.5) * 0.2;

            player.level().addParticle(ParticleTypes.END_ROD, x, y, z, dx, 0.2, dz);
            player.level().addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.AMETHYST_CLUSTER.defaultBlockState()),
                    x, y, z, dx, 0.2, dz
            );
        }
    }
}
