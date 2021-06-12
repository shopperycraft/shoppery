package com.ki11erwolf.shoppery.util;

import net.minecraft.client.resources.I18n;

/**
 * Allows defining additional, external locale
 * domains.
 *
 * <p/>A locale domain is a grouping of
 * related I18n translation key prefixes,
 * e.g. {@code item.shoppery.*}
 */
public interface LocaleDomain {


    /**
     * {@inheritDoc}
     * All domains are grouped under the default
     * domain: {@literal shoppery}.
     *
     * @return the locale domain as a unique key prefix
     * for use in .json lang files. E.g. <b>{@code
     * item.shoppery.} - always includes the
     * trailing separator.</b>
     */
    default String getDomain() {
        return String.format("%s%s.", LocaleDomains.DEFAULT.getDomain(), getName());
    }

    /**
     * @return the single identifying name of this
     * domain.
     */
    String getName();

    /**
     * Gets a translation matched to the domain object and
     * identifier, from the currently selected language
     * translations file. {@link String#format(String,
     * Object...) Formatted} with the given
     * parameters first.
     *
     * <p/>Wrapper for {@link I18n#format(String, Object...)}.
     *
     * @param identifier the translations identifier within the grouping.
     * @param params parameters used to
     * {@link String#format(String, Object...) format} the translation.
     * @return the matched and formatted translation, or an error message
     * detailing why no translation was returned.
     */
    default String format(String identifier, Object... params){
        return I18n.format(this.getDomain() + identifier, params);
    }

    /**
     * Gets a translation matched to the domain object and
     * identifier, from the currently selected language
     * translations file.
     *
     * <p/>Wrapper for {@link I18n#format(String, Object...)} without
     * arguments.
     *
     * @param identifier the translations identifier within the grouping.
     * @return the matched translation, or an error message
     * detailing why no translation was returned.
     */
    default String get(String identifier){
        return I18n.format(this.getDomain() + identifier);
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
    default LocaleDomain sub(LocaleDomain subDomain){
        return new LocaleDomainString(this, subDomain);
    }

    /**
     * A LocaleDomain implementation that allows us to create
     * strings of domains and sub domains as a new instance.
     *
     * @see #sub(LocaleDomain) sub( LocaleUtil.LocaleDomain )
     */
    class LocaleDomainString implements LocaleDomain {

        /**
         * The higher priority domain grouping.
         */
        private final LocaleDomain parent;

        /**
         * The lower priority domain grouped under
         * the parent domain.
         */
        private final LocaleDomain child;

        /**
         * Creates a single new domain comprised of a string
         * of smaller individual parent and child domains.
         *
         * @param parent the top level parent domain - the higher level grouping.
         * @param sub the lower level grouping sub domain.
         */
        private LocaleDomainString(LocaleDomain parent, LocaleDomain sub){
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