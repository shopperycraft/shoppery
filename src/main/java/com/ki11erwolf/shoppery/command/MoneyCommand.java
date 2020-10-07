package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Used to give the player note items
 * by taking the amount off their balance.
 */
class MoneyCommand extends Command {

    /**
     * Name of this command.
     */
    private static final String NAME = "money";

    /**
     * Money command constructor.
     */
    MoneyCommand() {
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

        switch (arguments[0].toLowerCase()){
            case "1":
                if(playerWallet.subtract(1) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_ONE));
                else localeMessage(player, "insufficient_funds");
                break;
            case "5":
                if(playerWallet.subtract(5) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_FIVE));
                else localeMessage(player, "insufficient_funds");
                break;
            case "10":
                if(playerWallet.subtract(10) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_TEN));
                else localeMessage(player, "insufficient_funds");
                break;
            case "20":
                if(playerWallet.subtract(20) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_TWENTY));
                else localeMessage(player, "insufficient_funds");
                break;
            case "50":
                if(playerWallet.subtract(50) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_FIFTY));
                else localeMessage(player, "insufficient_funds");
                break;
            case "100":
                if(playerWallet.subtract(100) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_ONE_HUNDRED));
                else localeMessage(player, "insufficient_funds");
                break;

            case "500":
                if(playerWallet.subtract(500) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_FIVE_HUNDRED));
                else localeMessage(player, "insufficient_funds");
                break;
            case "1k":
                if(playerWallet.subtract(1000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_ONE_K));
                else localeMessage(player, "insufficient_funds");
                break;
            case "5k":
                if(playerWallet.subtract(5000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_FIVE_K));
                else localeMessage(player, "insufficient_funds");
                break;
            case "10k":
                if(playerWallet.subtract(10000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_TEN_K));
                else localeMessage(player, "insufficient_funds");
                break;
            case "50k":
                if(playerWallet.subtract(50000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_FIFTY_K));
                else localeMessage(player, "insufficient_funds");
                break;
            case "100k":
                if(playerWallet.subtract(100000) || player.isCreative())
                    player.addItemStackToInventory(new ItemStack(ModItems.NOTE_ONE_HUNDRED_K));
                else localeMessage(player, "insufficient_funds");
                break;
            default:
                localeMessage(player, "invalid_amount");
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
