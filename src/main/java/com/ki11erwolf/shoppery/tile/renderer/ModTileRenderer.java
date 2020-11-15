package com.ki11erwolf.shoppery.tile.renderer;

public interface ModTileRenderer {

    void bindTileToRenderer();

    default ModTileRenderer register() {
        ModTileRenderers.queueItem(this);
        return this;
    }
}
