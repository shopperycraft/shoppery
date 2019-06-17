package com.ki11erwolf.shoppery.gui;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MoneyGui extends GuiInventory {

    public MoneyGui(EntityPlayer c){
        super(c);
    }
}