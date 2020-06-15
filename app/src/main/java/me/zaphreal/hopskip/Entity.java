package me.zaphreal.hopskip;

import android.content.Context;
import android.widget.ImageView;

public class Entity {
    private final String type;
    private final int columnIdx;
    private final float width, height, xOffset, yOffset;
    private final ImageView view;
    private float[] scaleVelocity;
    public boolean onScreen = false;

    Entity(Context context, String type, int columnIdx, float blockW, float blockH, float xOffset, float yOffset) {
        this.type = type;
        this.columnIdx = columnIdx;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        view = new ImageView(context);
        view.setScaleType(ImageView.ScaleType.FIT_XY);

        switch(type) {
            case "move_brick_sync":
            case "move_brick":
                view.setImageResource(R.drawable.brick1);
                // moving template: xScale, +/- num blocks to travel, time per half cycle, time variable, starting position
                width = blockW;
                height = blockH/2;
                scaleVelocity = new float[]{1.0f, 1.0f, 2.0f, 0, ((float)-Math.PI)/4};
                break;
            case "platform_wood":
                view.setImageResource(R.drawable.wood);
                view.setTag("platform");
                width = blockW;
                height = blockH/4;
                scaleVelocity = new float[]{1.0f, 0};
                break;
            case "coin":
            default:
                if (MainActivity.player.getDrawableID() == R.drawable.frog) {
                    view.setImageResource(R.drawable.coin_frog);
                } else if (MainActivity.player.getDrawableID() == R.drawable.bunny){
                    view.setImageResource(R.drawable.coin_bunny);
                } else {
                    // intentionally distracting resourse to indicate something was not handled in future
                    view.setImageResource(R.drawable.old_bunny);
                }
                view.setTag("coin");
                width = blockW/3;
                height = blockH/3;
                scaleVelocity = new float[]{1.0f, 0.2f, 0.5f, 0, 0};
                break;
        }
    }

    public String getType() { return type; }

    public int getColumnIdx() {
        return columnIdx;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public ImageView getView() {
        return view;
    }

    public float[] getScaleVelocity() {
        return scaleVelocity;
    }

    public void setScaleVelocity(float[] scaleVelocity) {
        this.scaleVelocity = scaleVelocity;
    }
}
