package com.ki11erwolf.shoppery.packets;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.item.CoinItem;
import com.ki11erwolf.shoppery.item.NoteItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
 * Sent by client players to request the server deposit the
 * ItemStack of Notes or Coins they're holding in their
 * inventory ({@link PlayerInventory#getItemStack()})
 * to wallet of the player.
 */
public class DepositCashPacket extends Packet<DepositCashPacket> {

    /**
     * The player requesting the deposit.
     */
    private final String playerUUID;

    /**
     * Defines whether the packet will
     * consume the stack or a single item;
     * {@code true} will consume the stack
     * and {@code false} will consume a
     * single item.
     */
    private final boolean stack;

    /**
     * @param playerUUID The player requesting the deposit.
     * @param stack {@code true} if the entire stack should,
     *        {@code false} if a single item should be consumed.
     */
    public DepositCashPacket(String playerUUID, boolean stack){
        this.playerUUID = playerUUID;
        this.stack = stack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<DepositCashPacket, PacketBuffer> getEncoder() {
        return (packet, buffer) -> {
            buffer.writeBoolean(packet.stack);
            writeString(packet.playerUUID, buffer);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, DepositCashPacket> getDecoder() {
        return (buffer) -> {
            boolean stack = buffer.readBoolean();
            String playerUUID = readString(buffer);
            return new DepositCashPacket(playerUUID, stack);
        };
    }

    /**
     * {@inheritDoc}
     *
     * Handles depositing the ItemStack the player is holding
     * in their inventory to the players wallet.
     */
    @Override
    BiConsumer<DepositCashPacket, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            try{
                //Get player
                PlayerEntity player = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer())
                        .getPlayerList().getPlayerByUUID(UUID.fromString(packet.playerUUID));

                if(player == null){
                    ShopperyMod.getNewLogger().error(
                            "Player requesting balance cannot be found: " + packet.playerUUID
                    );
                    return;
                }

                //Get referenced objects
                ItemStack deposit = player.inventory.getItemStack();
                Wallet senderWallet = BankManager._getWallet(player.getEntityWorld(), player);

                //Do deposit
                if(deposit.getItem() instanceof NoteItem){
                    senderWallet.add(
                            ((NoteItem) deposit.getItem()).getWorth() * (packet.stack ? deposit.getCount() : 1)
                    );

                    if(packet.stack) deposit.setCount(0);
                    else deposit.shrink(1);
                } else if(deposit.getItem() instanceof CoinItem){
                    int amount = ((CoinItem) deposit.getItem()).getWorth()
                            * (packet.stack ? deposit.getCount() : 1);

                    senderWallet.add(amount / 100, (byte)(amount % 100));

                    if(packet.stack) deposit.setCount(0);
                    else deposit.shrink(1);
                }
            } catch (Exception e){
                ShopperyMod.getNewLogger().error("Failed to deposit cash item stack", e);
            }
        });
    }
}
