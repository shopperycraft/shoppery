package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
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
    void onCommandCalled(String[] arguments, EntityPlayer player, World world) {
        EntityPlayer toPlayer
                = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(arguments[0]);
        String amount = arguments[1];

        if(player.isCreative()){
            message(player, TextFormatting.RED + "Cannot use /pay in creative mode.");
            return;
        }

        if(toPlayer != null){
            if(!player.getEntityWorld().getWorldInfo().getWorldName()
                    .equals(toPlayer.getEntityWorld().getWorld().getWorldInfo().getWorldName())){
                message(player, TextFormatting.RED + "Player must be in the same world as you.");
                return;
            }

            Wallet toPlayerWallet = BankManager._getWallet(world, toPlayer);
            Wallet fromPlayerWallet = BankManager._getWallet(world, player);

            if(fromPlayerWallet.subtract(amount)){
                try{
                    toPlayerWallet.add(amount);
                    message(
                            player,
                            "Paid: " + TextFormatting.RED + "$" + amount +
                                    TextFormatting.WHITE + " to " + TextFormatting.BLUE +
                                    player.getName().getString()
                    );
                    message(
                            toPlayer,
                            TextFormatting.BLUE + player.getName().getString() +
                                    TextFormatting.WHITE + " sent you: " +
                                    TextFormatting.GREEN + "$" + amount
                    );
                } catch (NumberFormatException e){
                    message(player, TextFormatting.RED + "Given amount is in invalid format: " + amount);
                    ShopperyMod.getNewLogger().warn("Number format exception: " + amount, e);
                }
            } else {
                message(player, TextFormatting.RED + "Insufficient funds!");
            }

        } else {
            message(player, TextFormatting.RED + "Player: " + arguments[0] + " not found.");
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
    boolean canExecute(EntityPlayer player, World world) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    String getUsage() {
        return "/pay <Player Name> <Amount>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getFunction() {
        return "Pays the specified player the specified amount from " +
                "your in-game funds, provided you have enough.";
    }
}
