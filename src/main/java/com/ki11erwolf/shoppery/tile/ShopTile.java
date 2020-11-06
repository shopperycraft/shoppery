package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.config.ModConfig;
import com.ki11erwolf.shoppery.config.categories.ShopsConfig;
import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * The parent class and template for the different Shop Block
 * implementations, such as the {@link BasicShopTile}. Provides
 * much of the groundwork needed to create a functioning Shop.
 *
 * //TODO: Finish Documentation
 *
 * @param <T> the exact implementation of {@link ShopTileData} to
 *           use as the data container and manager for this type
 *           of Shop Tile.
 */
public abstract class ShopTile<T extends ShopTileData> extends ModTile {

    /**
     * A convenience reference to the global shops configuration settings.
     */
    protected static final ShopsConfig SHOPS_CONFIG = ModConfig.GENERAL_CONFIG.getCategory(ShopsConfig.class);

    /**
     * This Shops recent transactions. Helps reverse/undo recent player
     * transactions upon request.
     */
    private final ShopTransactions transactions = new ShopTransactions();

    /**
     * This Shops internal {@link ShopTileData data manager and container}.
     * Exact implementation may be different depending on Shop type.
     */
    private final T data;

    public ShopTile(TileRegistration<? extends ModTile> tileDefinition) {
        super(tileDefinition);
        this.data = Objects.requireNonNull(createDataManager());
    }

    // Impl & Setup

    /**
     * Requests that the specific Shop implementation
     * setup itself as best it can.
     */
    protected abstract void setup();

    /**
     * Requests that the specific Shop implementation
     * create a brand new object {@link ShopTileData}
     * object, or some implementation thereof that
     * is capable of storing the data the Shop Tile
     * needs and uses.
     *
     * @return a new {@link ShopTileData} object, or
     * some implementation thereof, that can be used
     * to store the data of the specific Shop Tile
     * implementation.
     */
    protected abstract T createDataManager();

    // Impl API

    /**
     * @return this Shops internal {@link ShopTileData data manager and
     * container} implementation instance, which handles the data required
     * by this specific Shop type.
     */
    protected T getData() {
        return data;
    }

    /**
     * @return this Shops {@link ShopTransactions transactions}
     * - allows checking for and reversing/undoing recent player
     * transactions that have been logged.
     */
    protected ShopTransactions getTransactions() {
        return transactions;
    }

    // Trading

    /**
     * Attempts to sell the item traded by this shop to the
     * specified player. The trade will fail if the player
     * doesn't have the funds, is not meeting all {@link
     * #allowTrade(World, PlayerEntity) trade requirements},
     * or if the shop cannot be setup.
     *
     * @param world the world the player & shop are in.
     * @param player the player attempting the trade.
     * @return {@code true} if the item was successfully sold
     * to the player, {@code false} otherwise.
     */
    protected boolean sellToPlayer(World world, PlayerEntity player) { // PurchaseItem()
        //Check side and player sneak/inventory
        if(!allowTrade(world, player)) return false;
        //Don't allow selling unsellable items.
        if(getBuyPrice() <= 0) return false;

        //Init Vars
        boolean paid;
        Wallet wallet = BankManager._getBank(world).getWallet(player);
        boolean isReversal = transactions.reverseTransaction(player, false);

        //Try Take Pay
        if(!isReversal)
            paid = wallet.subtract((float) getBuyPrice());
        else paid = wallet.subtract((float) getSellPrice());

        //Must trade - pay taken!
        if(!paid) return false;

        //Give Item
        IItemProvider itemToTrade = getData().getItemObject();
        if(!player.addItemStackToInventory(new ItemStack(itemToTrade)))
            world.addEntity(new ItemEntity(world,player.getPosX(),
                    player.getPosY(), player.getPosZ(), new ItemStack(itemToTrade)
            ));

        //Log
        if(!isReversal)
            transactions.logTransaction(player, true);

        return true; //Successful trade
    }

