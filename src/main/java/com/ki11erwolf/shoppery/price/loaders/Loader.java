package com.ki11erwolf.shoppery.price.loaders;

import com.ki11erwolf.shoppery.price.ItemPrice;

public abstract class Loader {

    @SuppressWarnings("WeakerAccess")
    protected Results results;

    private boolean errored;

    Loader(){
        results = new Results();
    }

    void flagAsErrored(){
        this.errored = true;
        this.results.flagAsErrored();
    }

    public boolean hasErrored(){
        return errored;
    }

    public Results getResults(){
        return this.results;
    }

    public abstract ItemPrice[] load();
}
