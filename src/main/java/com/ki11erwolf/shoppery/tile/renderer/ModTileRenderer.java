package com.ki11erwolf.shoppery.tile.renderer;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.function.Function;

/**
 * Implementable interface for special TileEntity Renderers,
 * which can handle more complicated rendering of blocks,
 * by providing a special render function.
 */
public interface ModTileRenderer {

    /**
     * For implementations to provide a reference to the
     * {@link TileEntityType} of the TileEntity the Renderer
     * is for.
     *
     * @return a reference to the  {@link TileEntityType} of
     * the TileEntity the Renderer is for.
     */
    TileEntityType<?> tileType();

    /**
     * For implementations to provide the actual {@link
     * net.minecraft.client.renderer.tileentity.TileEntityRenderer}
     * which does the rendering.
     *
     * @return the actual {@link
     * net.minecraft.client.renderer.tileentity.TileEntityRenderer}
     * which does the rendering.
     */
    @SuppressWarnings("rawtypes") // Screw Generics, Just work.
    Function renderer();

    /**
     * Handles binding the {@link net.minecraft.client.renderer.tileentity.TileEntityRenderer}
     * which does the rendering of the {@link net.minecraft.tileentity.TileEntity}
     * to the TileEntity.
     */
    @SuppressWarnings("unchecked") // Screw Generics, Just work.
    default void bindTileToRenderer() {
        ClientRegistry.bindTileEntityRenderer(tileType(), renderer());
    }

    /**
     * Queues this Renderer for registration.
     *
     * @return {@code this} renderer.
     */
    default ModTileRenderer register() {
        ModTileRenderers.queueItem(this);
        return this;
    }
}
