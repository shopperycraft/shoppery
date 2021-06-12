package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.packets.PlayerMessagePacket;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Used to list commands and get information on
 * specific commands.
 */
class ModCommand extends Command {

    /**
     * Name of the command.
     */
    private static final String NAME = "shoppery";

    /**
     * Shoppery command constructor.
     */
    ModCommand() {
        super(NAME);
    }

    /**
     * {@inheritDoc}
     *
     *  Either sends the calling player a message containing
     *  a list of all shoppery commands or a message containing
     *  information on a specific command depending on the
     *  arguments given.
     *
     * @param arguments the arguments given by the player.
     *                  May be empty!
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     */
    @Override
    void onCommandCalled(String[] arguments, PlayerEntity player, World world) {
        if(arguments.length == 0){
            localeMessage(player, "help_message");

            forEach((s, command) -> {
                PlayerMessagePacket.send(
                        player, LocaleDomains.COMMAND.sub(LocaleDomains.USAGE), command.getName()
                );
                PlayerMessagePacket.send(
                        player, LocaleDomains.COMMAND.sub(LocaleDomains.DESCRIPTION), command.getName()
                );
            });
        } else {
            Command cmd = get(arguments[0].toLowerCase());

            if(cmd == null){
                localeMessage(player, "command_not_found");
            } else {
                PlayerMessagePacket.send(
                        player, LocaleDomains.COMMAND.sub(LocaleDomains.USAGE), cmd.getName()
                );
                PlayerMessagePacket.send(
                        player, LocaleDomains.COMMAND.sub(LocaleDomains.DESCRIPTION), cmd.getName()
                );
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     * @return {@code true}
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
        return args.length <= 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void localeMessage(PlayerEntity playerEntity, String identifier, Object... params){
        PlayerMessagePacket.send(
                playerEntity, Command.COMMAND_MESSAGES.sub(() -> "shopperycraft"), identifier, params
        );
    }
}
