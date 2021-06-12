package com.ki11erwolf.shoppery.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

/**
 * A set of utility methods and classes that aid in
 * working with most rendering and graphics tasks.
 */
@OnlyIn(Dist.CLIENT)
public final class RenderUtil {

    //No Instantiate
    private RenderUtil() {}

    /**
     * Converts Minecraft frame render() times from
     * floats to milliseconds as an integer.
     *
     * @param frameRenderTime time taken for Minecraft
     * render method to render the frame, as a decimal.
     * @return the time taken to render the frame, in
     * milliseconds.
     */
    public static int frameTimeToMs(float frameRenderTime){
        return (int) (frameRenderTime * 50);
    }

    // ####################
    // Classes & Interfaces
    // ####################

    /**
     * A utility object for animation renderers that render
     * animations by repeatedly cycling through a predefined
     * set of animation frames, where each frame is rendered
     * for a specific amount of time (in milliseconds) before
     * the next is cycled and rendered.
     *
     * <p>Simply define a new TimedAnimationStepper and pass
     * in a frame count, frame render time, and a {@link
     * FrameChangedListener} callback. Once done, the renderer
     * simply needs to render the frame number it's told to by
     * the FrameChangedListener callback.
     *
     * <p>Then to play the animation, ensure {@link #onRender
     * (float)} is called every render cycle from the {@code
     * render()} method. The animation will pause on its current
     * frame as soon as {@code onRender()} is  no longer being
     * called.
     */
    public static class TimedAnimationStepper {

        /**
         * Change listener that enables the renderer to be
         * notified when to change frames and to what frame.
         */
        private final FrameChangedListener frameChangedListener;

        /**
         * The amount of milliseconds a frame should be rendered
         * on screen for before the next is cycled.
         */
        private final int millisecondsPerFrame;

        /**
         * The total number of frames in the animation sequence.
         */
        private final int amountOfFrames;

        /**
         * Internal: amount of time spent rendering the current
         * frame so far.
         */
        private int millisecondsOnFrame = 0;

        /**
         * Internal: the specific frame of the animation currently
         * being rendered.
         */
        private int frame = 0;

        /**
         * @param millisecondsPerFrame The amount of milliseconds a
         * frame should be rendered on screen for before the next
         * is cycled and rendered.
         * @param amountOfFrames number of frames in the
         * animation sequence. Must be 2 or more.
         * @param changeListener callback that enables the
         * renderer to be notified when to change frames and to
         * what frame.
         */
        public TimedAnimationStepper(int millisecondsPerFrame, int amountOfFrames, FrameChangedListener changeListener){
            if(amountOfFrames <= 1)
                throw new IllegalArgumentException("Minimum of 2 frames needed in render cycle.");
            if(millisecondsPerFrame <= 0)
                throw new IllegalArgumentException("Frame render time cannot be 0 or below.");

            this.frameChangedListener = Objects.requireNonNull(changeListener);
            this.millisecondsPerFrame = millisecondsPerFrame;
            this.amountOfFrames = amountOfFrames;
        }

        /**
         * <b>Needs to be called by the animation renderer, from the
         * renderers {@code render()} method.</b> for as long as the
         * animation should play.
         *
         * <p>Stop calling this method to pause or stop the animation.
         *
         * @param frameRenderTime the decimal float value given by
         * Minecraft's render methods that specifies how long every
         * frame took to render.
         */
        public void onRender(float frameRenderTime){
            //Count milliseconds elapsed during render
            millisecondsOnFrame += frameTimeToMs(frameRenderTime);

            //If set time has passed
            if(millisecondsOnFrame > millisecondsPerFrame) {
                frame++;//Increment animation frame
                millisecondsOnFrame = 0;//and reset frame time

                //Then reset frame if over frame count
                if(frame >= amountOfFrames) { frame = 0; }

                //Before finally notifying the actual rendering code of a frame change.
                frameChangedListener.onFrameChanged(frame);
            }
        }

        /**
         * Resets the animation back to its default starting frame.
         */
        public void reset(){
            this.millisecondsOnFrame = 0;
            this.frame = 0;
        }
    }

    /**
     * Interface used to receive callbacks from the
     * {@link TimedAnimationStepper} when the next
     * animation frame needs rendering.
     */
    public interface FrameChangedListener {

        /**
         * Called when the specific amount of render
         * time has passed and the next frame of the
         * animation needs rendering.
         *
         * @param frame the frame of the animation
         * to render.
         */
        void onFrameChanged(int frame);

    }

}
