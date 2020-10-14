package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.price.ItemPrice;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

/**
 * A helper object for {@link ShopTile}s that holds and
 * manages the data (traded item & prices) of the ShopTile.
 * In addition to this, the object also provides to/from
 * NBT methods to easily read and write the data in the
 * Tile. Data is modified using setter methods that make
 * sure to mark the Tile as dirty ({@link
 * net.minecraft.tileentity.TileEntity#markDirty()}).
 */
class ShopTileData {

    /**
     * A String value that is used as a key to store a specific
     * value within the NBT map.
     */
    protected static final String KEY_ITEM_NAMESPACE = "ItemNamespace", KEY_ITEM_PATH = "ItemPath",
                KEY_ITEM_BUY = "ItemBuy", KEY_ITEM_SELL = "ItemSell";

    /**
     * The {@link ShopTile} that created this object
     * to hold its data.
     */
    protected final ShopTile shopTile;

    /**
     * The registry name of the Item or Block that
     * the {@link ShopTile} is trading.
     */
    private ResourceLocation item;

    /**
     * The price that the {@link ShopTile} will sell
     * the {@link #item} to the player for.
     */
    private double buy;

    /**
     * The price that the {@link ShopTile} will buy
     * the {@link #item}from the player for.
     */
    private double sell;

    /**
     * Creates a new ShopTileData object to hold
     * and manage the data for a specific ShopTile.
     *
     * @param shopTile the specific ShopTile to hold
     * and manage data for.
     */
    public ShopTileData(ShopTile shopTile){
        this.shopTile = shopTile;
    }

    // Modifiers

    public void setFromItemPrice(ItemPrice itemPrice){
        this.item = itemPrice.getItem();
        this.buy = itemPrice.getBuyPrice();
        this.sell = itemPrice.getSellPrice();
        shopTile.markDirty();
    }

    /**
     * Sets the Item/Block the ShopTile will trade
     * using the registry name of the Item/Block.
     *
     * @param item the registry name as a Resource
     * Location of the Item/Block to trade.
     */
    public void setItem(ResourceLocation item){
        this.item = Objects.requireNonNull(item);
        shopTile.markDirty();
    }

    /**
     * Sets the price the ShopTile will sell
     * the traded Item/Block to the player for.
     *
     * @param buy the new price the ShopTile will
     * allow players to buy for.
     */
    public void setBuy(double buy){
        if(buy < 0) buy = 0;
        this.buy = buy;
        shopTile.markDirty();
    }

    /**
     * Sets the price the ShopTile will buy
     * the traded Item/Block from the player for.
     *
     * @param sell the new price the ShopTile will
     * allow players to sell for.
     */
    public void setSell(double sell){
        if(sell < 0) sell = 0;
        this.sell = sell;
        shopTile.markDirty();
    }

    /**
     * Clears all custom values ever written to this object.
     * Will reset the traded {@link #item} to none and will
     * reset the prices to {@code 0}.
     */
    public void reset(){
        this.item = null;
        this.buy = 0;
        this.sell = 0;
    }

    // Accessors

    /**
     * @return The registry name of the Item or Block
     * that the {@link ShopTile} is trading.
     */
    public ResourceLocation getItem() {
        return item;
    }

    /**
     * @return The price that the {@link ShopTile} will
     * sell the {@link #item} to the player for.
     */
    public double getBuy() {
        return buy;
    }

    /**
     * @return The price that the {@link ShopTile} will
     * buy the {@link #item}from the player for.
     */
    public double getSell() {
        return sell;
    }

    /**
     * @return {@code true} if and only if the {@link #item}
     * the ShopTile is trading is valid and points to a
     * registered Item/Block in the registry.
     */
    public boolean hasItem() {
        return item != null && !item.getPath().equals("");
    }

    // Other

    /**
     * {@inheritDoc}
     *
     * @return a simple and human readable description
     * of this object instance and the data contained within.
     */
    @Override
    public String toString(){
        return String.format(
                "ShopTileData[item=%s, buy=%s, sell=%s]",
                (item != null) ? item.toString() : "null", buy, sell
        );
    }

    // To/From NBT

    /**
     * Will set the ShopTiles data contained within this object
     * using a {@link CompoundNBT} object containing previously
     * written data.
     *
     * @param nbt the nbt data containing ShopTile data to be
     *            read and stored.
     * @return the {@link CompoundNBT} passed as a parameter.
     */
    protected CompoundNBT setNBTFromData(CompoundNBT nbt){
        String itemNamespace = nbt.getString(KEY_ITEM_NAMESPACE);
        String itemPath = nbt.getString(KEY_ITEM_PATH);

        if(!itemPath.equals(""))
            this.item = new ResourceLocation(itemNamespace, itemPath);

        this.buy = nbt.getDouble(KEY_ITEM_BUY);
        this.sell = nbt.getDouble(KEY_ITEM_SELL);

        return nbt;
    }

    /**
     * Will convert and write the data contained within this
     * object to the given {@link CompoundNBT} object, to be
     * used as a method of data storage.
     *
     * @param nbt a {@link CompoundNBT} object that can be written to.
     * @return the {@link CompoundNBT} passed as a parameter,
     * now containing the ShopTiles data.
     */
    protected CompoundNBT getDataAsNBT(CompoundNBT nbt){
        if(item != null) {
            nbt.putString(KEY_ITEM_NAMESPACE, this.item.getNamespace());
            nbt.putString(KEY_ITEM_PATH, this.item.getPath());
        }

        nbt.putDouble(KEY_ITEM_BUY, this.buy);
        nbt.putDouble(KEY_ITEM_SELL, this.sell);

        return nbt;
    }
}
