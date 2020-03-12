package com.ki11erwolf.shoppery.util;

import net.minecraft.client.resources.I18n;

/**
 * List of Shoppery {@link LocaleUtil.LocaleDomain}s.
 */
public enum LocaleDomains implements LocaleUtil.LocaleDomain {

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
            return this.name + ".";
        }
    },

    /**
     * Used to group all translations of titles.
     */
    TITLE("title"),

    /**
     * Used to group all translation done in relation
     * to GUI screens.
     */
    SCREEN("screen");

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
     * {@inheritDoc}
     * All domains are grouped under the
     * default domain: {@literal shoppery}.
     */
    @Override
    public String getDomain() {
        return String.format("%s%s.", DEFAULT.getDomain(), name);
    }

    /**
     * Allows stringing together multiple domains to create
     * sub domains. The sub domain is always append to the
     * right of the parent domain.
     *
     * @param subDomain the domain that will be grouped
     *                  under this domain as a sub domain
     * @return the new domain title made of the parent and
     * sub domain.
     */
    public LocaleUtil.LocaleDomain sub(LocaleUtil.LocaleDomain subDomain){
        return new LocaleDomainString(this, subDomain);
    }

    /**
     * @see LocaleUtil#format(LocaleUtil.LocaleDomain, String, Object...) LocaleUtil.format()
     */
    public static String format(LocaleUtil.LocaleDomain domain, String identifier, Object... params){
        return LocaleUtil.format(domain, identifier, params);
    }

    /**
     * @see LocaleUtil#get(LocaleUtil.LocaleDomain, String) LocaleUtil.get()
     */
    public static String get(LocaleUtil.LocaleDomain domain, String identifier){
        return I18n.format(domain.getDomain() + identifier);
    }

    /**
     * A LocaleDomain implementation that allows us to create
     * strings of domains and sub domains as a new instance.
     *
     * @see #sub(LocaleUtil.LocaleDomain) sub( LocaleUtil.LocaleDomain )
     */
    private static class LocaleDomainString implements LocaleUtil.LocaleDomain {

        /**
         * The higher priority domain grouping.
         */
        private final LocaleUtil.LocaleDomain parent;

        /**
         * The lower priority domain grouped under
         * the parent domain.
         */
        private final LocaleUtil.LocaleDomain child;

        /**
         * Creates a single new domain comprised of a string
         * of smaller individual parent and child domains.
         *
         * @param parent the top level parent domain - the higher level grouping.
         * @param sub the lower level grouping sub domain.
         */
        private LocaleDomainString(LocaleUtil.LocaleDomain parent, LocaleUtil.LocaleDomain sub){
            this.parent = parent; this.child = sub;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDomain() {
            return parent.getDomain() + child.getName() + ".";
        }

        /**
         * {@inheritDoc}
         * In the case of stringed domains, the name
         * consists of the parent and child names
         * concatenated together with a "$".
         */
        @Override
        public String getName() {
            return parent.getName() + "$" + child.getName();
        }
    }
}
