package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Inventory GUI with an added Money gui that allows
 * the player to add/take money from their
 * {@link com.ki11erwolf.shoppery.bank.Wallet} through
 * the gui. This GUI acts like an extension to the inventory
 * GUI (like the recipe book) however it is a separate
 * GUI extending the original inventory GUI.
 */
@OnlyIn(Dist.CLIENT)
public class MoneyGui extends GuiInventory {

    /**
     * The texture file containing the money gui
     * texture.
     */
    private static final ResourceLocation MONEY_GUI_TEXTURE
            = new ResourceLocation(ShopperyMod.MODID, "textures/gui/money_gui.png");

    /**
     * Constructs a new money inventory.
     *
     * @param c the player the gui belongs to.
     */
    MoneyGui(EntityPlayer c){
        super(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
    }

    /**
     * {@inheritDoc}
     *
     * Draws the money part of the gui background as well.
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.mc.getTextureManager().bindTexture(MONEY_GUI_TEXTURE);

        int guiWidth = 256;
        int guiHeight = 68;

        if(func_194310_f().isVisible()){
            this.drawTexturedModalRect(
                    this.guiLeft + 10 - (guiWidth / 2),
                    this.guiTop - guiHeight - 4,
                    0, 0,
                    guiWidth, guiHeight
            );
        } else {
            this.drawTexturedModalRect(
                    this.guiLeft + (this.xSize / 2) - (guiWidth / 2),
                    this.guiTop - guiHeight - 4,
                    0, 0,
                    guiWidth, guiHeight
            );
        }
    }
}