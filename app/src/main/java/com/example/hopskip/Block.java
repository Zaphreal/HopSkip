package com.example.hopskip;

import android.widget.ImageView;

public class Block {
    private final float width, height;
    private final ImageView view;
    private float[] scaleVelocity;

    Block(float width, float height, float[] scaleVelocity, ImageView blockImg) {
        this.width = width;
        this.height = height;
        this.scaleVelocity = scaleVelocity;
        view = blockImg;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public ImageView getView() {
        return view;
    }

    public float[] getScaleVelocity() {
        return scaleVelocity;
    }
}
