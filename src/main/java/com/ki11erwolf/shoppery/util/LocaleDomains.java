package com.ki11erwolf.shoppery.util;

/**
 * List of Shoppery {@link LocaleDomain}.
 */
public enum LocaleDomains implements LocaleDomain {

    /**
     * No domain grouping.
     */
    DEFAULT("shoppery"){

        /**
         * {@inheritDoc}
         *
         * </p>Special case handles the
         * highest level domain.
         */
        @Override
        public String getDomain(){
            return this.getName() + ".";
        }
    },

    /**
     * Used to group all translations of titles.
     */
    TITLE("title"),

    /**
     * Used to group all translations done in relation
     * to GUI screens.
     */
    SCREEN("screen"),

    /**
     * Groups all error message translations.
     */
    ERROR("error"),

    /**
     * Groups all message translations related
     * to commands.
     */
    COMMAND("command"),

    /**
     * Holds the usage message translations for all commands.
     */
    USAGE("usage"),

    /**
     * Holds the description message translations for all commands.
     */
    DESCRIPTION("description"),

    /**
     * Groups all general and specific message translation.
     */
    MESSAGE("message");

    /**
     * @param name the single identifying
     * name of this domain.
     */
    LocaleDomains(String name){
        this.name = name;
    }

    /**
     * The single identifying name of this domain.
     */
    final String name;

    /**
     * @return The single identifying name of this domain.
     */
    @Override
    public String getName(){
        return name;
    }

    /**
     * Equivalent to {@link #getDomain()}.
     *
     * <p/>{@inheritDoc}
     *
     * @return the locale domain as a domain string
     * for use in .json lang files. E.g. <b>{@code item.shoppery.*}</b>
     */
    @Override
    public String toString(){
        return getDomain();
    }
}