    /**
     * Attempts to buy the item traded by this shop from the
     * specified player for a small amount of money. The trade
     * will fail if the player doesn't have the item in inventory,
     * is not meeting all {@link #allowTrade(World, PlayerEntity)
     * trade requirements}, or if the shop cannot be setup.
     *
     * @param world the world the player & shop are in.
     * @param player the player attempting the trade.
     * @return {@code true} if the item was successfully bought
     * from the player, {@code false} otherwise.
     */
    protected boolean buyFromPlayer(World world, PlayerEntity player) { // SellItem()
        //Check side and player sneak/inventory
        if(!allowTrade(world, player)) return false;
        //Don't allow buying non-purchasable items.
        if(getSellPrice() <= 0) return false;

        Item toFind = getData().getItemObject().asItem();
        for(ItemStack stack : player.inventory.mainInventory) {
            //Check for nulls
            if(stack.getItem().getRegistryName() == null || toFind.getRegistryName() == null){
                continue;
            }

            //Check for and find trade item
            ItemStack foundItem;
            if(stack.getItem().getRegistryName().equals(toFind.getRegistryName())){
                foundItem = stack;
            } else continue;

            //Can trade!

            // Init Vars
            Wallet wallet = BankManager._getBank(world).getWallet(player);
            boolean isReversal = transactions.reverseTransaction(player, true);

            //Take Item
            foundItem.shrink(1);

            //Give Pay
            if(!isReversal)
                wallet.add((float) getSellPrice());
            else wallet.add((float) getBuyPrice());

            //Log
            if(!isReversal)
                transactions.logTransaction(player, false);

            return true;
        }

        return false; //Failed trade
    }

    /**
     * Checks to see if the world & player are in the
     * correct states needed to perform a trade.
     *
     * @param world the world the player & shop are in.
     * @param player the player attempting the trade.
     * @return {@code true} if all conditions required
     * for trade are met, {@code false} otherwise.
     */
    protected boolean allowTrade(World world, PlayerEntity player) {
        if(world.isRemote)
            return false;

        if(SHOPS_CONFIG.requireSneakToUse() && !player.isSneaking())
            return false;

        return !SHOPS_CONFIG.requireEmptyHandToUse() || player.getHeldItemMainhand().isEmpty();
    }

    // Public API

    /**
     * Called when a player wants to purchase the item
     * this shop trades. If the player has enough funds
     * in their wallet, and if all conditions required
     * for trade are met, the player will be given the
     * item and have the {@link #getBuyPrice() price
     * of the item} taken from their balance.
     *
     * @param world the world the player & shop are in.
     * @param player the player attempting the trade.
     * @return {@code true} if the item was successfully
     * sold to the player, {@code false} otherwise.
     */
    public boolean purchaseItem(World world, PlayerEntity player) {
        if(!(player instanceof ServerPlayerEntity))
            return false;

        //Setup shop if not already.
        if(ensureSetup()) {
            if(validateSetup())
                playOpenedSoundEvent(world, player);
            else playFailSoundEvent(world, player);

            return true;
        }

        if(sellToPlayer(world, player)) {
            playTradeSoundEvent(world, player);
            return true;
        }

        playFailSoundEvent(world, player);
        return false;
    }

    /**
     * Called when a player wants to sell the item
     * this shop trades. If the player has the item in
     * their inventory, and if all conditions required
     * for trade are met, the player will have the item
     * taken from their inventory and have the {@link
     * #getSellPrice()} price of the item} taken from
     * their balance.
     *
     * @param world the world the player & shop are in.
     * @param player the player attempting the trade.
     * @return {@code true} if the item was successfully
     * purchased from the player, {@code false} otherwise.
     */
    public boolean sellItem(World world, PlayerEntity player) {
        if(!(player instanceof ServerPlayerEntity))
            return false;

        //Setup shop if not already.
        if(ensureSetup()) {
            if(validateSetup())
                playOpenedSoundEvent(world, player);
            else playFailSoundEvent(world, player);

            return true;
        }

        if(buyFromPlayer(world, player)) {
            playTradeSoundEvent(world, player);
            return true;
        }

        playFailSoundEvent(world, player);
        return false;
    }

    /**
     * @return the {@link ResourceLocation} that identifies the
     * item/block that this shop sells. May be {@code null} or
     * an empty resource location if this shop doesn't sell
     * an item.
     */
    public ResourceLocation getItem() {
        return getData().getItem();
    }

    /**
     * @return the price this specific shop will sell its item for.
     */
    public double getBuyPrice() {
        return getData().getBuy();
    }

    /**
     * @return the price this specific shop will buy its item for.
     */
    public double getSellPrice() {
        return getData().getSell();
    }

    // Setup

    /**
     * Should be called before any attempt to use the shop is made.
     *
     * <p/>Makes sure this shop is setup to sell an item correctly,
     * if not, an attempt is made to setup the shop.
     *
     * @return {@code true} if the shop was not setup and an attempt
     * was made at setting it up, {@code false} if the shop was
     * already setup.
     */
    protected boolean ensureSetup() {
        if(validateSetup())
            return false;

        setup();
        return true;
    }

