package com.ki11erwolf.shoppery.command;

import com.ki11erwolf.shoppery.ShopperyMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Base class & object reference holder
 * for all Shoppery commands. This class
 * also handles executing commands when
 * called through chat.
 */
@SuppressWarnings({"WeakerAccess", "StaticInitializerReferencesSubClass"})
public abstract class Command {

    //**************************
    // Command Object Instances
    //**************************

    /**
     * Instance of the pay command.
     */
    public static final CmdPay PAY_COMMAND;

    /**
     * Instance of the shoppery command.
     */
    public static final CmdShoppery SHOPPERY_COMMAND;

    /**
     * Instance of the money command.
     */
    public static final CmdMoney MONEY_COMMAND;

    /**
     * Instance of the balance command.
     */
    public static final CmdBalance BALANCE_COMMAND;

    public static final CmdPrice PRICE_COMMAND;

    //*******
    // Logic
    //*******

    /**
     * Creates a new command.
     *
     * @param commandName the name the command is called by.
     *                    Must be unique.
     */
    Command(String commandName){
        if(CommandListener.COMMAND_MAP.containsKey(commandName))
            throw new IllegalArgumentException("Duplicate command names cannot exist: " + commandName);

        CommandListener.COMMAND_MAP.put(commandName.toLowerCase(), this);
    }

    /**
     * Initializes this class.
     */
    //Dummy method that allows us to load
    //the class (static block).
    public static void init(){}

    /**
     * Allows sending a usage message
     * if arguments are incorrect.
     *
     * @return {@code true} if the arguments
     * are correct, {@code false} otherwise.
     */
    boolean checkArguments(String[] arguments){return true;}

    /**
     * Called when a player attempts to issue
     * this command. Will only execute if
     * {@link #canExecute(net.minecraft.entity.player.PlayerEntity, World)}
     * {@code returns true}.
     *
     * @param arguments the arguments given by the player.
     *                  May be empty!
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     */
    abstract void onCommandCalled(String[] arguments, PlayerEntity player, World world);

    /**
     * Called before the command is executed to
     * ensure the player has sufficient permissions.
     * Sends the player a message warning them of
     * permissions if {@code false} is returned.
     *
     * @param player the player that issued the command.
     * @param world the world the player issued the command in.
     * @return {@code true} if the player can issue the command,
     * {@code false} otherwise.
     */
    abstract boolean canExecute(PlayerEntity player, World world);

    /**
     * @return Should return a string showing
     * how to use the command.
     */
    abstract String getUsage();

    /**
     * @return Should return a string describing
     * what the command does.
     */
    abstract String getFunction();

    /**
     * Util method to send a player a message.
     *
     * @param player the player to send the message to.
     * @param message the message.
     */
    static void message(PlayerEntity player, String message){
        player.sendMessage(new StringTextComponent(message));
    }

    /**
     * Iterates over the Map of commands (commands mapped to their name)
     * using {@link Map#forEach(BiConsumer)}.
     *
     * @param action {@link Map#forEach(BiConsumer)} iterator.
     */
    static void forEach(BiConsumer<String, Command> action){
        CommandListener.COMMAND_MAP.forEach(action);
    }

    /**
     * Gets the command registered under the
     * given name if it exists.
     *
     * @param name the name command is registered under.
     * @return the registered command or {@code null} if
     * no command is registered under the given name.
     */
    static Command get(String name){
        return CommandListener.COMMAND_MAP.get(name);
    }

    /**
     * Singleton callback class that waits for commands
     * (any message beginning with a {@code '/'}), parses
     * them and then executes them if needed.
     */
    private enum CommandListener{

        /**
         * Singleton instance.
         */
        INSTANCE;

        /**
         * Logger for this class.
         */
        private static final Logger LOGGER = ShopperyMod.getNewLogger();

        /**
         * Maps commands to their name.
         */
        static final Map<String, Command> COMMAND_MAP = new HashMap<>();

        /**
         * Called when the players issues a command
         * in chat.
         *
         * Parses the message into a command and
         * executes it if possible.
         *
         * @param event forge event.
         */
        @SubscribeEvent
        public void onCommandSent(CommandEvent event){
            String message = event.getParseResults().getReader().getString();

            try {
                PlayerEntity player = event.getParseResults().getContext().getSource().asPlayer();
                World world = player.getEntityWorld();
                message = message.replace("/", "");
                String[] args = message.split(" ");

                Command command = COMMAND_MAP.get(args[0]);

                if(command == null)
                    return;

                if(command.canExecute(player, world)){
                    args = Arrays.copyOfRange(args, 1, args.length);
                    LOGGER.info("Calling command: " + event.getParseResults().getReader().getString());

                    if(!command.checkArguments(args)){
                        message(
                                player,
                                TextFormatting.GREEN + "Usage: " + command.getUsage() +
                                        TextFormatting.WHITE + " - " +
                                        TextFormatting.BLUE + command.getFunction()
                        );
                        event.setCanceled(true);
                        return;
                    }

                    command.onCommandCalled(args, player, world);
                } else {
                    message(player, TextFormatting.RED + "You cannot use that command!");
                }

                event.setCanceled(true);
            } catch (Exception e) {
                LOGGER.error("Failed to process command: " + message, e);
            }
        }
    }

    /*
        Registers the CommandListener to the
        forge event bus & initializes all
        commands when the class is loaded.
     */
    static{
        MinecraftForge.EVENT_BUS.register(CommandListener.INSTANCE);

        //Instance initialization.
        PAY_COMMAND = new CmdPay();
        SHOPPERY_COMMAND = new CmdShoppery();
        MONEY_COMMAND = new CmdMoney();
        BALANCE_COMMAND = new CmdBalance();
        PRICE_COMMAND = new CmdPrice();
    }
}
