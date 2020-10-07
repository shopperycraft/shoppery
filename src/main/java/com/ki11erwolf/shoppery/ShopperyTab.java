package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * The item group (creative tab) for all
 * shoppery items & blocks.
 */
public final class ShopperyTab extends ItemGroup {

    /**
     * Shoppery Item Group singleton instance.
     */
    public static final ShopperyTab INSTANCE
            = new ShopperyTab(ShopperyMod.MODID);

    /**
     * Private constructor.
     *
     * @param label unique name for the item group.
     */
    private ShopperyTab(String label) {
        super(label);
    }

    /**
     * @return the item to display on the item group.
     */
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.NOTE_ONE_HUNDRED_K);
    }
}
