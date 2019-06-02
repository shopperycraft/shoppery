package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.item.ShopperyItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * The item group (creative tab) for all
 * shoppery items & blocks.
 */
public final class ShopperyItemGroup extends ItemGroup {

    /**
     * Shoppery Item Group singleton instance.
     */
    public static final ShopperyItemGroup INSTANCE
            = new ShopperyItemGroup(ShopperyMod.MODID);

    /**
     * Private constructor.
     *
     * @param label unique name for the item group.
     */
    private ShopperyItemGroup(String label) {
        super(label);
    }

    /**
     * @return the item to display on the item group.
     */
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ShopperyItems.NOTE_ONE_HUNDRED_K);
    }
}
