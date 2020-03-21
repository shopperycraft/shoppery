package com.ki11erwolf.shoppery.util;

import java.util.function.Function;

/**
 * Utility class that limits multiple calls to a function
 * within a specific time frame. Prevents excessive calls
 * in a fast loop.
 */
public class WaitTimer {

    /**
     * The time (in milliseconds) to wait before another
     * call can be made.
     */
    private final int waitTime;

    /**
     * The time clocked when the last call
     * succeeded.
     */
    private long lastClockedTime;

    /**
     * Creates a new WaitTimer that will block
     * multiple calls if within the given wait
     * time.
     *
     * @param waitTime the time, in milliseconds,
     *                 to block multiple calls for.
     */
    public WaitTimer(int waitTime){
        this.waitTime = waitTime;

        this.clock();
        this.lastClockedTime -= waitTime;
    }

    /**
     * Single action method. Will execute the given
     * function and clock the time it was executed,
     * if and only if enough time has passed since
     * the last call to this method.
     *
     * @param action the function to call.
     */
    public void time(Function<Void, Void> action){
        if(this.hasLimitElapsed()){
            action.apply(null);
            this.clock();
        }
    }

    /**
     * Clocks the current time.
     */
    private void clock(){
        this.lastClockedTime = System.currentTimeMillis();
    }

    /**
     * Checks if enough time has passed
     * since the last call.
     *
     * @return {@code true} if the specific time has
     * passed since the last call and clock, {@code false}
     * otherwise.
     */
    private boolean hasLimitElapsed(){
        return System.currentTimeMillis() >= this.lastClockedTime + this.waitTime;
    }
}
