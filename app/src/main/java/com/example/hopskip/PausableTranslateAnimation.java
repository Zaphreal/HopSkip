package com.example.hopskip;

import android.view.animation.TranslateAnimation;

public class PausableTranslateAnimation extends TranslateAnimation {

    boolean paused = false;

    public PausableTranslateAnimation(float fromXDelta, float toXDelta,
                                      float fromYDelta, float toYDelta) {
        super(fromXDelta, toXDelta, fromYDelta, toYDelta);


    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }
}
