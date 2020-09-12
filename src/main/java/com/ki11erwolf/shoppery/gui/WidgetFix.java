package com.ki11erwolf.shoppery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
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
interface WidgetFix {

    // ###########
    // Rename util
    // ###########

    default void renderTooltip1(MatrixStack stack, FontRenderer fontRenderer, ITextComponent tooltipText, int x, int y, int z) {
        renderTooltip1_(stack, fontRenderer, tooltipText, x, y, z);
    }

    default void renderTooltip2(MatrixStack stack, FontRenderer fontRenderer, ITextComponent tooltipText, int x, int y, int z) {
        renderTooltip2_(stack, fontRenderer, tooltipText, x, y, z);
    }

    default void blit(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b, int c, int d) {
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

    default void func_230431_b_(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
        this.render(matrix, mouseXPos, mouseYPos, frameTime);
    }

    default void func_230430_a_(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
        this.render(matrix, mouseXPos, mouseYPos, frameTime);
    }

    // ######################
    // Self Getters & Setters
    // ######################

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

    static void renderTooltip1_(MatrixStack stack, FontRenderer fontRenderer, ITextComponent tooltipText, int x, int y, int z) {
        AbstractGui.func_238472_a_(stack, fontRenderer, tooltipText, x, y, z);
    }

    static void renderTooltip2_(MatrixStack stack, FontRenderer fontRenderer, ITextComponent tooltipText, int x, int y, int z) {
        AbstractGui.func_238475_b_(stack, fontRenderer, tooltipText, x, y, z);
    }

    static void blit_(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b, int c, int d) {
        Button.func_238463_a_(matrix, posX, posY, xBegin, yBegin, a, b, c, d);
    }

    static void blitA_(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b) {
        Button.func_238463_a_(matrix, posX, posY, xBegin, yBegin, a, b, 0, 0);
    }

    static void blitB_(MatrixStack matrix, int posX, int posY, float xBegin, float yBegin, int a, int b) {
        Button.func_238463_a_(matrix, posX, posY, xBegin, yBegin, a, b, 16, 16);
    }

    static boolean isHovered (Widget widget){
        return widget.func_230449_g_();
    }

    static int getXPos(Widget widget){
        return widget.field_230690_l_;
    }

    static int getYPos(Widget widget){
        return widget.field_230691_m_;
    }

    static void setXPos(Widget widget, int x){
        widget.field_230690_l_ = x;
    }

    static void setYPos(Widget widget, int y){
        widget.field_230691_m_ = y;
    }

    static int getWidth(Widget widget){
        return widget.func_230998_h_();
    }

    static int getHeight(Widget widget){
        return widget.func_238483_d_();
    }
}
