package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.block.ModBlocks;
import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.ItemPrices;
import com.ki11erwolf.shoppery.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class ShopTile extends ModTile{

    static final TileDefinition<?> TILE_DEFINITION = new TileDefinition<>(
            "shop", ShopTile::new, ModBlocks.SHOP_BLOCK
    );

    private final ShopTileData data;

    public ShopTile() {
        super(TILE_DEFINITION);
        data = new ShopTileData(this);
    }

    // Trading

    protected boolean trySellToPlayer(World world, PlayerEntity player) {
        //Check side, held item, and if can trade
        if(!canTrade(world, player)) return false;

        //Check player balance
        Wallet playerWallet = BankManager._getBank(world).getWallet(player);
        if(!playerWallet.subtract(Float.parseFloat(String.valueOf(getBuyPrice())))) {
            return false;
        }

        //Give item
        IItemProvider itemToTrade = getItemInstance();
        if(!player.addItemStackToInventory(new ItemStack(itemToTrade)))
            world.addEntity(new ItemEntity(
                    world, player.getPosX(), player.getPosY(), player.getPosZ(), new ItemStack(itemToTrade)
            ));

        return true;
    }

    protected boolean tryBuyFromPlayer(World world, PlayerEntity player){
        return false;
    }

    protected IItemProvider getItemInstance(){
        IItemProvider itemToTrade = ForgeRegistries.ITEMS.getValue(getTradedItem());
        if(itemToTrade == null) itemToTrade = ForgeRegistries.BLOCKS.getValue(getTradedItem());

        return itemToTrade;
    }

    protected boolean canTrade(World world, PlayerEntity playerEntity){
        return !world.isRemote && data.hasItem() && playerEntity.getHeldItemMainhand().getItem() == Items.AIR;
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
        if(!(price.allowsBuying() || price.allowsSelling()))
            throw new IllegalArgumentException("ItemPrice used to setup ShopTile cannot be traded.");

        this.data.setFromItemPrice(price);
    }

    protected void setup(ResourceLocation item){
        ItemPrice price = ItemPrices.getPrice(item).withPriceFluctuation();
        if(!(price.allowsBuying() || price.allowsSelling()))
            throw new IllegalArgumentException("ItemPrice used to setup ShopTile cannot be traded.");

        this.data.setFromItemPrice(price);
    }

    // Public API

    public void activate(){
        ItemPrice price = ItemPrices.getRandomPrice();

        if(!(price.allowsBuying() && price.allowsSelling()))
            activate();

        setupIfNot(ItemPrices.getRandomItemPrice());
    }

    public boolean sellToPlayer(World world, PlayerEntity player) {
        boolean sold = trySellToPlayer(world, player);

        if(player instanceof ServerPlayerEntity && sold) ShopperySoundEvents.sendSoundEvent(player,
                ShopperySoundEvents.TRANSACTION_2, 1.0F, (float) MathUtil.getRandomDoubleInRange(0.9, 1.3));
        else ShopperySoundEvents.sendSoundEvent(player, ShopperySoundEvents.DECLINE, 1.0F, 1.0F);

        return sold;
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
