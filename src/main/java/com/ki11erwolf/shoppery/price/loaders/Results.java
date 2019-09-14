package com.ki11erwolf.shoppery.price.loaders;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.price.ItemPrice;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Results{

    private static final Logger LOG = ShopperyMod.getNewLogger();

    private String name;

    private int numberOfEntries = 0;

    private int numberOfReplacements = 0;

    private boolean errored = false;

    private List<String> invalidEntries = new ArrayList<>();

    private List<String> affectedMods = new ArrayList<>();

    private List<String> unaffectedMods = new ArrayList<>();

    private List<String> errors = new ArrayList<>();

    private List<ItemPrice> registered = new ArrayList<>();

    Results(){}

    // Getters & Setters

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

    public void logRegistered(ItemPrice itemPrice){
        this.registered.add(itemPrice);
    }

    public ItemPrice[] getRegistered(){
        if(registered.size() == 0)
            return null;

        return registered.toArray(new ItemPrice[0]);
    }

    //Printing

    public void print(){
        LOG.info(getAsStandardResultString());
        if(!errored)
            LOG.debug(getAsDebugResultString());
    }

    protected String getAsDebugResultString(){
        StringBuilder sResults = new StringBuilder("\n");
        sResults.append("------------- Debug Results: ").append(getName()).append(" -------------\n");

        sResults.append("Loaded entries:").append("\n");
        for(ItemPrice registeredIP : registered){
            sResults.append(registeredIP.getItem().toString());
            sResults.append(" -> ").append(registeredIP.toString()).append("\n");
        }

        sResults.append("--------------------------------------------------------");
        return sResults.toString();
    }

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