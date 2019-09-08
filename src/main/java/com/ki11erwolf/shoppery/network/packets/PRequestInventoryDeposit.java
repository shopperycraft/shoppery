package com.ki11erwolf.shoppery.network.packets;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.item.CoinItem;
import com.ki11erwolf.shoppery.item.NoteItem;
import net.minecraft.entity.player.PlayerEntity;
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
 * Sent by clients to request the server take all the
 * notes/coins in the given players inventory and add
 * the sum to their wallets balance.
 */
public class PRequestInventoryDeposit extends Packet<PRequestInventoryDeposit> {

    /**
     * The player requesting the inventory deposit.
     */
    private final String playerUUID;

    /**
     * Constructor.
     *
     * @param playerUUID The player requesting the inventory deposit.
     */
    public PRequestInventoryDeposit(String playerUUID){
        this.playerUUID = playerUUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PRequestInventoryDeposit, PacketBuffer> getEncoder() {
        return (packet, buffer) -> writeString(packet.playerUUID, buffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PRequestInventoryDeposit> getDecoder() {
        return (buffer) -> new PRequestInventoryDeposit(readString(buffer));
    }

    /**
     * {@inheritDoc}
     *
     * Sums up the worth of all the notes and coins in the
     * players inventory and adds it to their wallet balance.
     */
    @Override
    BiConsumer<PRequestInventoryDeposit, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            try{
                PlayerEntity player = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer())
                        .getPlayerList().getPlayerByUUID(UUID.fromString(packet.playerUUID));

                if(player == null){
                    ShopperyMod.getNewLogger().error(
                            "Player requesting balance cannot be found: " + packet.playerUUID
                    );
                    return;
                }

                Wallet senderWallet = BankManager._getWallet(player.getEntityWorld(), player);

                for(int i = 0; i < 100; i++){
                    ItemStack stack = player.inventory.getStackInSlot(i);

                    if(stack.getItem() instanceof NoteItem){
                        senderWallet.add(((NoteItem) stack.getItem()).getWorth() * stack.getCount());
                        stack.setCount(0);
                    }

                    if(stack.getItem() instanceof CoinItem){
                        int amount = ((CoinItem) stack.getItem()).getWorth() * stack.getCount();

                        senderWallet.add(amount / 100, (byte)(amount % 100));
                        stack.setCount(0);
                    }
                }

            } catch (Exception e){
                ShopperyMod.getNewLogger().error("Failed to deposit players inventory", e);
            }
        });
    }
}
