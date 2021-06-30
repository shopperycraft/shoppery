package com.ki11erwolf.shoppery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * This interface is designed solely to work around Minecraft source
 * obfuscation for inheriting Mod classes that also inherit from
 * Minecraft's {@link Widget}. This class effectively renames the
 * obfuscated Minecraft source method names, improving code writing &
 * readability.
 *
 * <p/>Inheriting classes will then be given access to the correctly named
 * {@code default} methods in this interface, which can be used instead
 * of the obfuscated Minecraft method names. As well as a rendering
 * {@link #render(MatrixStack, int, int, float)} method that can be used
 * instead of the Minecraft one.
 */
public interface WidgetFix {

    /**
     * @return {@code true} if this widget is being clicked during
     * a mouse action event.
     *
     * @param x mouse x pos.
     * @param y mouse y pos.
     * @param button mouse button id.
     */
    default boolean isSelfClicked(double x, double y, int button) {
        Widget self = asWidget();
        if (self.active && self.visible) {
            // this.active && this.visible
            return button >= 0 && button < 3;
        }

        return false;
    }

    // #################
    // MC Render Methods
    // #################

//    //Button Render Method
//    default void func_230431_b_(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
//        this.render(matrix, mouseXPos, mouseYPos, frameTime);
//    }

//    //Widget Render Method
//    default void func_230430_a_(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
//        this.render(matrix, mouseXPos, mouseYPos, frameTime);
//    }

    // ###################
    // Image & Item Render
    // ###################

    /**
     * Draws an image of an ItemStack on the screen at
     * the given screen X & Y starting positions. Makes
     * specific items visible for display.
     *
     * <p><br>
     *     Copied from {@link net.minecraft.client.gui.screen.inventory.ContainerScreen}.
     * </br></p>
     *
     * @param stack the item or block to draw.
     * @param posX the X position on screen at which to start drawing
     *             an item of the ItemStack.
     * @param posY the Y position on screen at which to start drawing
     *             an item of the ItemStack.
     */
    default void renderItemStackAt(ItemStack stack, int posX, int posY) {
        renderItemStackAt(this, stack, posX, posY);
    }

    /**
     * Draws an image of an ItemStack on the screen at
     * the given screen X & Y starting positions. Makes
     * specific items visible for display.
     *
     * <p><br>
     *     Copied from {@link net.minecraft.client.gui.screen.inventory.ContainerScreen}.
     * </br></p>
     *
     * @param widget the component widget implementation doing the drawing.
     * @param stack the item or block to draw.
     * @param posX the X position on screen at which to start drawing
     *             an item of the ItemStack.
     * @param posY the Y position on screen at which to start drawing
     *             an item of the ItemStack.
     */
    static void renderItemStackAt(WidgetFix widget, ItemStack stack, int posX, int posY) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        //noinspection deprecation
        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
        widget.setBlitOffset(200);
        itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        itemRenderer.renderItemAndEffectIntoGUI(stack, posX, posY);
        widget.setBlitOffset(0);
        itemRenderer.zLevel = 0.0F;
    }

    /**
     * Renders an image from resource location on the screens foremost render layer
     * at the given screen coordinates. Allows specifying a Y position offset in
     * the texture map for hover response.
     *
     * @param imageResource image texture located by resource location.
     * @param screenPosX screen X position to start rendering the image at.
     * @param screenPosY screen y position to start rendering the image at.
     * @param textureStartXPos X coordinate in the image file to start rendering at.
     * @param textureStartYPos Y coordinate in the image file to start rendering at.
     * @param textureRenderWidth the width of the image to render, i.e. X coordinate to end rendering at.
     * @param textureRenderHeight the height of the image to render, i.e. Y coordinate to end rendering at.
     * @param imageXPixelResolution the width pixel resolution of the image file on disk
     * @param imageYPixelResolution the height pixel resolution of the image file on disk
     * @param hoverOffset amount of pixels downward to shift the Y start position by on mouse hover.
     */
    default void renderImage(MatrixStack matrix, ResourceLocation imageResource, int screenPosX, int screenPosY, int textureStartXPos,
                             int textureStartYPos, int textureRenderWidth, int textureRenderHeight,
                             int imageXPixelResolution, int imageYPixelResolution, int hoverOffset) {
        renderImageResource(
                asWidget(), matrix, imageResource, screenPosX, screenPosY, textureStartXPos, textureStartYPos,
                textureRenderWidth, textureRenderHeight, imageXPixelResolution, imageYPixelResolution, hoverOffset
        );
    }

    /**
     * Renders an image from resource location on the screens foremost render layer
     * at the given screen coordinates. Allows specifying a Y position offset in
     * the texture map for hover response.
     *
     * @param imageResource image texture located by resource location.
     * @param screenPosX screen X position to start rendering the image at.
     * @param screenPosY screen y position to start rendering the image at.
     * @param textureStartXPos X coordinate in the image file to start rendering at.
     * @param textureStartYPos Y coordinate in the image file to start rendering at.
     * @param textureRenderWidth the width of the image to render, i.e. X coordinate to end rendering at.
     * @param textureRenderHeight the height of the image to render, i.e. Y coordinate to end rendering at.
     * @param imageWidthPixelResolution the width pixel resolution of the image file on disk
     * @param imageHeightPixelResolution the height pixel resolution of the image file on disk
     * @param hoverOffset amount of pixels downward to shift the Y start position by on mouse hover.
     */
    static void renderImageResource(Widget widget, MatrixStack matrix, ResourceLocation imageResource, int screenPosX, int screenPosY,
                                    int textureStartXPos, int textureStartYPos, int textureRenderWidth, int textureRenderHeight,
                                    int imageWidthPixelResolution, int imageHeightPixelResolution, int hoverOffset) {
        if (widget.isHovered()) { //If hovered
            textureStartYPos += hoverOffset;
        }

        renderImageResource(
                matrix, imageResource, screenPosX, screenPosY, textureStartXPos, textureStartYPos,
                textureRenderWidth, textureRenderHeight, imageWidthPixelResolution, imageHeightPixelResolution
        );
    }

    /**
     * Renders an image from resource location on the screens foremost render layer
     * at the given screen coordinates.
     *
     * @param imageResource image texture located by resource location.
     * @param screenPosX screen X position to start rendering the image at.
     * @param screenPosY screen y position to start rendering the image at.
     * @param textureStartXPos X coordinate in the image file to start rendering at.
     * @param textureStartYPos Y coordinate in the image file to start rendering at.
     * @param textureRenderWidth the width of the image to render, i.e. X coordinate to end rendering at.
     * @param textureRenderHeight the height of the image to render, i.e. Y coordinate to end rendering at.
     * @param imageWidthPixelResolution the width pixel resolution of the image file on disk
     * @param imageHeightPixelResolution the height pixel resolution of the image file on disk
     */
    default void renderImage(MatrixStack matrix, ResourceLocation imageResource, int screenPosX, int screenPosY,
                             int textureStartXPos, int textureStartYPos, int textureRenderWidth, int textureRenderHeight,
                             int imageWidthPixelResolution, int imageHeightPixelResolution) {
        renderImageResource(
                matrix, imageResource, screenPosX, screenPosY, textureStartXPos, textureStartYPos,
                textureRenderWidth, textureRenderHeight, imageWidthPixelResolution, imageHeightPixelResolution
        );
    }

    /**
     * Renders an image from resource location on the screens foremost render layer
     * at the given screen coordinates.
     *
     * @param imageResource image texture located by resource location.
     * @param screenPosX screen X position to start rendering the image at.
     * @param screenPosY screen y position to start rendering the image at.
     * @param textureStartXPos X coordinate in the image file to start rendering at.
     * @param textureStartYPos Y coordinate in the image file to start rendering at.
     * @param textureRenderWidth the width of the image to render, i.e. X coordinate to end rendering at.
     * @param textureRenderHeight the height of the image to render, i.e. Y coordinate to end rendering at.
     * @param imageXPixelResolution the width pixel resolution of the image file on disk
     * @param imageYPixelResolution the height pixel resolution of the image file on disk
     */
    static void renderImageResource(MatrixStack matrix, ResourceLocation imageResource, int screenPosX, int screenPosY,
                                    int textureStartXPos, int textureStartYPos, int textureRenderWidth, int textureRenderHeight,
                                    int imageXPixelResolution, int imageYPixelResolution) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(imageResource);

        RenderSystem.enableDepthTest();
        Widget.blit(matrix, screenPosX, screenPosY, (float)textureStartXPos, (float)textureStartYPos,
                textureRenderWidth, textureRenderHeight, imageXPixelResolution, imageYPixelResolution);
    }

    // ###########
    // Rename util
    // ###########

    default void renderTooltip1(MatrixStack stack, FontRenderer fontRenderer, ITextComponent tooltipText, int x, int y, int z) {
        renderTooltip1_(stack, fontRenderer, tooltipText, x, y, z);
    }

    default void renderTooltip2(MatrixStack stack, FontRenderer fontRenderer, ITextComponent tooltipText, int x, int y, int z) {
        renderTooltip2_(stack, fontRenderer, tooltipText, x, y, z);
    }

    default void blitC(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b, int c, int d) {
        blit_(matrix, posX, posY, xBegin, yBegin, a, b, c, d);
    }

    default void blitA(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b) {
        blitA_(matrix, posX, posY, xBegin, yBegin, a, b);
    }

    default void blitB(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b) {
        blitB_(matrix, posX, posY, xBegin, yBegin, a, b);
    }

    // ####################
    // Alternative render()
    // ####################

    /**
     * @param mouseXPos mouse X coordinate
     * @param mouseYPos mouse Y coordinate.
     * @param frameTime the amount of time (in milliseconds)
     *            the frame took render.
     */
    void render(MatrixStack matrixStack, int mouseXPos, int mouseYPos, float frameTime);

    // ######################
    // Self Getters & Setters
    // ######################

    default void setBlitOffset(int offset){
        setBlitOffset(asWidget(), offset);
    }

    default boolean isHovered() {
        return isHovered(asWidget());
    }

    default int getXPos(){
        return getXPos(asWidget());
    }

    default int getYPos(){
        return getYPos(asWidget());
    }

    default void setXPos(int x){
        setXPos(asWidget(), x);
    }

    default void setYPos(int y){
        setYPos(asWidget(), y);
    }

    default int getWidth(){
        return getWidth(asWidget());
    }

    default int getHeight(){
        return getHeight(asWidget());
    }

    default boolean isOfTypeWidget(){
        return this instanceof Widget;
    }

    default Widget asWidget(){
        if(!(this instanceof Widget))
            throw new ClassCastException(
                "Child classes must inherit from Minecrafts Widget class."
            );

        return (Widget) this;
    }

    // ##################
    // Static Rename util
    // ##################

    @SuppressWarnings("SuspiciousNameCombination")
    static void renderTooltip1_(MatrixStack stack, FontRenderer fontRenderer, ITextComponent tooltipText, int x, int y, int z) {
        AbstractGui.drawCenteredString(stack, fontRenderer, tooltipText, x, y, z);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    static void renderTooltip2_(MatrixStack stack, FontRenderer fontRenderer, ITextComponent tooltipText, int x, int y, int z) {
        AbstractGui.drawString(stack, fontRenderer, tooltipText, x, y, z);
    }

    static void blit_(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b, int c, int d) {
        Button.blit(matrix, posX, posY, xBegin, yBegin, a, b, c, d);
    }

    static void blitA_(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b) {
        Button.blit(matrix, posX, posY, xBegin, yBegin, a, b, 0, 0);
    }

    static void blitB_(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b) {
        Button.blit(matrix, posX, posY, xBegin, yBegin, a, b, 16, 16);
    }

    static boolean isHovered (Widget widget){
        return widget.isFocused();
    }

    static int getXPos(Widget widget){
        return widget.x;
    }

    static int getYPos(Widget widget){
        return widget.y;// widget.field_230691_m_;
    }

    static void setXPos(Widget widget, int x){
        widget.x = x;
    }

    static void setYPos(Widget widget, int y){
        widget.y = y;
    }

    static int getWidth(Widget widget){
        return widget.getWidth();
    }

    static int getHeight(Widget widget){
        return widget.getHeightRealms();
    }

    static void setBlitOffset(Widget widget, int offset) { widget.setBlitOffset(offset); }
}
