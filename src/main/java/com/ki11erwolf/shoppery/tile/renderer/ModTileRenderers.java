package com.ki11erwolf.shoppery.tile.renderer;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.tile.BasicShopTile;
import com.ki11erwolf.shoppery.util.QueueRegisterer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nonnull;

/**
 * Holds all instances of shoppery tile (entity) renderers
 * and handles the registration of them.
 */
public final class ModTileRenderers extends QueueRegisterer<ModTileRenderer> {

    /**
     * Private singleton instance of this class.
     */
    public static final ModTileRenderers RENDERERS = new ModTileRenderers();
    private ModTileRenderers(){}

    //############################
    //          Renders
    //############################

    public static final ModTileRenderer A = ((ModTileRenderer) () ->
            ClientRegistry.bindTileEntityRenderer(BasicShopTile.BASIC_SHOP_REGISTRATION.getTileType(), (a) ->
                    new TileEntityRenderer<TileEntity>(a) {
                        @Override
                        public void render(@Nonnull TileEntity tileEntity, float v, @Nonnull MatrixStack matrixStack,
                                           @Nonnull IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
                            ShopperyMod.getNewLogger().info("render()!");
                        }
                    }
            )
    ).register();

    //############################
    //          Renders
    //############################

    public static void registerRenderers(FMLClientSetupEvent event) {
        RENDERERS.iterateQueue(ModTileRenderer::bindTileToRenderer);
    }

    static void queueItem(ModTileRenderer renderer){
        RENDERERS.queueForRegistration(renderer);
    }

}
