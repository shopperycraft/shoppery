package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
    void onCommandCalled(String[] arguments, PlayerEntity player, World world) {
        //Normal balance check
        if(arguments.length == 0){
            //Creative check
            if(player.isCreative()){
                message(player, formatLocalizedMessage(
                        "balance", getLocalizedMessage("infinite_balance")
                ));
                return;
            }

            //Balance message
            Wallet wallet = BankManager._getWallet(world, player);
            message(player, formatLocalizedMessage("balance", wallet.getShortenedBalance()));
        }

        //Named player balance check.
        if (arguments.length == 1){
            //Permission check.
            if(!(player.hasPermissionLevel(ServerLifecycleHooks.getCurrentServer().getOpPermissionLevel()))){
                message(player, getLocalizedMessage("not_op"));
            }

            //Get player
            String playerName = arguments[0];
            PlayerEntity target = ServerLifecycleHooks
                    .getCurrentServer().getPlayerList().getPlayerByUsername(playerName);

            //Check player
            if(target == null){
                message(player, formatLocalizedMessage(
                        "player_not_found", playerName)
                );
                return;
            }

            //Balance
            message(player, formatLocalizedMessage("balance_op", target.getName().getString(),
                    BankManager._getBank(world).getWallet(target).getShortenedBalance())
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean canExecute(PlayerEntity player, World world) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean checkArguments(String[] args){
        return args.length == 0 || args.length == 1;
    }
}
