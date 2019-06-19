package com.ki11erwolf.shoppery.container;

import com.ki11erwolf.shoppery.item.ShopperyItems;
import com.ki11erwolf.shoppery.network.packets.PRequestInventoryDeposit;
import com.ki11erwolf.shoppery.network.packets.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotDeposit extends SlotItemHandler {

    public SlotDeposit(EntityPlayer player, int index, int relX, int relY, int xPosition, int yPosition) {
        super(new Handler(player), index, relX + xPosition, relY + yPosition);
    }

    private static void onClick(EntityPlayer player){
        Minecraft.getInstance().getSoundHandler().play(
                SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F)
        );

        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestInventoryDeposit(player.getUniqueID().toString())
        );
    }

    private static class Handler implements IItemHandlerModifiable {

        private final EntityPlayer player;

        Handler(EntityPlayer player){
            this.player = player;
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
            return new ItemStack(ShopperyItems.DEPOSIT_ITEM);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            SlotDeposit.onClick(player);
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            SlotDeposit.onClick(player);
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
