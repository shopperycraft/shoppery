package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.ItemPrices;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Chat command used to set prices of items in game.
 */
public class SetPriceCommand extends Command{

    //Constructor
    SetPriceCommand() {
        super("setprice");
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Will attempt to parse the command into a valid ItemPrice
     * for the item in the players main hand.
     */
    @Override
    void onCommandCalled(String[] arguments, PlayerEntity player, World world) {
        ItemPrice parsedPrice = parse(arguments, player);
        if(parsedPrice == null) return;

        if(ItemPrices.setPrice(parsedPrice)){
            localeMessage(player, "success",
                    player.getHeldItemMainhand().getItem().getName().getString(),
                    parsedPrice.getBuyPrice(), parsedPrice.getSellPrice()
            );
        } else {
            localeMessage(player, "failure",
                    player.getHeldItemMainhand().getItem().getName().getString()
            );
        }
    }

    /**
     * Parses the players command into an {@link ItemPrice} object
     * that can be used to set the price of an item.
     *
     * @param arguments the command arguments from the player.
     * @param player the player who issues the command.
     * @return the ItemPrice parsed from the command, or {@code null}
     * if no ItemPrice could be parsed.
     */
    private ItemPrice parse(String[] arguments, PlayerEntity player){
        double buy, sell;

        try {
            buy = Double.parseDouble(arguments[0]);
        } catch (NumberFormatException e) {
            localeMessage(player, "invalid_buy_price", arguments[0]);
            return null;
        }

        if(arguments.length == 1)
            sell = buy / 2;
        else {
            try {
                sell = Double.parseDouble(arguments[1]);
            } catch (NumberFormatException e) {
                localeMessage(player, "invalid_sell_price", arguments[1]);
                return null;
            }
        }

        if(player.getHeldItem(Hand.MAIN_HAND).getItem() == Items.AIR){
            localeMessage(player, "hold_item");
            return null;
        }

        return new ItemPrice(
                player.getHeldItem(Hand.MAIN_HAND).getItem().getRegistryName(),
                buy < 0, buy, sell < 0, sell
        );
    }

    /**
     * {@inheritDoc}
     * @return {@code true} if there are between 1 and 4 (inclusive)
     * arguments.
     */
    @Override
    boolean checkArguments(String[] arguments){
        return arguments.length > 0 && arguments.length < 3;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Only allows execution of the command if the player
     * is a server operator.
     *
     * @return {@code true} if the calling player is a server
     * operator, {@code false} otherwise.
     */
    @Override
    boolean canExecute(PlayerEntity player, World world) {
        if(player.getServer() == null)
            return player.isCreative();

        return player.hasPermissionLevel(player.getServer().getOpPermissionLevel());
    }
}
