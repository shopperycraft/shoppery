package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * Displays the players balance in chat.
 */
class CmdBalance extends Command{

    /**
     * The command name.
     */
    private static final String NAME = "balance";

    /**
     * Creates a new command.
     */
    CmdBalance() {
        super(NAME);
    }

    /**
     * {@inheritDoc}
     *
     * Sends the calling player a message displaying
     * their in-game balance.
     *
     * @param arguments the arguments given by the player.
     *                  May be empty!
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     */
    @Override
    void onCommandCalled(String[] arguments, EntityPlayer player, World world) {
        if(player.isCreative()){
            message(player, "Balance: " + TextFormatting.GREEN + "Infinite");
            return;
        }

        Wallet wallet = BankManager._getWallet(world, player);
        message(player, "Balance: "
                + TextFormatting.GREEN + wallet.getFullBalance() + " (" + wallet.getFormattedBalance() + ")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean canExecute(EntityPlayer player, World world) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean checkArguments(String[] args){
        return args.length == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getUsage() {
        return "/balance";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getFunction() {
        return "Displays your in-game balance.";
    }
}
