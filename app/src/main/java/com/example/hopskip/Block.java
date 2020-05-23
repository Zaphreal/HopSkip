package com.example.hopskip;

import android.widget.ImageView;

public class Block {
    private final float width, height;
    private final ImageView view;

    Block(float w, float h, ImageView blockImg) {
        width = w;
        height = h;
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
}
