package com.ki11erwolf.shoppery.container;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.item.CoinItem;
import com.ki11erwolf.shoppery.network.packets.PRequestMoney;
import com.ki11erwolf.shoppery.network.packets.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotCoin extends SlotItemHandler {

    public SlotCoin(EntityPlayer player, CoinItem coin, int index, int relX, int relY, int xPosition, int yPosition,
                    long balance, byte cents) {
        super(new Handler(player, coin, balance, cents), index, relX + xPosition, relY + yPosition);
    }

    private static void onClick(EntityPlayer player, CoinItem item){
        Minecraft.getInstance().getSoundHandler().play(
                SimpleSound.getMasterRecord(ShopperySoundEvents.MONEY, 1.0F)
        );

        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestMoney(player.getUniqueID().toString(), false, item.getWorth())
        );
    }

    private static class Handler implements IItemHandlerModifiable {

        private final EntityPlayer player;

        private final CoinItem item;

        private final long balance;

        private final byte cents;

        Handler(EntityPlayer player, CoinItem item, long balance, byte cents){
            this.player = player;
            this.item = item;
            this.balance = balance;
            this.cents = cents;
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {

        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            if(balance > 0)                         return new ItemStack(item);
            else if(item.getWorth() <= cents)       return new ItemStack(item);
            else                                    return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            SlotCoin.onClick(player, item);
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            SlotCoin.onClick(player, item);
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    }
}
