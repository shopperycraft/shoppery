package com.ki11erwolf.shoppery;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * List of sound effects added & used by the Shoppery
 * mod.
 */
public final class ShopperySoundEvents {

    /**
     * The money sound effect. Used when taking money from the
     * wallet/money gui.
     */
    public static final SoundEvent WITHDRAW = new SoundEvent(new ResourceLocation(
            ShopperyMod.MODID, "withdraw"
    ));

    /**
     * Private constructor.
     */
    private ShopperySoundEvents(){}

}
