package com.ki11erwolf.shoppery;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * List of sound effects added & used by the Shoppery
 * mod.
 */
public final class ShopperySoundEvents {

    /**
     * The cash withdraw sound effect. Used when taking money from the
     * wallet/money gui.
     */
    public static final SoundEvent WITHDRAW = new SoundEvent(new ResourceLocation(
            ShopperyMod.MODID, "withdraw"
    ));

    /**
     * The cash deposit sound effect. Used when adding money to the
     * wallet/money gui.
     */
    public static final SoundEvent DEPOSIT = new SoundEvent(new ResourceLocation(
            ShopperyMod.MODID, "deposit"
    ));

    /**
     * The cash transaction sound effect. Used when a shop has sold/purchased
     * an item.
     */
    public static final SoundEvent TRANSACTION = new SoundEvent(new ResourceLocation(
            ShopperyMod.MODID, "transaction"
    ));

    /**
     * Private constructor.
     */
    private ShopperySoundEvents(){}

}
