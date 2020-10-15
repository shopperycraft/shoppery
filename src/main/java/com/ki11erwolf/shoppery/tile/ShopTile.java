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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class ShopTile extends ModTile {

    protected static final ShopsConfig SHOPS_CONFIG = ModConfig.GENERAL_CONFIG.getCategory(ShopsConfig.class);

    static final TileDefinition<?> TILE_DEFINITION = new TileDefinition<>(
            "shop", ShopTile::new, ModBlocks.SHOP_BLOCK
    );

    private final ShopTileData data;

    public ShopTile() {
        super(TILE_DEFINITION);
        data = new ShopTileData(this);
    }

    // Init & Setup

    protected void setup() {
        setupRandomPrice();
    }

    // Price setup

    protected void setupRandomPrice() {
        ItemPrice randomSetPrice = ItemPrices.getRandomPrice().withPriceFluctuation();
        if(!checkPrice(randomSetPrice))
            setupRandomPrice();

        setupPrice(randomSetPrice);
    }

    // Trading

    protected boolean trySellToPlayer(World world, PlayerEntity player) {
        //Check side and player sneak/inventory
        if(!canTrade(world, player)) return false;
        //Don't allow selling unsellable items.
        if(getBuyPrice() <= 0) return false;

        //Check player balance
        Wallet playerWallet = BankManager._getBank(world).getWallet(player);
        if(!playerWallet.subtract(Float.parseFloat(String.valueOf(getBuyPrice())))) {
            return false;
        }

        //Give item
        IItemProvider itemToTrade = data.getItemObject();
        if(!player.addItemStackToInventory(new ItemStack(itemToTrade)))
            world.addEntity(new ItemEntity(world,player.getPosX(),
                    player.getPosY(), player.getPosZ(), new ItemStack(itemToTrade)
            ));

        return true;
    }

    protected boolean tryBuyFromPlayer(World world, PlayerEntity player){
        return false;
    }

    @SuppressWarnings("RedundantIfStatement")
    protected boolean canTrade(World world, PlayerEntity player){
        if(world.isRemote)
            return false;

        if(SHOPS_CONFIG.requireSneakToUse() && !player.isSneaking())
            return false;

        if(SHOPS_CONFIG.requireEmptyHandToUse() && !player.getHeldItemMainhand().isEmpty())
            return false;

        return true;
    }

    protected IItemProvider getItemInstance() {
        IItemProvider itemToTrade = ForgeRegistries.ITEMS.getValue(getTradedItem());
        itemToTrade = (itemToTrade == null) ? ForgeRegistries.BLOCKS.getValue(getTradedItem()) : itemToTrade;

        return itemToTrade;
    }

    // Public API

    public boolean sellToPlayer(World world, PlayerEntity player) {
        if(!(player instanceof ServerPlayerEntity))
            return false;

        //Setup shop if not already.
        if(ensureSetup()) {
            playActivatedSoundEvent(world, player);
            return true;
        }

        if(trySellToPlayer(world, player)) {
            playTradeSoundEvent(world, player);
            return true;
        }

        playFailSoundEvent(world, player);
        return false;
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

    // Sound

    protected void playActivatedSoundEvent(World world, PlayerEntity player){
        ShopperySoundEvents.sendSoundEvent(player, ShopperySoundEvents.CASH_REGISTER,
                1.0F, (float) MathUtil.getRandomDoubleInRange(0.8, 1.4));
    }

    protected void playFailSoundEvent(World world, PlayerEntity player){
        ShopperySoundEvents.sendSoundEvent(player, ShopperySoundEvents.DECLINE, 1.0F, 1.0F);
    }

    protected void playTradeSoundEvent(World world, PlayerEntity player){
        ShopperySoundEvents.sendSoundEvent(player, ShopperySoundEvents.SOUND_CONFIG.useAltTransactionSound()
                        ? ShopperySoundEvents.TRANSACT_ALT : ShopperySoundEvents.TRANSACT,
                1.0F, (float) MathUtil.getRandomDoubleInRange(0.9, 1.3)
        );
    }

    // Setup Check

    protected boolean ensureSetup() {
        if(isSetup())
            return false;

        setup();
        return true;
    }

    protected boolean isSetup() {
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

    // Price Setup Check

    protected boolean checkPrice(ItemPrice price) {
        if(price == null) return false;

        //Don't allow unsellable/non-purchasable items.
        if(!(price.allowsSelling() && price.allowsBuying()))
            return false;

        double minBuyPrice = price.getBuyPrice() -
                (price.getBuyPrice() * (price.getPriceFluctuation() / 100));

        //Don't allow selling for more than purchase price
        return !(price.getSellPrice() >= minBuyPrice);
    }

    protected void setupPrice(ItemPrice item) {
        this.data.setFromItemPrice(item);
    }

    // Read/Write

    @Override
    protected CompoundNBT onWrite(CompoundNBT tags) {
        data.writeNBT(tags);
        return tags;
    }

    @Override
    protected void onRead(BlockState state, CompoundNBT tags) {
        data.readNBT(tags);
    }
}
