package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.item.ShopperyItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Used to give the player note items
 * by taking the amount off their balance.
 */
class CmdMoney extends Command {

    /**
     * Name of this command.
     */
    private static final String NAME = "money";

    /**
     * Money command constructor.
     */
    CmdMoney() {
        super(NAME);
    }

    /**
     * {@inheritDoc}
     *
     * Gives the player the specified amount as NoteItem item
     * provided they have enough funds.
     *
     * @param arguments the arguments given by the player.
     *                  May be empty!
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     */
    @Override
    void onCommandCalled(String[] arguments, PlayerEntity player, World world) {
        Wallet playerWallet = BankManager._getWallet(world, player);
        String insufficientFunds = getLocalizedMessage("insufficient_funds");

        switch (arguments[0].toLowerCase()){
            case "1":
                if(playerWallet.subtract(1) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_ONE));
                else message(player, insufficientFunds);
                break;
            case "5":
                if(playerWallet.subtract(5) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIVE));
                else message(player, insufficientFunds);
                break;
            case "10":
                if(playerWallet.subtract(10) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_TEN));
                else message(player, insufficientFunds);
                break;
            case "20":
                if(playerWallet.subtract(20) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_TWENTY));
                else message(player, insufficientFunds);
                break;
            case "50":
                if(playerWallet.subtract(50) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIFTY));
                else message(player, insufficientFunds);
                break;
            case "100":
                if(playerWallet.subtract(100) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_ONE_HUNDRED));
                else message(player, insufficientFunds);
                break;

            case "500":
                if(playerWallet.subtract(500) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIVE_HUNDRED));
                else message(player, insufficientFunds);
                break;
            case "1k":
                if(playerWallet.subtract(1000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_ONE_K));
                else message(player, insufficientFunds);
                break;
            case "5k":
                if(playerWallet.subtract(5000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIVE_K));
                else message(player, insufficientFunds);
                break;
            case "10k":
                if(playerWallet.subtract(10000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_TEN_K));
                else message(player, insufficientFunds);
                break;
            case "50k":
                if(playerWallet.subtract(50000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_FIFTY_K));
                else message(player, insufficientFunds);
                break;
            case "100k":
                if(playerWallet.subtract(100000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ShopperyItems.NOTE_ONE_HUNDRED_K));
                else message(player, insufficientFunds);
                break;
            default:
                message(player, getLocalizedMessage("invalid_amount"));
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean checkArguments(String[] args){
        return args.length == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean canExecute(PlayerEntity player, World world) {
        return true;
    }
}
