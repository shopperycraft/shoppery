package com.ki11erwolf.shoppery.price.loaders;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.price.ItemPrice;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the results of running a {@link Loader}.
 * Every Loader has its own Results object that gives
 * the details from the load. This includes:
 *
 * <ul>
 *     <li>The name of the loader.</li>
 *     <li>The number of item price entries the loader loaded.</li>
 *     <li>The number of item price entries in registry replaced by this loader.</li>
 *     <li>A flag that indicates if this loader failed.</li>
 *     <li>A list of item price entries that are invalid.</li>
 *     <li>A list of affected mods - mods that has prices loaded for their items.</li>
 *     <li>A list of unaffected mods - unloaded/not-installed mods with item prices defined for them.</li>
 *     <li>A list of errors that occurred during the load - will only be displayed if the loader completely fails.</li>
 *     <li>A list of all entries that were found.</li>
 * </ul>
 *
 * <p/>The results object is modified from both the loader
 * and the registry. IT'S UNWISE TO MODIFY THIS OBJECT!
 *
 * <p/>At the end of the registry load cycle, every results
 * object will be called to print itself to the console.
 */
@SuppressWarnings("WeakerAccess")
public class Results{

    /**
     * Logger for this class.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

    /**
     * The name of the loader (or mod) this results object belongs to.
     */
    private String name;

    /**
     * The total number of entries loaded by this loader. Usually
     * set by the registry.
     */
    private int numberOfEntries = 0;

    /**
     * The number of entries in the registry replace with
     * entries from this loader. Usually set by the registry.
     */
    private int numberOfReplacements = 0;

    /**
     * Flag that indicates if the loader has failed
     * or not.
     */
    private boolean errored = false;

    /**
     * List of entries loaded by the loader that are invalid -
     * entries that could not be parsed into a valid price.
     */
    private List<String> invalidEntries = new ArrayList<>();

    /**
     * List of mods (mod ids) that have had prices loaded for their
     * items/blocks by this loader.
     */
    private List<String> affectedMods = new ArrayList<>();

    /**
     * List of mods that are not installed but have had ItemPrices
     * loaded for them. These item prices are discarded.
     */
    private List<String> unaffectedMods = new ArrayList<>();

    /**
     * The list of errors that occurred during the load process.
     * Will only be displayed if the loader errored.
     */
    private List<String> errors = new ArrayList<>();

    /**
     * The full list of ItemPrices that were successfully loaded
     * by this loader.
     */
    private List<ItemPrice> registeredEntries = new ArrayList<>();

    /**Package-private constructor*/
    Results(){}

    // *****************
    // Getters & Setters
    // *****************

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setNumberOfEntries(int numberOfEntries){
        this.numberOfEntries = numberOfEntries;
    }

    public int getNumberOfEntries(){
        return numberOfEntries;
    }

    public void setNumberOfReplacements(int numberOfReplacements){
        this.numberOfReplacements = numberOfReplacements;
    }

    public int getNumberOfReplacements(){
        return this.numberOfReplacements;
    }

    public void logInvalidEntry(String invalidEntry){
        this.invalidEntries.add(invalidEntry);
    }

    public String[] getInvalidEntries(){
        if(invalidEntries.size() == 0)
            return null;

        return invalidEntries.toArray(new String[0]);
    }

    public void logAffectedMod(String affectedMod){
        this.affectedMods.add(affectedMod);
    }

    public String[] getAffectedMods(){
        if(affectedMods.size() == 0)
            return null;

        return affectedMods.toArray(new String[0]);
    }

    public void logUnaffectedMod(String unaffectedMod){
        this.unaffectedMods.add(unaffectedMod);
    }

    public String[] getUnaffectedMods(){
        if(unaffectedMods.size() == 0)
            return null;

        return unaffectedMods.toArray(new String[0]);
    }

    public void flagAsErrored(){
        logError("Loader has failed fatally.");
        this.errored = true;
    }

    public boolean hasErrored(){
        return errored;
    }

    public void logError(String error){
        this.errors.add(error);
    }

    public String[] getErrors(){
        if(errors.size() == 0)
            return null;

        return errors.toArray(new String[0]);
    }

    public void logRegisteredEntry(ItemPrice itemPrice){
        this.registeredEntries.add(itemPrice);
    }

    public ItemPrice[] getRegisteredEntries(){
        if(registeredEntries.size() == 0)
            return null;

        return registeredEntries.toArray(new ItemPrice[0]);
    }

    // ********
    // Printing
    // ********

    /**
     * Prints out both the normal and debug results from
     * this results object.
     */
    public void print(){
        LOG.info(getAsStandardResultString());
        if(!errored)
            LOG.debug(getAsDebugResultString());
    }

    /**
     * @return this object as a visual String to be
     * printed in the console at the debug level. Contains
     * a list of every loaded item price.
     */
    protected String getAsDebugResultString(){
        StringBuilder sResults = new StringBuilder("\n");
        sResults.append("------------- Debug Results: ").append(getName()).append(" -------------\n");

        sResults.append("Loaded entries:").append("\n");
        for(ItemPrice registeredIP : registeredEntries){
            sResults.append(registeredIP.getItem().toString());
            sResults.append(" -> ").append(registeredIP.toString()).append("\n");
        }

        sResults.append("--------------------------------------------------------");
        return sResults.toString();
    }

    /**
     * @return this object as a visual String to be printed
     * in the console at the info level.
     */
    protected String getAsStandardResultString(){
        String error = getAsErrorResultString();

        if(error != null)
            return error;

        StringBuilder sResults = new StringBuilder("\n");
        sResults.append("------------- Results: ").append(getName()).append(" -------------\n");
        sResults.append("Number of entries added: ").append(getNumberOfEntries()).append("\n");
        sResults.append("Number of entries replaced: ").append(getNumberOfReplacements()).append("\n");

        if(getAffectedMods() != null){
            sResults.append("Affected Mods: ");
            for(String affectedMod : getAffectedMods()){
                sResults.append(affectedMod).append(", ");
            }
            sResults.deleteCharAt(sResults.length() -1);
            sResults.deleteCharAt(sResults.length() -1);
            sResults.append("\n");
        }

        if(getUnaffectedMods() != null){
            sResults.append("Unaffected (not loaded) Mods: ");
            for(String unaffectedMod : getUnaffectedMods()){
                sResults.append(unaffectedMod).append(", ");
            }
            sResults.deleteCharAt(sResults.length() -1);
            sResults.deleteCharAt(sResults.length() -1);
            sResults.append("\n");
        }

        if(getInvalidEntries() != null){
            sResults.append("Invalid entries:\n");
            for(String invalidEntry : getInvalidEntries()){
                sResults.append("\t").append(invalidEntry).append("\n");
            }
        }

        sResults.append("--------------------------------------------------------");
        return sResults.toString();
    }

    /**
     * @return this object as a visual String to be printed
     * in the console at the info level. Only printed when errored.
     */
    protected String getAsErrorResultString(){
        if(hasErrored()){
            StringBuilder sResults = new StringBuilder("\n");
            sResults.append("------------- Results: ").append(getName()).append(" -------------\n");
            sResults.append("ERRORED!").append("\n");

            if(getErrors() != null){
                for(String error : getErrors()){
                    sResults.append("Error: ").append(error).append("\n");
                }
            }

            sResults.append("--------------------------------------------------------");
            return sResults.toString();
        }

        return null;
    }
}