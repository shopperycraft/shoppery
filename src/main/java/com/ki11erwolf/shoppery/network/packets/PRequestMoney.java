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

/**
 * Sent by clients to request the server put the requested
 * coin/note in the players inventory provided they have
 * enough funds.
 */
public class PRequestMoney extends Packet<PRequestMoney> {

    /**
     * The player who's requesting the money.
     */
    private final String playerUUID;

    /**
     * True if it's a note item, false
     * if it's a coin item.
     */
    private final boolean isNote;

    /**
     * The worth of the coin/note.
     */
    private final int amount;

    /**
     * Constructor
     *
     * @param playerUUID The player who's requesting the money.
     * @param isNote True if it's a note item, false
     * if it's a coin item.
     * @param amount The worth of the coin/note.
     */
    public PRequestMoney(String playerUUID, boolean isNote, int amount){
        this.playerUUID = playerUUID;
        this.isNote = isNote;
        this.amount = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PRequestMoney, PacketBuffer> getEncoder() {
        return (packet, buffer) -> {
            writeString(packet.playerUUID, buffer);
            buffer.writeBoolean(packet.isNote);
            buffer.writeInt(packet.amount);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PRequestMoney> getDecoder() {
        return (buffer) -> new PRequestMoney(readString(buffer), buffer.readBoolean(), buffer.readInt());
    }

    /**
     * {@inheritDoc}
     *
     * Gives the player the requested amount as a note/coin
     * provided they have enough funds.
     */
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
                    //Notes
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
                    //Coins
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
