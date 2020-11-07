package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.config.ModConfig;
import com.ki11erwolf.shoppery.config.categories.GeneralConfig;
import com.ki11erwolf.shoppery.item.ICurrencyItem;
import com.ki11erwolf.shoppery.item.ModItem;
import com.ki11erwolf.shoppery.packets.*;
import com.ki11erwolf.shoppery.util.WaitTimer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * A button that represents a clickable Currency
 * Item within the {@link WalletInventoryScreen}.
 *
 * <p/>The button will withdraw currency item
 * from the players wallet when clicked.
 */
@OnlyIn(Dist.CLIENT)
public class WalletMoneySlot extends Widget implements WidgetFix {

    /**
     * Timer to prevent spamming the server with
     * balance update requests.
     */
    private static final WaitTimer BALANCE_REQ_TIMER
            = new WaitTimer(ModConfig.GENERAL_CONFIG.getCategory(GeneralConfig.class).getPacketWaitTime());

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
    private final ModItem<?> currencyItem;

    /**
     * The resource location that locates and identifies
     * this currency items texture.
     */
    private final ResourceLocation itemImageResource;

    /**
     * The unique ID of the player that's
     * interacting with this button.
     */
    private final UUID playerUUID;

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
    public WalletMoneySlot(int x, int y, ModItem<?> currencyItem, PlayerEntity player) {
        super(x, y, SIZE, SIZE, new StringTextComponent(""));
        this.playerUUID = player.getUniqueID();
        this.updateBalance();

        if(currencyItem.getRegistryName() == null)
            throw new NullPointerException("Item registry name cannot be null.");

        if(!(currencyItem instanceof ICurrencyItem)) {
            throw new IllegalArgumentException("Provided item is not a type of currency, coin, or note.");
        }

        this.currencyItem = currencyItem;
        this.itemImageResource = new ResourceLocation("shoppery",
                "textures/item/" + currencyItem.getRegistryName().getPath() + ".png"
        );
    }

    // ######
    // Render
    // ######

    /** Obfuscated {@link #render(MatrixStack, int, int, float)}. */
    @Override @ParametersAreNonnullByDefault
    public void func_230431_b_(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
        this.render(matrix, mouseXPos, mouseYPos, frameTime);
    }

    /**
     * Handles the drawing and rendering of the button
     * on screen.
     *
     * <p/>Renders the Currency Items icon as an image
     * for the button.
     *
     * @param mouseXPos mouse X coordinate
     * @param mouseYPos mouse Y coordinate.
     * @param frameTime the amount of time (in milliseconds)
     *            the frame took render.
     */
    @Override
    public void render(MatrixStack matrixStack, int mouseXPos, int mouseYPos, float frameTime) {
        updateBalance(); //Update and calculate position.
        Vector2f animationOffset = this.getHoverAnimationOffset(this.affordable(), frameTime);
        int x = this.getXPos() + (int) animationOffset.x;
        int y = this.getYPos() + (int) animationOffset.y;

        //Render currency item
        renderImage(matrixStack, itemImageResource, x, y, 0, 0,
                16, 16, 16, 16
        );

        //Render Locked Overlay
        renderLockOverlayIfAppropriate(matrixStack, x, y);
    }

    /**
     * @return  a vector that describes how much to move the image
     * in the x & y positions at this point in the animation.
     *
     * @param affordable if the player can afford the currency.
     * @param frameTime time to render the frame.
     */
    private Vector2f getHoverAnimationOffset(boolean affordable, float frameTime){
        Vector2f animation;
        if(this.isHovered() && affordable){
            stepAnimationRenderer(frameTime);
            animation = getHoverAnimationPath(frame);
        } else {
            resetAnimationRenderer();
            animation = new Vector2f(0, 0);
        }

        return animation;
    }

