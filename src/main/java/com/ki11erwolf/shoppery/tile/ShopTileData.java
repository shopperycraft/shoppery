package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.price.ItemPrice;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Dedicated data management and storage object for {@link ShopTile}'s
 * core internal data. <br/> Handles the complex task of dealing with
 * Shop Tile data directly - including {@link CompoundNBT NBT} I/O,
 * validation, and object conversions - as a simple object common to
 * all Shop Tiles. Additionally, this class can be extended upon to
 * create custom implementations for more complex Shop Tiles with more
 * data.
 *
 * <p/> The <i>standard</i> ShopTileData object only handles data
 * common to all Shops: the Item to trade and the prices to buy and sell
 * the Item at. Shop Tile implementations that require additional or
 * more complex data storage and handling, need to provide their own
 * implementation. Implementations <b>MUST</b> override {@link #writeNBT(
 * CompoundNBT)} and {@link #readNBT(CompoundNBT)} in order to read and
 * write the additional data.
 */
class ShopTileData {

    /**
     * Modding logger instance for this class.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

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
    protected final ShopTile<?> shopTile;

    /**
     * The registry name of the Item or Block that
     * the {@link ShopTile} is trading.
     */
    private ResourceLocation item;

    /**
     * The actual Item or Block instance that this Shop trades.
     * This object is never saved or loaded - it is always set from
     * the registry using {@link #item}. Used as a validation test
     * and as a reference.
     */
    private IItemProvider itemObject;

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
    public ShopTileData(ShopTile<?> shopTile){
        this.shopTile = shopTile;
    }

    // Modifiers

    public void setFromItemPrice(ItemPrice itemPrice){
        this.item = itemPrice.getItem();
        this.buy = itemPrice.getBuyPrice();
        this.sell = itemPrice.getSellPrice();
        clearAndValidateItemObject();
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
        clearAndValidateItemObject();
        shopTile.markDirty();
    }

    /**
     * Sets the price the ShopTile will sell
     * the traded Item/Block to the player for.
     *
     * @param buy the new price the ShopTile will
     * allow players to buy for.
     */
    public void setBuyPrice(double buy){
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
    public void setSellPrice(double sell) {
        if(sell < 0) sell = 0;
        this.sell = sell;
        shopTile.markDirty();
    }

    /**
     * Clears all custom values ever written to this object.
     * Will reset the traded {@link #item} to none and will
     * reset the prices to {@code 0}.
     */
    public void reset() {
        this.item = null;
        this.buy = 0;
        this.sell = 0;
        clearAndValidateItemObject();
    }

    // Accessors

    /**
     * @return {@code true} if the actual Item object instance
     * within the registry that is registered under the item id
     * ({@link #item}) exists and can be used. Will return {@code
     * false} if no item/ItemID has been set yet, if the ItemID
     * is invalid, or if no Item/Block is registered using the
     * ItemID.
     */
    public boolean isItemValid(){
        return hasValidItemObject();
    }

    /**
     * @return {@code true} if the Shop has an
     * item id for the item to trade, and if
     * the item id references a loaded mod.
     * Will return {@code false} if the item id
     * is blank or {@code null}.
     */
    public boolean isItemSet(){
        return isItemIDSet();
    }

    /**
     * @return The registry name of the Item or Block
     * that the {@link ShopTile} is trading.
     */
    public ResourceLocation getItem() {
        return item;
    }

    /**
     * @return the actual Item object instance within
     * the registry that is registered under the item id
     * ({@link #item}). Will return {@code null} if no
     * item/ItemID has been set yet, if the ItemID is
     * invalid, or if no Item/Block is registered using
     * the ItemID.
     */
    public IItemProvider getItemObject() {
        return itemObject;
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

    // Item & Price

    /**
     * Does a thorough check to find out if the
     * Shop has a set & valid item id for the
     * item it's trading. Used to determine if
     * the Shop has been setup or not. This
     * method will NOT check if the item exists.
     *
     * @return {@code true} if the Shop has an
     * item id for the item to trade, and if
     * the item id references a loaded mod.
     * Will return {@code false} if the item id
     * is blank or {@code null}.
     */
    protected boolean isItemIDSet(){
        if(item == null) return false;

        String namespace = item.getNamespace();
        String path = item.getPath();

        if(namespace.equals("") || path.equals(""))
            return false;

        return ModList.get().isLoaded(namespace);
    }

    /**
     * Attempts to further validate item id of the Shops traded
     * item by matching the item id to an existing item in
     * the registry. If we find an item object we can be sure
     * the item id is valid.
     *
     * @return the item object in the registry that matches
     * the Shops item id, if it could be found, otherwise
     * {@code null} is returned to indicate the item id
     * is not valid.
     */
    protected IItemProvider getItemObjectInstance() {
        IItemProvider itm = ForgeRegistries.ITEMS.getValue(item);

        if(itm == null)
            LOG.error("Failed to get a valid Item from " +
                "Shop ItemID: " + item.toString());

        return itm;
    }

    /**
     * Provides that the Shops {@link #item} is set, valid,
     * and can be used.
     *
     * <p/> Attempts to validate the ItemID of the Shops
     * traded item, by checking that it has been set, and
     * that it is set correctly. Once that is done, the
     * ItemID is used to obtain an actual Item/Block from
     * the registry. The obtained Item/Block object can
     * be used to prove the ItemID is usable. No object
     * proves the ItemID is unusable.
     */
    protected void validateItemObject(){
        //Clear instance if ID not set
        if(!isItemIDSet()){
            itemObject = null;
            return;
        }

        //Try set object if ID is set
        if(itemObject == null){
            itemObject = getItemObjectInstance();
        }

        //If object is set, check that it matches the ID.
        if(!hasValidItemObject())
            itemObject = null;
    }

    /**
     * Clears the object instance reference to the Shops
     * item and then attempts to validate it once again.
     * Used whenever the ItemID is changed to keep the
     * object instance up-to-date with the ItemID.
     */
    protected void clearAndValidateItemObject(){
        this.itemObject = null;
        validateItemObject();
    }

    /**
     * @return {@code true} if the actual Item object instance
     * within the registry that is registered under the item id
     * ({@link #item}) exists and can be used. Will return {@code
     * false} if no item/ItemID has been set yet, if the ItemID
     * is invalid, or if no Item/Block is registered using the
     * ItemID.
     */
    protected boolean hasValidItemObject(){
        if(itemObject == null) return false;
        if(itemObject.asItem().getRegistryName() == null)
            return false;

        return (item.equals(itemObject.asItem().getRegistryName()));
    }

    // Other

    /**
     * {@inheritDoc}
     *
     * @return a simple and human readable description
     * of this object instance and the data contained within.
     */
    @Override
    public String toString() {
        return String.format("ShopTileData[item=%s, buy=%s, sell=%s, object=%s]",
                (item != null) ? item.toString() : "null", buy, sell,
                itemObject == null ? "null" : itemObject.asItem().getClass().getCanonicalName()
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
    protected CompoundNBT readNBT(CompoundNBT nbt) {
        String itemNamespace = nbt.getString(KEY_ITEM_NAMESPACE);
        String itemPath = nbt.getString(KEY_ITEM_PATH);

        if(!itemPath.equals(""))
            this.item = new ResourceLocation(itemNamespace, itemPath);

        clearAndValidateItemObject();
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
    protected CompoundNBT writeNBT(CompoundNBT nbt) {
        if(item != null) {
            nbt.putString(KEY_ITEM_NAMESPACE, this.item.getNamespace());
            nbt.putString(KEY_ITEM_PATH, this.item.getPath());
        }

        nbt.putDouble(KEY_ITEM_BUY, this.buy);
        nbt.putDouble(KEY_ITEM_SELL, this.sell);

        return nbt;
    }
}
