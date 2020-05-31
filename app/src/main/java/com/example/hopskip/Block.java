package com.example.hopskip;

import android.widget.ImageView;

public class Block {
    private final String type;
    private final float width, height;
    private final ImageView view;
    private final float[] scaleVelocity;

    Block(String type, float width, float height, float[] scaleVelocity, ImageView blockImg) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.scaleVelocity = scaleVelocity;
        view = blockImg;
    }

    public String getType() { return type; }

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
