package com.rangelcham.adventuremod.custom.item;

import com.rangelcham.adventuremod.AdventureMod;
import com.rangelcham.adventuremod.custom.block.ModBlocks;
import com.rangelcham.adventuremod.custom.effect.ModEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AdventureMod.MODID);

    public static final DeferredItem<Item> DOUBLEJUMP_CRYSTAL = ITEMS.register(
            "jcrystal",
            name -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, name))
                    .component(
                        DataComponents.CONSUMABLE,
                        Consumable.builder()
                            .consumeSeconds(2f)
                            .animation(ItemUseAnimation.EAT)
                            .sound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_CLUSTER_BREAK))
                            .soundAfterConsume(SoundEvents.BREEZE_WIND_CHARGE_BURST)
                            .hasConsumeParticles(false)
                            .onConsume(
                                new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.DOUBLEJUMP_EFFECT, 1, 0))
                            )
                            .build()
                    )
            )
    );

    public static final DeferredItem<Item> DASH_CRYSTAL = ITEMS.register(
            "dcrystal",
            name -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, name))
                    .component(
                        DataComponents.CONSUMABLE,
                        Consumable.builder()
                            .consumeSeconds(2f)
                            .animation(ItemUseAnimation.EAT)
                            .sound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_CLUSTER_BREAK))
                            .soundAfterConsume(SoundEvents.BREEZE_WIND_CHARGE_BURST)
                            .hasConsumeParticles(false)
                            .onConsume(
                                    new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.DASH_EFFECT, 1, 0))
                            )
                            .build()
                    )
            )
    );

    public static final DeferredItem<Item> FUSION_SHARD = ITEMS.register(
            "fusion_shard",
            name -> new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, name))
                    .component(
                            DataComponents.CONSUMABLE,
                            Consumable.builder()
                                    .consumeSeconds(2f)
                                    .animation(ItemUseAnimation.EAT)
                                    .sound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_CLUSTER_BREAK))
                                    .soundAfterConsume(SoundEvents.BREEZE_WIND_CHARGE_BURST)
                                    .hasConsumeParticles(false)
                                    .onConsume(
                                            new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.FUSION_SHARD_EFFECT, 1, 0))
                                    )
                                    .build()
                    )
            )
    );

    public static final DeferredItem<Item> BLUECRYSTAL_BLOCK_ITEM = ITEMS.register("bluecrystal_block",
            name -> new BlockItem(ModBlocks.BLUE_CRYSTAL.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, name))));

    public static final DeferredItem<Item> GREENCRYSTAL_BLOCK_ITEM = ITEMS.register("greencrystal_block",
            name -> new BlockItem(ModBlocks.GREEN_CRYSTAL.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, name))));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
