package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.block.ModBlocks;
import com.ki11erwolf.shoppery.config.ModConfig;
import com.ki11erwolf.shoppery.config.categories.ShopsConfig;
import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.ItemPrices;
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
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The base Tile for Shops and Shop types, as well as the Tile used for the {@link
 * com.ki11erwolf.shoppery.block.ShopBlock standard standard RNG singleplayer shop}.
 *
 * <br>Provides everything necessary for a very simple, yet complete shop in Minecraft.
 * Used as the Tile object instance for the standard RNG singleplayer shop.
 *
 * <p/>In addition, this class is designed to be used as a parent class for the Tiles of
 * specialized Shops and shop types. Supporting the easy creation of additional, more
 * complex Shops and Shop types by providing a solid foundation designed to be built
 * upon, extended in functionality, and adaptable.
 */
public class ShopTile extends ModTile {

    /**
     * The Tile registration object for this specific tile.
     */
    protected static final TileRegistration<?> REGISTRATION = new TileRegistration<>(
            "shop", ShopTile::new, ModBlocks.SHOP_BLOCK
    );

    /**
     * A convenience reference to the global shops configuration settings.
     */
    protected static final ShopsConfig SHOPS_CONFIG = ModConfig.GENERAL_CONFIG.getCategory(ShopsConfig.class);

    /**
     * This shop tiles transaction manager that aids in tracking,
     * timing, and reversing trades with players.
     */
    private final ShopTransactions transactions = new ShopTransactions();

    /**
     * This shop tiles data manager - aids in the storage & I/O of the tiles data.
     */
    private final ShopTileData data;

    /**
     * Creates a new basic shop and shop block tile.
     */
    public ShopTile() {
        super(REGISTRATION);
        data = new ShopTileData(this);
    }

    // Init & Setup

    /**
     * Attempts the complete setup and construction of this specific Shop Tile.
     *
     * The setup <b>should</b>, assuming it's possible, have the Shop correctly
     * setup and ready for use, with an Item to trade and price(s) to trade the
     * item at. In rare cases, where setup is simply not possible, the shops
     * traded item should not be set and the setup should fail.
     *
     * <p/>Implementing shop classes are expected to override this method and
     * provide their own implementation.
     */
    protected void setup() {
        setupSelf();
    }

    /**
     * Sets up the shop as to sell a randomly chosen item, at a similar
     * price to the original.
     */
    private void setupSelf() {
        ItemPrice randomSetPrice = ItemPrices.getRandomPrice().withPriceFluctuation();
        if(!isValidTrade(randomSetPrice))
            setupSelf(); //Repeat until valid price is found

        setShopsTrade(randomSetPrice);
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
        IItemProvider itemToTrade = data.getItemObject();
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

        Item toFind = data.getItemObject().asItem();
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

    /**
     * @return the Item object instance this shop is setup
     * to trade, or {@code null} if not setup.
     */
    protected IItemProvider getItemObject() {
        IItemProvider itemToTrade = ForgeRegistries.ITEMS.getValue(getItem());
        itemToTrade = (itemToTrade == null) ? ForgeRegistries.BLOCKS.getValue(getItem()) : itemToTrade;

        return itemToTrade;
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
            playOpenedSoundEvent(world, player);
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
            playOpenedSoundEvent(world, player);
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
        return data.getItem();
    }

    /**
     * @return the price this specific shop will sell its item for.
     */
    public double getBuyPrice() {
        return data.getBuy();
    }

    /**
     * @return the price this specific shop will buy its item for.
     */
    public double getSellPrice() {
        return data.getSell();
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
        if(data.isItemSet()) {
            // If setup and valid.
            if(data.isItemValid()) {
                return true;
            }

            // If not validated.
            data.validateItemObject();
            return data.isItemValid();
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
        this.data.setFromItemPrice(item);
    }

    // Read/Write

    /**
     * {@inheritDoc}
     *
     * <p/>Delegates the responsibility of writing nbt
     * to the {@link #data} object.
     *
     * @param tags the CompoundNBT object provided by
     *             Forge. Use to write memory data to
     *             file by converting to NBT-tag-data
     *             and writing it to the CompoundNBT
     *             object.
     */
    @Override
    protected CompoundNBT onWrite(CompoundNBT tags) {
        data.writeNBT(tags);
        return tags;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Delegates the responsibility of reading nbt
     * to the {@link #data} object.
     *
     * @param state the block providing the Tile as it
     *              exists in the world.
     * @param tags the CompoundNBT object containing
     *             previously saved tags from file. Use
     */
    @Override
    protected void onRead(BlockState state, CompoundNBT tags) {
        data.readNBT(tags);
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
