package com.ki11erwolf.shoppery;

import net.minecraft.init.Items;
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
    public static final ShopperyItemGroup SHOPPERY_ITEM_GROUP
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
        return new ItemStack(Items.DIAMOND);
    }
}
