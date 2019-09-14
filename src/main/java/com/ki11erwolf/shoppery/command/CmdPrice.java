package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.PriceAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

class CmdPrice extends Command{

    CmdPrice() {
        super("price");
    }

    @Override
    void onCommandCalled(String[] arguments, PlayerEntity player, World world) {
        if(arguments.length == 0){
            giveHeldItemPrice(player);
        }
    }

    private void giveHeldItemPrice(PlayerEntity playerEntity){
        ItemStack heldItem = playerEntity.getHeldItemMainhand();
        if(heldItem.getItem() == Items.AIR)
            heldItem = playerEntity.getHeldItemOffhand();

        if(heldItem.getItem() == Items.AIR){
            playerEntity.sendMessage(new StringTextComponent(
                    TextFormatting.RED + "Must be holding an item!"
            ));
            return;
        }

        ItemPrice price = PriceAPI.getPrice(heldItem);

        if(price == null)
            playerEntity.sendMessage(new StringTextComponent(
                    TextFormatting.RED + "Item does not have a price!"
            ));
        else
            playerEntity.sendMessage(new StringTextComponent(
                    "Item price for: " +  price.getItem() + ":"
                            + TextFormatting.RED + " buy: "
                            + ((price.allowsBuying()) ? "$" + price.getBuyPrice() : "prohibited")
                            + TextFormatting.GREEN + " sell: "
                            + ((price.allowsSelling()) ? "$" + price.getSellPrice() : "prohibited")
                            + TextFormatting.BLUE + " Price Fluctuation: " + price.getPriceFluctuation() + "%"
                    )
            );
    }

    @Override
    boolean canExecute(PlayerEntity player, World world) {
        return true;
    }

    @Override
    String getUsage() {
        return "TODO";
    }

    @Override
    String getFunction() {
        return "TODO";
    }
}
