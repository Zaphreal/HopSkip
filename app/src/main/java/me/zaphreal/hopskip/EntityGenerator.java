package me.zaphreal.hopskip;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class EntityGenerator {

    final float blockW, blockH;
    final Context context;

    public EntityGenerator(final float blockW, final float blockH, final Context context) {
        this.blockW = blockW;
        this.blockH = blockH;
        this.context = context;
    }

    void generate(Entity entity) {
        ImageView v = entity.getView();

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams((int) entity.getWidth() + 1, (int) entity.getHeight());
        v.setLayoutParams(params);
        v.setX(GameActivity.getLastColumnLayout().getX() + ((blockW - entity.getWidth())/2) + (blockW * entity.getxOffset()));
        v.setY(((blockH - entity.getHeight())/2) + (blockH * entity.getyOffset()));
        if (entity.getType().contains("sync")) {
            // will sync moving platform with speed and location of moving platform DIRECTLY on left
            Entity prevEntity = GameActivity.getPrevEntity(entity);
            v.setY(prevEntity.getView().getY());
            entity.setScaleVelocity(prevEntity.getScaleVelocity());
        }
        if (entity.getType().contains("platform")) {
            v.setY(blockH * entity.getyOffset());
        }
        //rl.addView(v);
        entity.onScreen = true;
    }
}
