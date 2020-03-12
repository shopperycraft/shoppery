package com.ki11erwolf.shoppery.util;

import net.minecraft.client.resources.I18n;

/**
 * Some utilities to aid in working with localization
 * and localization files.
 */
public class LocaleUtil {

    /**
     * Allows defining additional, external locale
     * domains. A locale domain is a grouping of
     * related translations, e.g.
     * <b>{@code item.shoppery}</b>
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
    }

    /**
     * Gets a translation from the currently selected (by player)
     * translation(lang) file, using the specified domain and
     * identifier to match the translation. Then {@link
     * String#format(String, Object...) formatted} with the given
     * parameters.
     *
     * <p/>Wrapper for {@link I18n#format(String, Object...)}.
     *
     * @param domain the translations grouping, e.g. {@code item.shoppery}.
     * @param identifier the translations identifier within the grouping.
     * @param params parameters used to
     * {@link String#format(String, Object...) format} the translation.
     * @return the matched and formatted translation, or an error message
     * detailing why no translation was returned.
     */
    public static String format(LocaleDomain domain, String identifier, Object... params){
        return I18n.format(domain.getDomain() + identifier, params);
    }

    /**
     * Gets a translation from the currently selected (by player)
     * translation(lang) file, using the specified domain and
     * identifier to match the translation.
     *
     * <p/>Wrapper for {@link I18n#format(String, Object...)} without
     * arguments.
     *
     * @param domain the translations grouping, e.g. {@code item.shoppery}.
     * @param identifier the translations identifier within the grouping.
     * @return the matched translation, or an error message
     * detailing why no translation was returned.
     */
    public static String get(LocaleDomain domain, String identifier){
        return I18n.format(domain.getDomain() + identifier);
    }
}
