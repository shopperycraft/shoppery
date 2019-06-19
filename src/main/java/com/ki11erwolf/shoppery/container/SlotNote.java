package com.ki11erwolf.shoppery.container;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.item.NoteItem;
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

public class SlotNote extends SlotItemHandler {

    public SlotNote(EntityPlayer player, NoteItem note, int index, int relX, int relY,
                    int xPosition, int yPosition, long balance) {
        super(new Handler(player, note, balance), index, relX + xPosition, relY + yPosition);
    }

    private static void onClick(EntityPlayer player, NoteItem item){
        Minecraft.getInstance().getSoundHandler().play(
                SimpleSound.getMasterRecord(ShopperySoundEvents.MONEY, 1.0F)
        );

        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestMoney(player.getUniqueID().toString(), true, item.getWorth())
        );
    }

    private static class Handler implements IItemHandlerModifiable {

        private final EntityPlayer player;

        private final NoteItem item;

        private final long balance;

        Handler(EntityPlayer player, NoteItem item, long balance){
            this.player = player;
            this.item = item;
            this.balance = balance;
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
            if(item.getWorth() <= balance)          return new ItemStack(item);
            else                                    return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            SlotNote.onClick(player, item);
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            SlotNote.onClick(player, item);
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
