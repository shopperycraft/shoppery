package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.container.SlotCoin;
import com.ki11erwolf.shoppery.container.SlotDeposit;
import com.ki11erwolf.shoppery.container.SlotNote;
import com.ki11erwolf.shoppery.item.ShopperyItems;
import com.ki11erwolf.shoppery.network.packets.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;

class MoneyGuiSlotHandler {

    private final Container container;

    private final EntityPlayer player;

    private boolean areSlotsAdded = false;

    private int slotsAdded = 0;

    MoneyGuiSlotHandler(Container container, EntityPlayer player){
        this.container = container;
        this.player = player;

        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestPlayerBalance(player.getUniqueID().toString())
        );

        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestPlayerCents(player.getUniqueID().toString())
        );
    }

    void addSlots(int x, int y){
        if(areSlotsAdded)
            return;

        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestPlayerBalance(player.getUniqueID().toString())
        );

        long balance = PReceivePlayerBalance.getLastReceivedBalance();

        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestPlayerCents(player.getUniqueID().toString())
        );

        byte cents = PReceivePlayerCents.getLastReceivedBalance();

        addSlot(new SlotDeposit(player, slotsAdded++, x, y, 6, 26));

        int xStart = 144; //+18
        int yStart = 6;   //+20

        addSlot(new SlotCoin(player, ShopperyItems.COIN_ONE, slotsAdded++, x, y, xStart, yStart, balance, cents));
        addSlot(new SlotCoin(player, ShopperyItems.COIN_FIVE, slotsAdded++, x, y, xStart += 18, yStart, balance, cents));
        addSlot(new SlotCoin(player, ShopperyItems.COIN_TEN, slotsAdded++, x, y, xStart += 18, yStart, balance, cents));
        addSlot(new SlotCoin(player, ShopperyItems.COIN_TWENTY, slotsAdded++, x, y, xStart += 18, yStart, balance, cents));
        addSlot(new SlotCoin(player, ShopperyItems.COIN_FIFTY, slotsAdded++, x, y, xStart += 18, yStart, balance, cents));
        addSlot(new SlotCoin(player, ShopperyItems.COIN_EIGHTY, slotsAdded++, x, y, xStart + 18, yStart, balance, cents));

        xStart = 144;//+18
        yStart += 20;//+20

        addSlot(new SlotNote(player, ShopperyItems.NOTE_ONE, slotsAdded++, x, y, xStart, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_FIVE, slotsAdded++, x, y, xStart += 18, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_TEN, slotsAdded++, x, y, xStart += 18, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_TWENTY, slotsAdded++, x, y, xStart += 18, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_FIFTY, slotsAdded++, x, y, xStart += 18, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_ONE_HUNDRED, slotsAdded++, x, y, xStart + 18, yStart, balance));

        xStart = 144;//+18
        yStart += 20;//+20

        addSlot(new SlotNote(player, ShopperyItems.NOTE_FIVE_HUNDRED, slotsAdded++, x, y, xStart, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_ONE_K, slotsAdded++, x, y, xStart += 18, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_FIVE_K, slotsAdded++, x, y, xStart += 18, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_TEN_K, slotsAdded++, x, y, xStart += 18, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_FIFTY_K, slotsAdded++, x, y, xStart += 18, yStart, balance));
        addSlot(new SlotNote(player, ShopperyItems.NOTE_ONE_HUNDRED_K, slotsAdded++, x, y, xStart + 18, yStart, balance));

        areSlotsAdded = true;
    }

    void removeSlots(){
        for(int i = 0; i < slotsAdded; i++)
            removeLastSlot();

        slotsAdded = 0;
        areSlotsAdded = false;
    }

    private Slot addSlot(Slot slot){
        slot.slotNumber = container.inventorySlots.size();
        container.inventorySlots.add(slot);
        container.inventoryItemStacks.add(ItemStack.EMPTY);
        return slot;
    }

    private void removeLastSlot(){
        int slotNumber = container.inventorySlots.size() - 1;
        container.inventorySlots.remove(slotNumber);
        container.inventoryItemStacks.remove(slotNumber);
    }
}
