package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.block.ModBlocks;
import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.ItemPrices;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ShopTile extends ModTile{

    static final TileDefinition<?> TILE_DEFINITION = new TileDefinition<>(
            "shop", ShopTile::new, ModBlocks.SHOP_BLOCK
    );

    private final ShopTileData data;

    public ShopTile() {
        super(TILE_DEFINITION);
        data = new ShopTileData(this);
    }

    // Setup

    protected void setupIfNot(ItemPrice item){
        if(data.hasItem()) return;
        setup(item);
    }

    protected void setupIfNot(ResourceLocation item){
        if(data.hasItem()) return;
        setup(item);
    }

    protected void setup(ItemPrice item){
        ItemPrice price = item.withPriceFluctuation();
        this.data.setFromItemPrice(price);
    }

    protected void setup(ResourceLocation item){
        ItemPrice price = ItemPrices.getPrice(item).withPriceFluctuation();
        this.data.setFromItemPrice(price);
    }

    // Public API

    public void activate(){
        setupIfNot(ItemPrices.getRandomItemPrice());
    }

    public ResourceLocation getTradedItem(){
        return data.getItem();
    }

    public double getBuyPrice(){
        return data.getBuy();
    }

    public double getSellPrice(){
        return data.getSell();
    }

    // Read/Write

    @Override
    protected CompoundNBT onWrite(CompoundNBT tags) {
        data.getDataAsNBT(tags);
        return tags;
    }

    @Override
    protected void onRead(BlockState state, CompoundNBT tags) {
        data.setNBTFromData(tags);
    }
}
