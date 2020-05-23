package com.example.hopskip;

import android.content.Context;
import android.widget.ImageView;

public class BlockColumnGenerator {

    final float width, height;
    final Context context;

    BlockColumnGenerator(final float blockW, final float blockH, final Context context) {
        width = blockW;
        height = blockH;
        this.context = context;
    }

    public Block[] generate(final String[] blockNames) {
        Block[] blockColumn = new Block[GameActivity.NUM_BLOCKS_Y];
        for (int i = 0; i < GameActivity.NUM_BLOCKS_Y; i++) {
            ImageView v = new ImageView(context);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);

            String name;
            if (i >= blockNames.length) {
                name = blockNames[blockNames.length - 1];
            } else {
                name = blockNames[i];
            }

            switch (name) {
                case "grass":
                    v.setImageResource(R.drawable.grass);
                    blockColumn[i] = new Block(width, height, new float[]{1.0f, 0}, v);
                    break;
                case "dirt":
                    v.setImageResource(R.drawable.dirt);
                    blockColumn[i] = new Block(width, height, new float[]{1.0f, 0}, v);
                    break;
                case "air":
                default:
            }
        }

        return blockColumn;
    }
}
