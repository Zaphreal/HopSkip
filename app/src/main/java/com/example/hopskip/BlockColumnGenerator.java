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
            v.setScaleType(ImageView.ScaleType.FIT_XY);

            String name;
            if (i >= blockNames.length) {
                name = blockNames[blockNames.length - 1];
            } else {
                name = blockNames[i];
            }

            switch (name) {
                case "grass":
                    v.setImageResource(R.drawable.grass);
                    blockColumn[i] = new Block(name, width, height, new float[]{1.0f, 0}, v);
                    break;
                case "dirt":
                    v.setImageResource(R.drawable.dirt);
                    blockColumn[i] = new Block(name, width, height, new float[]{1.0f, 0}, v);
                    break;
                case "brick1":
                    v.setImageResource(R.drawable.brick1);
                    blockColumn[i] = new Block(name, width, height, new float[]{1.0f, 0}, v);
                    break;
                case "brick2":
                    v.setImageResource(R.drawable.brick2);
                    blockColumn[i] = new Block(name, width, height, new float[]{1.0f, 0}, v);
                    break;
                case "move_brick_sync":
                case "move_brick":
                    v.setImageResource(R.drawable.brick1);
                    // moving template: xScale, +/- num blocks to travel, time per half cycle, time variable, starting position
                    blockColumn[i] = new Block(name, width, (height/2), new float[]{1.0f, 1.0f, 2.0f, 0, ((float)-Math.PI)/4}, v);
                    break;
                case "coin":
                    v.setImageResource(R.drawable.coin_frog);
                    v.setTag("coin");
                    blockColumn[i] = new Block(name, width/3, height/3, new float[]{1.0f, 0.2f, 0.5f, 0, 0}, v);
                    break;
                case "air":
                    // creates an ImageView with a null image resource, will be ignored by player
                    blockColumn[i] = new Block(name, width, height, new float[]{1.0f, 0}, v);
                    break;
                default:
            }
        }

        return blockColumn;
    }
}
