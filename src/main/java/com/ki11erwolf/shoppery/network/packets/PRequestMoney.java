package com.ki11erwolf.shoppery.network.packets;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.item.ShopperyItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PRequestMoney extends Packet<PRequestMoney> {

    private final String playerUUID;

    private final boolean isNote;

    private final int amount;

    public PRequestMoney(String playerUUID, boolean isNote, int amount){
        this.playerUUID = playerUUID;
        this.isNote = isNote;
        this.amount = amount;
    }

    @Override
    BiConsumer<PRequestMoney, PacketBuffer> getEncoder() {
        return (packet, buffer) -> {
            writeString(packet.playerUUID, buffer);
            buffer.writeBoolean(packet.isNote);
            buffer.writeInt(packet.amount);
        };
    }

    @Override
    Function<PacketBuffer, PRequestMoney> getDecoder() {
        return (buffer) -> new PRequestMoney(readString(buffer), buffer.readBoolean(), buffer.readInt());
    }

    @Override
    BiConsumer<PRequestMoney, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            EntityPlayer player = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer())
                    .getPlayerList().getPlayerByUUID(UUID.fromString(packet.playerUUID));

            if(player == null){
                ShopperyMod.getNewLogger().error(
                        "Player requesting balance cannot be found: " + packet.playerUUID
                );
                return;
            }

            Wallet senderWallet = BankManager._getWallet(player.getEntityWorld(), player);

            boolean isNote = packet.isNote;
            int amount = packet.amount;

            if(isNote){
                if(senderWallet.subtract(amount)) {
                    switch (amount) {
                        case 1:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_ONE));
                            break;
                        case 5:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIVE));
                            break;
                        case 10:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_TEN));
                            break;
                        case 20:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_TWENTY));
                            break;
                        case 50:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIFTY));
                            break;
                        case 100:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_ONE_HUNDRED));
                            break;

                        case 500:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIVE_HUNDRED));
                            break;
                        case 1_000:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_ONE_K));
                            break;
                        case 5_000:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIVE_K));
                            break;
                        case 10_000:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_TEN_K));
                            break;
                        case 50_000:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIFTY_K));
                            break;
                        case 100_000:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_ONE_HUNDRED_K));
                            break;
                    }
                }
            } else {
                if(senderWallet.subtract(0, (byte)amount)) {
                    switch (amount) {
                        case 1:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.COIN_ONE));
                            break;
                        case 5:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.COIN_FIVE));
                            break;
                        case 10:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.COIN_TEN));
                            break;
                        case 20:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.COIN_TWENTY));
                            break;
                        case 50:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.COIN_FIFTY));
                            break;
                        case 80:
                            player.inventory.addItemStackToInventory(new ItemStack(ShopperyItems.COIN_EIGHTY));
                            break;
                    }
                }
            }
        });
    }
}
