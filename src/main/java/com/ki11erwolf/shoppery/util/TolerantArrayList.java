package com.ki11erwolf.shoppery.util;

import com.ki11erwolf.shoppery.ShopperyMod;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link ArrayList} designed to ignore (throw away)
 * {@link IndexOutOfBoundsException}s that are thrown
 * by {@link ArrayList#get(int)}.
 *
 * This class was written to replace the players
 * slot gui list ({@link net.minecraft.inventory.Container#inventorySlots})
 * so that it doesn't flood the server log with exceptions caused
 * by the {@link com.ki11erwolf.shoppery.gui.MoneyGui}. An ugly
 * workaround but it works.
 *
 * @param <T> the element type the list hold.
 */
public class TolerantArrayList<T> extends ArrayList<T> {

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param original the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public TolerantArrayList(List<T> original){
        super(original);
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * or {@code null} if the index is out of bounds.
     * @throws IndexOutOfBoundsException not thrown by this method.
     */
    @Override
    public T get(int index) {
        try{
            return super.get(index);
        } catch (Exception e){
            //Log it on the debug level instead.
            ShopperyMod.getNewLogger().debug(
                    "Throwing away exception on TolerantArrayList.get(): "
                    + e.getClass().getName() + " - " + e.getMessage()
            );
        }

        return null;
    }
}