    /**
     * Handles rendering the additional lock overlay on top
     * of the button if the player cannot afford the coin
     * or note.
     *
     * @param x the x position at which to start rendering.
     * @param y the y position at which to start rendering.
     */
    protected void renderLockOverlayIfAppropriate(MatrixStack matrix, int x, int y){
        if(!affordable()){
            renderImage(
                    matrix, WalletInventoryScreen.WALLET_GUI_TEXTURE, x, y, 0,
                    65, SIZE, SIZE, 256, 256
            );
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
     * Calculates the current animation key frame
     * based on the amount of time passed during
     * renders.
     *
     * <p/>Effectively steps through the animation
     * render cycle, in sync with the frame rate.
     *
     * <p/>Called each render cycle, but only steps
     * through when the animation(s) are supposed to
     * be changed.
     *
     * @param frameTime the amount of time the frame
     * took render.
     */
    private void stepAnimationRenderer(float frameTime){
        if(renderTime >= (1000 / FRAMES_PER_SECOND)){
            renderTime = 0;
            if(frame >= FRAMES - 1)
                frame = 0;
            else frame++;
        } else renderTime += frameTime * 100;
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
    protected Vector2f getHoverAnimationPath(int frame){
        switch (frame + 1){
            case 1: return new Vector2f(0, -1);
            case 2: return new Vector2f(0, 0);
            case 3: return new Vector2f(0, 1);
            case 4: return new Vector2f(0, 0);
            default: return new Vector2f(0, -1);
        }
    }


    // ############
    // Action Event
    // ############

    /** Obfuscated {@link #onMouseAction(double, double, int)} */
    @Override public boolean func_231044_a_(double x, double y, int button) { return onMouseAction(x, y, button); }

    /**
     * Called when a mouse action is performed. Will handle invoking
     * a click if the mouse if hovering this widget.
     *
     * @param x mouse X position when clicked.
     * @param y mouse Y position when clicked.
     * @param button the mouse button id.
     * @return {@code true} if a clicked event
     * was raised (effectively clicked).
     */
    public boolean onMouseAction(double x, double y, int button) {
        if(isSelfClicked(x, y, button)) {
            if (func_230992_c_(x, y)) { //If hovered
                this.onPress();
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the button is clicked by the player.
     * Sends a {@link com.ki11erwolf.shoppery.packets.MoneyWithdrawPacket}
     * in an attempt to withdraw the requested amount.
     */
    public void onPress(){
        playDownSound(Minecraft.getInstance().getSoundHandler());
        ICurrencyItem cItem = (ICurrencyItem) currencyItem;

        if(cItem.isWholeCashValue())
            Packet.send(PacketDistributor.SERVER.noArg(),
                    new MoneyWithdrawPacket(playerUUID.toString(), true, cItem.getSimpleCashValue())
            );

        else if(cItem.isFractionalCashValue())
            Packet.send(PacketDistributor.SERVER.noArg(),
                    new MoneyWithdrawPacket(playerUUID.toString(), false, cItem.getSimpleCashValue())
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
        ICurrencyItem cItem = (ICurrencyItem) currencyItem;

        if(balance >= 1){
            if(cItem.isFractionalCashValue())
                return true;
            else return balance >= cItem.getSimpleCashValue();
        } else {
            if(cItem.isWholeCashValue())
                return false;
            else return cents >= cItem.getSimpleCashValue();
        }
    }

    // #####
    // Sound
    // #####

    /** Obfuscated {@link #playDownSound(SoundHandler)}. */
    @Override @ParametersAreNonnullByDefault
    public void func_230988_a_(SoundHandler soundHandler) {
        playDownSound(soundHandler);
    }

    /**
     * Plays the shoppery money withdraw sound effect,
     * replacing the default button press sounds with
     * {@link ShopperySoundEvents#WITHDRAW}.
     */
    public void playDownSound(SoundHandler soundHandler) {
        if(affordable()) {
            soundHandler.play(SimpleSound.master(ShopperySoundEvents.WITHDRAW,
                    RandomUtils.nextFloat(1.2F, 1.4F), 0.50F
            ));
        }
    }
}
