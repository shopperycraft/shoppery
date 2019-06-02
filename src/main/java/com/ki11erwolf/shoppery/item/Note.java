package com.ki11erwolf.shoppery.item;

/**
 * Item class for every type of note.
 */
public class Note extends ShopperyItem<Note> {

    /**
     * Prefix for the name of every note item.
     */
    private static final String ITEM_NAME_PREFIX = "note_";

    /**
     * How much money this note is worth (worth > 0)
     */
    private final int worth;

    /**
     * Package private constructor to prevent
     * item instance creation from outside
     * packages.
     *
     * @param noteName the name of the specific
     *                 note (e.g. note_fifty)
     * @param worth how much money the note
     *              is worth (worth > 0).
     */
    Note(String noteName, int worth) {
        super(new Properties(), ITEM_NAME_PREFIX + noteName);

        if(worth < 0)
            throw new IllegalArgumentException("Invalid worth of note (worth < 0). Worth: " + worth);

        this.worth = worth;
    }

    /**
     * @return how much money this note is worth (worth > 0)
     */
    public int getWorth() {
        return worth;
    }
}
