package com.ki11erwolf.shoppery.tile.renderer;

import com.ki11erwolf.shoppery.tile.BasicShopTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Special renderer for {@link BasicShopTile}s. Handles
 * rendering the Item or Block for sale atop the Block,
 * and displaying the GUI with additional information
 * about the shop.
 */
public class BasicShopRenderer implements ModTileRenderer {

    /**
     * {@inheritDoc}
     */
    @Override @SuppressWarnings("rawtypes") // Screw Generics, Just work.
    public Function renderer() {
        return (renderer) ->
                new TileEntityRenderer<TileEntity>((TileEntityRendererDispatcher) renderer) {
                    @Override
                    public void render(@Nonnull TileEntity tileEntity, float partialTicks, @Nonnull MatrixStack matrixStack,
                                       @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
                        BasicShopTile shop = (BasicShopTile) tileEntity;
                        renderItemForSale(ForgeRegistries.ITEMS.getValue(shop.getItem()), matrixStack, buffer, overlay);
                    }
                };
    }

    /**
     * Renders an Item or Block just above the Shop Block,
     * indicating that the shop sells that item.
     *
     * @param item the Item or Block to render.
     */
    private void renderItemForSale(IItemProvider item, MatrixStack stack, IRenderTypeBuffer buffer, int overlay) {
        stack.push();
        stack.translate(0.5D, 1.0D, 0.5D);
        stack.rotate(Vector3f.YP.rotationDegrees(-180.0F));
        stack.scale(0.6F, 0.6F, 0.6F);
        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(item),
                ItemCameraTransforms.TransformType.FIXED, 200,
                overlay, stack, buffer);
        stack.pop();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link BasicShopTile#BASIC_SHOP_REGISTRATION}
     */
    @Override
    public TileEntityType<?> tileType() {
        return BasicShopTile.BASIC_SHOP_REGISTRATION.getTileType();
    }
}