    /**
     * Checks to see if the shop is setup correctly
     * and trading a valid item.
     *
     * @return {@code true} if the shop is setup &
     * valid, {@code false} if not.
     */
    protected boolean validateSetup() {
        if(getData().isItemSet()) {
            // If setup and valid.
            if(getData().isItemValid()) {
                return true;
            }

            // If not validated.
            getData().validateItemObject();
            return getData().isItemValid();
        }

        return false;
    }

    // ItemPrices & Trading

    /**
     * Performs checks on the given {@link ItemPrice} object to
     * ensure that it is valid and okay for a shop to trade.
     *
     * @param price the ItemPrice to check.
     * @return {@code true} if it is okay to trade the item price
     * as it is in a shop.
     */
    protected boolean isValidTrade(ItemPrice price) {
        if(price == null) return false;

        //Don't allow unsellable/non-purchasable items.
        if(!(price.allowsSelling() && price.allowsBuying()))
            return false;

        double minBuyPrice = price.getBuyPrice() -
                (price.getBuyPrice() * (price.getPriceFluctuation() / 100));

        //Don't allow selling for more than purchase price
        return !(price.getSellPrice() >= minBuyPrice);
    }

    /**
     * Sets up this shop to trade the given item
     * at the given prices, from an {@link
     * ItemPrice}. Trade is not checked for validity
     * before setting up - use {@link
     * #isValidTrade(ItemPrice)} to check it.
     *
     * @param item the ItemPrice to make this shop
     *             trade.
     */
    protected void setShopsTrade(ItemPrice item) {
        this.getData().setFromItemPrice(item);
    }

    // Read/Write

    /**
     * {@inheritDoc}
     *
     * <p/>Delegates the responsibility of writing nbt
     * to the {@link #getData()} object.
     *
     * @param tags the CompoundNBT object provided by
     *             Forge. Use to write memory data to
     *             file by converting to NBT-tag-data
     *             and writing it to the CompoundNBT
     *             object.
     */
    @Override
    protected CompoundNBT onWrite(CompoundNBT tags) {
        getData().writeNBT(tags);
        return tags;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Delegates the responsibility of reading nbt
     * to the {@link #getData()} object.
     *
     * @param state the block providing the Tile as it
     *              exists in the world.
     * @param tags the CompoundNBT object containing
     *             previously saved tags from file. Use
     */
    @Override
    protected void onRead(BlockState state, CompoundNBT tags) {
        getData().readNBT(tags);
    }

    // Sound

    /**
     * Sends a packet to the given players Client requesting the 'shop
     * opened' sound effective be played.
     *
     * @param world the world the player is in.
     * @param player the player to send the sound effect request to.
     */
    protected void playOpenedSoundEvent(World world, PlayerEntity player){
        if(ShopperySoundEvents.SOUND_CONFIG.playActivatedSoundEffect())
            ShopperySoundEvents.sendSoundEvent(player, ShopperySoundEvents.CASH_REGISTER,
                    1.0F, (float) MathUtil.getRandomDoubleInRange(0.8, 1.4));
    }

    /**
     * Sends a packet to the given players Client requesting the 'transaction
     * failed/declined' sound effective be played.
     *
     * @param world the world the player is in.
     * @param player the player to send the sound effect request to.
     */
    protected void playFailSoundEvent(World world, PlayerEntity player){
        if(ShopperySoundEvents.SOUND_CONFIG.playTransactionDeclinedSoundEffect())
            ShopperySoundEvents.sendSoundEvent(player, ShopperySoundEvents.DECLINE, 1.0F, 1.0F);
    }

    /**
     * Sends a packet to the given players Client requesting the 'trade'
     * sound effective be played.
     *
     * @param world the world the player is in.
     * @param player the player to send the sound effect request to.
     */
    protected void playTradeSoundEvent(World world, PlayerEntity player){
        if(ShopperySoundEvents.SOUND_CONFIG.playTransactionSoundEffect())
            ShopperySoundEvents.sendSoundEvent(player, ShopperySoundEvents.SOUND_CONFIG.useAltTransactionSound()
                            ? ShopperySoundEvents.TRANSACT_ALT : ShopperySoundEvents.TRANSACT,
                    1.0F, (float) MathUtil.getRandomDoubleInRange(0.9, 1.3)
            );
    }
}
