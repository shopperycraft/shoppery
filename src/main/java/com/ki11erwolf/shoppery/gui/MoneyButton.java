package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.config.ShopperyConfig;
import com.ki11erwolf.shoppery.config.categories.General;
import com.ki11erwolf.shoppery.item.CoinItem;
import com.ki11erwolf.shoppery.item.NoteItem;
import com.ki11erwolf.shoppery.item.ShopperyItem;
import com.ki11erwolf.shoppery.packets.*;
import com.ki11erwolf.shoppery.util.WaitTimer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * A button that represents a clickable Currency
 * Item within the {@link ShopperyInventoryScreen}.
 *
 * <p/>The button will withdraw currency item
 * from the players wallet when clicked.
 */
@OnlyIn(Dist.CLIENT)
public class MoneyButton extends Button {

    /**
     * Timer to prevent spamming the server with
     * balance update requests.
     */
    private static final WaitTimer BALANCE_REQ_TIMER
            = new WaitTimer(ShopperyConfig.GENERAL_CONFIG.getCategory(General.class).getPacketWaitTime());

    /**
     * The height and width of the button.
     */
    private static final int SIZE = 16;//px

    /**
     * The number of key animation frames
     * in the animations render cycle.
     */
    private static final int FRAMES = 4;

    /**
     * The number of animation key frames cycled
     * through and played in a second.
     */
    private static final int FRAMES_PER_SECOND = 6;

    /**
     * The currency item this Money Button represents.
     */
    private final ShopperyItem<?> currencyItem;

    /**
     * The unique ID of the player that's
     * interacting with this button.
     */
    private final UUID playerUUID;

    /**
     * The texture of the currency item.
     */
    private final ResourceLocation itemTextureResource;

    /**
     * Cached player balance.
     */
    private long balance;

    /**
     * Cached player cents balance.
     */
    private byte cents;

    /**
     * The current animation key frame in the
     * animation render cycle.
     */
    private int frame = 0;

    /**
     * The amount of time (in milliseconds) that has passed
     * since the last key frame.
     */
    private int renderTime;

    /**
     * Creates a new CurrencyItem representation
     * as a button.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param currencyItem the currency item to represent.
     */
    public MoneyButton(int x, int y, ShopperyItem<?> currencyItem, PlayerEntity player) {
        super(x, y, SIZE, SIZE, "", (bool) -> {/*Done by override*/});
        this.playerUUID = player.getUniqueID();
        this.updateBalance();

        if(!(currencyItem instanceof NoteItem) && !(currencyItem instanceof CoinItem))
            throw new IllegalArgumentException("Provided item is not a type of coin or note currency");
        else this.currencyItem = currencyItem;

        //noinspection ConstantConditions
        this.itemTextureResource = new ResourceLocation("shoppery",
                "textures/item/" + currencyItem.getRegistryName().getPath() + ".png"
        );
    }

    /**
     * Requests the server send the players updated balance,
     * provided enough time has passed since the last request.
     *
     * <p/>Also sets {@link #balance} & {@link #cents} to the
     * updated balance.
     */
    private void updateBalance(){
        BALANCE_REQ_TIMER.time((x) -> {
            Packet.send(PacketDistributor.SERVER.noArg(),
                    new PlayerBalanceReqPacket(playerUUID.toString())
            );

            Packet.send(PacketDistributor.SERVER.noArg(),
                    new PlayerCentsReqPacket(playerUUID.toString())
            );

            return null;
        });

        this.balance = PlayerBalanceRecPacket.getLastReceivedBalance();
        this.cents = PlayerCentsRecPacket.getLastReceivedBalance();
    }

    /**
     * Determines if the players balance is sufficient
     * to purchase this currency.
     *
     * @return {@code true} if and only if the players
     * wallet contains enough funds to purchase this
     * coin/note.
     */
    private boolean affordable(){
        if(balance >= 1){
            if(currencyItem instanceof CoinItem)
                return true;
            else return balance >= ((NoteItem)currencyItem).getWorth();
        } else {
            if(currencyItem instanceof NoteItem)
                return false;
            else return cents >= ((CoinItem)currencyItem).getWorth();
        }
    }

