package com.ki11erwolf.shoppery.price.loaders;

import com.ki11erwolf.shoppery.price.ItemPrice;

/**
 * Base class for all shoppery registry loaders.
 * Provides the basics needed to build a loader.
 *
 * <p/>A loader is called once by the Price Registry
 * to get a list of ItemPrices to append to the
 * registry. This is how the registry constructs
 * itself.
 *
 * <p/>Defines a Results object for the loader,
 * a way to signal if a fatal error has occurred,
 * as well as a single method {@link #load()}
 * that is called by the registry to get the
 * ItemPrice entries from the loader.
 *
 * <p/>All loaders are destroyed and claimed
 * by the garbage collector after use.
 */
public abstract class Loader {

    /**
     * The results from calling the loader:
     * number of entries loaded, errors,
     * affected mods, ect. This object
     * may be overridden/reinitialized
     * by subclasses.
     */
    Results results;

    /**
     * Flag set to true by the loader
     * if a fatal error has occurred.
     *
     * <p/>If true, the loaders entries
     * will not be added to the registry
     * and the results will display an error.
     */
    private boolean errored;

    /**
     * Default constructor - creates a new
     * Results object for the loader.
     */
    Loader(){
        results = new Results();
    }

    /**
     * Flags this loader as a failed
     * loader.
     *
     * <p/>Once a loader has been flagged as
     * an errored loader, it's entries will
     * not be added to the registry and it's
     * result will display an error.
     *
     * <p/>This action cannot be undone.
     */
    void flagAsErrored(){
        this.errored = true;
        this.results.flagAsErrored();
    }

    /**
     * @return {@code true} if and only if this
     * loader has been flagged as an errored loader.
     */
    public boolean hasErrored(){
        return errored;
    }

    /**
     * @return the Results from called this loader.
     */
    public Results getResults(){
        return this.results;
    }

    /**
     * Called once by the price registry at load time
     * when it wants to load this loader and obtain
     * the entries from it.
     *
     * <p/>If an exception is thrown during the load process,
     * the loader will be flagged as an errored loader and its
     * entries will NOT be added to the registry.
     *
     * <p/>This method may return multiple prices for the same
     * item/block, however, only the last one returned will be
     * used.
     *
     * <p/>Any prices returned that don't give a price for a
     * valid block/item in the forge registries will be removed
     * by the registry cleaner.
     *
     * @return a list of all the ItemPrices obtained by this
     * loader that should be added to the price registry.
     */
    public abstract ItemPrice[] load() throws Exception;
}
