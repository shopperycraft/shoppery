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
     * @return the locale domain as a unique key prefix
     * for use in .json lang files. E.g. <b>{@code item.shoppery.}</b>
     *
     * <p/>Always includes the trailing separator.
     */
    String getDomain();

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
}