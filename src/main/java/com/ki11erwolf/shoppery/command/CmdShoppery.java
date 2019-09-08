package com.ki11erwolf.shoppery.command;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * Used to list commands and get information on
 * specific commands.
 */
class CmdShoppery extends Command {

    /**
     * Name of the command.
     */
    private static final String NAME = "shoppery";

    /**
     * Shoppery command constructor.
     */
    CmdShoppery() {
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
            StringBuilder message = new StringBuilder(TextFormatting.GOLD + "Shoppery Commands:\n");
            forEach((s, command) -> message.append(TextFormatting.GREEN).append("Usage: ")
                    .append(command.getUsage()).append(TextFormatting.WHITE).append(" - ")
                    .append(TextFormatting.BLUE).append(command.getFunction()).append("\n"));
            message(player, message.toString());
        } else {
            Command cmd = get(arguments[0].toLowerCase());

            if(cmd == null){
                message(player, TextFormatting.RED + "Command not found!");
            } else {
                message(
                        player,
                        TextFormatting.GREEN + "Usage: " +
                                cmd.getUsage() + TextFormatting.WHITE + " - " +
                                TextFormatting.BLUE + cmd.getFunction() + "\n"
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
    String getUsage() {
        return "/shoppery [<Command>]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getFunction() {
        return "Displays the list of ShopperyCraft commands or displays information on a specific command.";
    }
}
