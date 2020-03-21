package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.config.ShopperyConfig;
import com.ki11erwolf.shoppery.config.categories.General;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Allows one player to transfer money to
 * another player given they have sufficient
 * funds.
 */
class CmdPay extends Command{

    /**
     * Name of the command.
     */
    private static final String NAME = "pay";

    /**
     * Pay command constructor.
     */
    CmdPay() {
        super(NAME);
    }

    /**
     * {@inheritDoc}
     *
     * Pays the specified the specified amount
     * if the players balance is sufficient.
     *
     * @param arguments the arguments given by the player.
     *                  May be empty!
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     */
    @Override
    void onCommandCalled(String[] arguments, PlayerEntity player, World world) {
        PlayerEntity toPlayer
                = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(arguments[0]);
        String amount = arguments[1];

        if(player.isCreative()){
            message(player, getLocalizedMessage("creative_mode"));
            return;
        }

        if(toPlayer != null){
            if(!player.getEntityWorld().getWorldInfo().getWorldName()
                    .equals(toPlayer.getEntityWorld().getWorld().getWorldInfo().getWorldName())){
                message(player, getLocalizedMessage("world_mismatch"));
                return;
            }

            Wallet toPlayerWallet = BankManager._getWallet(world, toPlayer);
            Wallet fromPlayerWallet = BankManager._getWallet(world, player);

            if(fromPlayerWallet.subtract(amount)){
                try{
                    toPlayerWallet.add(amount);
                    message(player, formatLocalizedMessage("received",
                            player.getName().getString(), ShopperyConfig.GENERAL_CONFIG
                                    .getCategory(General.class).getCurrencySymbol() + amount
                    ));
                    message(toPlayer, formatLocalizedMessage("paid",
                            ShopperyConfig.GENERAL_CONFIG
                                    .getCategory(General.class).getCurrencySymbol()
                                    + amount, player.getName().getString()
                    ));
                } catch (NumberFormatException e){
                    message(player, getLocalizedMessage("format_error"));
                    ShopperyMod.getNewLogger().warn("Number format exception: " + amount, e);
                }
            } else {
                message(player, getLocalizedMessage("insufficient_funds"));
            }

        } else {
            message(player, formatLocalizedMessage("player_not_found", arguments[0]));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     * @return true
     */
    @Override
    boolean canExecute(PlayerEntity player, World world) {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param arguments
     * @return {@code false} if incorrect
     * number of arguments.
     */
    @Override
    boolean checkArguments(String[] arguments){
        return arguments.length == 2;
    }
}
