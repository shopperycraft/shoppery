package com.ki11erwolf.shoppery.tile.renderer;

import com.ki11erwolf.shoppery.util.QueueRegisterer;

/**
 * Holds all instances of shoppery tile (entity) renderers
 * and handles the registration of them.
 */
public final class ModTileRenderers extends QueueRegisterer<ModTileRenderer> {

    /**
     * Private singleton instance of this class.
     */
    public static final ModTileRenderers RENDERERS = new ModTileRenderers();

    /** Private constructor */
    private ModTileRenderers(){}

    //############################
    //          Renders
    //############################

    /**
     * The {@link net.minecraft.client.renderer.tileentity.TileEntityRenderer} for the
     * {@link com.ki11erwolf.shoppery.tile.BasicShopTile} tile entity.
     */
    @SuppressWarnings("unused") // Self registering declaration
    public static final ModTileRenderer BASIC_SHOP_RENDERER = new BasicShopRenderer().register();

    //############################
    //          Renders
    //############################

    /**
     * Handles registering all constructed and registered TileEntity Renderers.
     */
    public static void registerRenderers() {
        RENDERERS.iterateQueue(ModTileRenderer::bindTileToRenderer);
    }

    /**
     * Adds a renderer to a queue, where from there it will then be registered
     * to the game at the appropriate time.
     *
     * @param renderer the renderer to register.
     */
    static void queueItem(ModTileRenderer renderer) {
        RENDERERS.queueForRegistration(renderer);
    }

}
