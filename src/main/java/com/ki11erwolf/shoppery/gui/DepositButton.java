package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.packets.InventoryDepositPacket;
import com.ki11erwolf.shoppery.packets.Packet;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

/**
 * The Deposit button, added to the {@link ShopperyInventoryScreen}
 * that sends an {@link InventoryDepositPacket} to the server when
 * pressed.
 */
class DepositButton extends ImageButton {

    /**
     * X and Y starting positions of the button.
     */
    private static final int POSITION_X = 119, POSITION_Y = 36;

    /**
     * The width and height of the button.
     */
    private static final int WIDTH = 20, HEIGHT = 18;

    /**
     * Coordinates on the texture map where the buttons
     * texture starts.
     */
    private static final int TEXTURE_POSITION_X = 68, TEXTURE_POSITION_Y = 0;

    /**
     * Constructs a new Deposit Button with the given X and Y
     * starting positions.
     *
     * @param player the player viewing this button.
     * @param inventoryX X starting position.
     * @param inventoryY Y starting position.
     */
    public DepositButton(final PlayerEntity player, int inventoryX, int inventoryY) {
        super(
                inventoryX + POSITION_X, inventoryY + POSITION_Y,
                WIDTH, HEIGHT, TEXTURE_POSITION_X, TEXTURE_POSITION_Y,
                HEIGHT + 1, ShopperyButton.BUTTON_TEXTURES,
                (button) -> sendPacket(player.getUniqueID())
        );
    }

    /**
     * Sends the {@link InventoryDepositPacket} to the server.
     *
     * @param playerUUID the player sending the packet.
     */
    private static void sendPacket(UUID playerUUID){
        Packet.send(PacketDistributor.SERVER.noArg(), new InventoryDepositPacket(playerUUID.toString()));
    }

    /**
     * {@inheritDoc}
     * Extended to provide tooltip rendering.
     */
    @Override
    public void renderButton(int x, int y, float z) {
        super.renderButton(x, y, z);

        if(this.isHovered)
            drawTooltip(x, y);
    }

    /**
     * Draws the buttons tooltip on screen.
     *
     * @param x mouse x position.
     * @param y mouse y position.
     */
    private void drawTooltip(int x, int y){
        drawCenteredString(Minecraft.getInstance().fontRenderer,
                LocaleDomains.TOOLTIP.sub(LocaleDomains.BUTTON).get("deposit"),
                this.x + (width - 56),
                this.y + (height / 2) - 4,
                0XF8F8F8
        );
    }
}