    /**
     * Called when the button is clicked by the player.
     * Sends a {@link com.ki11erwolf.shoppery.packets.MoneyWithdrawPacket}
     * in an attempt to withdraw the requested amount.
     */
    @Override
    public void onPress(){
        Packet.send(PacketDistributor.SERVER.noArg(),
                ((currencyItem instanceof NoteItem)
                    ? new MoneyWithdrawPacket(playerUUID.toString(), true, ((NoteItem)currencyItem).getWorth())
                    : new MoneyWithdrawPacket(playerUUID.toString(), false, ((CoinItem)currencyItem).getWorth())
                )
        );
    }

    /**
     * Handles the drawing and rendering of the button
     * on screen.
     *
     * <p/>Renders the Currency Items icon as an image
     * for the button.
     *
     * @param mouseX mouse X coordinate
     * @param mouseY mouse Y coordinate.
     * @param fps the amount of time (in milliseconds)
     *            the frame took render.
     */
    @Override
    public void renderButton(int mouseX, int mouseY, float fps) {
        //Balance
        updateBalance();
        boolean affordable = this.affordable();

        //Texture
        Minecraft.getInstance().getTextureManager().bindTexture(itemTextureResource);
        RenderSystem.disableDepthTest();

        //Hover animation
        Vector3f animation;
        if(isHovered && affordable){
            stepAnimationRenderer(fps);
            animation = getHoverAnimationPath(frame);
        } else {
            resetAnimationRenderer();
            animation = new Vector3f(0, 0, 0);
        }

        int x = this.x + (int) animation.getX();
        int y = this.y + (int) animation.getY();

        //Draw
        blit(x, y, (float)0, (float)0, 16, 16, 16, 16);
        RenderSystem.enableDepthTest();

        //Overlay
        renderOverlays(x, y);
    }

    /**
     * Handles rendering the additional lock overlay on top
     * of the button if the player cannot afford the coin
     * or note.
     *
     * @param x the x position at which to start rendering.
     * @param y the y position at which to start rendering.
     */
    protected void renderOverlays(int x, int y){
        if(!affordable()){
            Minecraft.getInstance().getTextureManager().bindTexture(ShopperyInventoryScreen.SHOPPERY_GUIS);
            RenderSystem.disableDepthTest();

            blit(x, y, 0, 65, 16, 16);
            RenderSystem.enableDepthTest();
        }
    }

    /**
     * Resets the animation render cycle
     * and animation key frame.
     */
    private void resetAnimationRenderer(){
        renderTime = 0;
        frame = 0;
    }

    /**
     * Calculates the current animation key
     * frame based on the amount of time passed
     * during renders.
     *
     * <p/>Effectively steps through the
     * animation render cycle, in sync
     * with the frame rate.
     *
     * <p/>Called each render cycle, but only
     * when the animation(s) are supposed to
     * be playing.
     *
     * @param fps the amount of time (in milliseconds)
     *            the frame took render.
     */
    private void stepAnimationRenderer(float fps){
        if(renderTime >= (1000 / FRAMES_PER_SECOND)){
            renderTime = 0;
            if(frame >= FRAMES - 1)
                frame = 0;
            else frame++;
        } else renderTime += fps * 100;
    }

    /**
     * Used to get a vector translating the rendering
     * start position based on a key frame in a loop.
     *
     * <p/>This allows creating an animation to render
     * in a repeatable loop.
     *
     * @param frame the frame number in the render cycle.
     * @return a vector describing the coordinates to add
     * to the renders coordinates.
     */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    protected Vector3f getHoverAnimationPath(int frame){
        switch (frame + 1){
            case 1: return new Vector3f(0, -1 ,0);
            case 2: return new Vector3f(0, 0, 0);
            case 3: return new Vector3f(0, 1 ,0);
            case 4: return new Vector3f(0, 0, 0);
            default: return new Vector3f(0, -1, 0);
        }
    }

    /**
     * Replaces the default button press sounds
     * with {@link ShopperySoundEvents#WITHDRAW}.
     */
    @Override
    @ParametersAreNonnullByDefault
    public void playDownSound( SoundHandler soundHandler) {
        if(affordable())
            soundHandler.play(SimpleSound.master(ShopperySoundEvents.WITHDRAW,
                    RandomUtils.nextFloat(1.2F, 1.4F), 0.50F
            ));
    }
}
