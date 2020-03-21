package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.config.ShopperyConfig;
import com.ki11erwolf.shoppery.config.categories.General;
import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.ItemPrices;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

/**
 * Command (/price) that allows players to check
 * the price of the item they're holding.
 */
class CmdPrice extends Command{

    /**
     * Creates the command object with name (/price).
     */
    CmdPrice() {
        super("price");
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Checks the price of the item the issuing player
     * is currently holding and prints it in chat.
     *
     * @param arguments the arguments given by the player.
     *                  May be empty!
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     */
    @Override
    void onCommandCalled(String[] arguments, PlayerEntity player, World world) {
        giveHeldItemPrice(player);
    }

    /**
     * Checks the price of the item the given player is
     * currently holding and prints it in chat.
     *
     * @param playerEntity the given player issuing the command.
     */
    private void giveHeldItemPrice(PlayerEntity playerEntity){
        ItemStack heldItem = playerEntity.getHeldItemMainhand();
        if(heldItem.getItem() == Items.AIR)
            heldItem = playerEntity.getHeldItemOffhand();

        if(heldItem.getItem() == Items.AIR){
            message(playerEntity, getLocalizedMessage("no_item"));
            return;
        }

        ItemPrice price = ItemPrices.getPrice(heldItem);

        if(price == null)
            message(playerEntity, getLocalizedMessage("no_price"));
        else
            message(playerEntity, formatLocalizedMessage(
                    "price",
                    price.getItem(), ((price.allowsBuying())
                            ? ShopperyConfig.GENERAL_CONFIG .getCategory(General.class)
                            .getCurrencySymbol() + price.getBuyPrice()
                            : getLocalizedMessage("buy_prohibited")),
                    ((price.allowsSelling())
                            ? ShopperyConfig.GENERAL_CONFIG .getCategory(General.class)
                            .getCurrencySymbol() + price.getSellPrice()
                            : getLocalizedMessage("sell_prohibited")),
                    price.getPriceFluctuation()
            ));
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
    boolean canExecute(PlayerEntity player, World world) {
        return true;
    }
}
