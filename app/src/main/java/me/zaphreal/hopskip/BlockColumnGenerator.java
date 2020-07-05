package me.zaphreal.hopskip;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;

import static me.zaphreal.hopskip.GameActivity.NUM_BLOCKS_Y;

public class BlockColumnGenerator {

    final float width, height;
    final Context context;

    BlockColumnGenerator(final float blockW, final float blockH, final Context context) {
        width = blockW;
        height = blockH;
        this.context = context;
    }

    public Block[] generate(final String[] blockNames) {
        Block[] blockColumn = new Block[NUM_BLOCKS_Y];
        for (int i = 0; i < NUM_BLOCKS_Y; i++) {
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
                case "wood":
                    v.setImageResource(R.drawable.wood);
                    blockColumn[i] = new Block(name, width, height, new float[]{1.0f, 0}, v);
                    break;
                case "bg_wood":
                    v.setImageResource(R.drawable.wood);
                    v.setTag("background");
                    v.setColorFilter(Color.argb(85, 0, 0, 0), PorterDuff.Mode.DARKEN);
                    blockColumn[i] = new Block(name, width, height, new float[]{1.0f, 0}, v);
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

    String[][] getStructure(int idx) {
        String[][] structure;
        switch (idx) {
            case 1:
                structure = new String[15][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[1] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[2] = new String[]{"", "", "", "", "", "", "", "grass", "dirt"};
                structure[3] = new String[]{"", "", "", "", "", "", "", "grass", "dirt"};
                structure[4] = new String[]{"", "", "", "", "", "", "", "grass", "dirt"};
                structure[5] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[6] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[7] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[8] = new String[]{"", "", "", "", "", "grass", "dirt"};
                structure[9] = new String[]{"", "", "", "", "", "grass", "dirt"};
                structure[10] = new String[]{"", "", "", "", "", "grass", "dirt"};
                structure[11] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[12] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[13] = new String[]{"", "", "", "grass", "", "", "", "", "grass", "dirt"};
                GameActivity.addEntity(new Entity(context, "coin", 13, width, height, 0.5f, 2));
                structure[14] = new String[]{"", "", "", "grass", "", "", "", "", "grass", "dirt"};
                break;
            case 2:
                structure = new String[10][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[1] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[2] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[3] = new String[]{"", "", "", "", "", "", "", "", "air"};
                structure[4] = new String[]{"", "", "", "", "", "", "", "", "air"};
                structure[5] = new String[]{"", "", "", "", "", "", "", "", "", "air"};
                GameActivity.addEntity(new Entity(context, "move_brick", 4, width, height, 0, 4));
                GameActivity.addEntity(new Entity(context, "move_brick_sync", 5, width, height, 0, 4));
                GameActivity.addEntity(new Entity(context, "move_brick", 4, width, height, 0, 8));
                GameActivity.addEntity(new Entity(context, "move_brick_sync", 5, width, height, 0, 8));
                structure[6] = new String[]{"", "", "", "", "", "", "",  "",  "", "air"};
                structure[7] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[8] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[9] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                break;
            // basic heaven stairs
            case 3:
                structure = new String[10][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "grass", "dirt"};
                structure[1] = new String[]{"", "", "", "", "", "", "", "grass", "dirt"};
                structure[2] = new String[]{"", "", "", "", "", "", "", "", "", "air"};
                structure[3] = new String[]{"", "", "", "", "", "", "", "", "", "air"};
                structure[4] = new String[]{"", "", "", "", "", "", "grass", ""};
                structure[5] = new String[]{"", "", "", "", "", "", "grass", ""};
                structure[6] = new String[]{"", "", "", "", "", "", "", "", "", "air"};
                structure[7] = new String[]{"", "", "", "", "", "", "", "", "", "air"};
                structure[8] = new String[]{"", "", "", "", "", "grass", ""};
                structure[9] = new String[]{"", "", "", "", "", "grass", ""};
                break;
            // basic float pyramid
            case 4:
                structure = new String[10][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[1] = new String[]{"", "", "", "", "", "grass", "", "", "grass", "dirt"};
                structure[2] = new String[]{"", "", "", "", "", "grass", "", "", "grass", "dirt"};
                structure[3] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[4] = new String[]{"", "", "", "grass", "", "", "", "", "grass", "dirt"};
                GameActivity.addEntity(new Entity(context, "coin", 4, width, height, 0.5f, 2));
                structure[5] = new String[]{"", "", "", "grass", "", "", "", "", "grass", "dirt"};
                structure[6] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[7] = new String[]{"", "", "", "", "", "grass", "", "", "grass", "dirt"};
                structure[8] = new String[]{"", "", "", "", "", "grass", "", "", "grass", "dirt"};
                structure[9] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                break;
            // hopscotch tower
            case 5:
                structure = new String[10][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[1] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[2] = new String[]{"brick1", "brick2", "brick1", "brick2", "brick1", "brick2", "bg_wood", "bg_wood", "wood", "dirt"};
                structure[3] = new String[]{"bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "wood", "dirt"};
                GameActivity.addEntity(new Entity(context, "coin", 3, width, height, 0, 1));
                GameActivity.addEntity(new Entity(context, "platform_wood", 3, width, height, 0,2));
                GameActivity.addEntity(new Entity(context, "platform_wood", 3, width, height, 0, 5));
                structure[4] = new String[]{"bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "wood", "dirt"};
                GameActivity.addEntity(new Entity(context, "platform_wood", 4, width, height, 0, 5));
                structure[5] = new String[]{"bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "wood", "dirt"};
                structure[6] = new String[]{"bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "wood", "dirt"};
                structure[7] = new String[]{"bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "bg_wood", "brick1", "wood", "dirt"};
                GameActivity.addEntity(new Entity(context, "platform_wood", 7, width, height, 0, 3));
                structure[8] = new String[]{"brick1", "brick2", "bg_wood", "brick2", "brick1", "brick2", "brick1", "brick2", "brick1", "dirt"};
                structure[9] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                break;
            // mission:impossible
            case 6:
                structure = new String[26][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[1] = new String[]{"", "", "", "", "", "grass", "", "", "grass", "dirt"};
                structure[2] = new String[]{"", "", "", "", "", "grass", "", "", "grass", "dirt"};
                structure[3] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[4] = new String[]{"", "", "", "", "grass", "", "", "", "grass", "dirt"};
                structure[5] = new String[]{"", "", "", "", "grass", "", "", "", "grass", "dirt"};
                structure[6] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                structure[7] = new String[]{"brick1", "brick2", "", "", "brick1", "brick2", "brick1", "brick2", "brick1", "brick2"};
                structure[8] = new String[]{"brick1", "brick2", "", "", "", "", "", "", "brick1", "brick2"};
                structure[9] = new String[]{"brick1", "brick2", "", "", "", "", "", "", "brick1", "brick2"};
                structure[10] = new String[]{"brick1", "brick2", "", "", "", "", "", "", "brick1", "brick2"};
                structure[11] = new String[]{"brick1", "brick2", "", "", "", "", "", "", "brick1", "brick2"};
                structure[12] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                GameActivity.addEntity(new Entity(context, "move_brick", 12, width, height, 0, 6));
                structure[13] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                GameActivity.addEntity(new Entity(context, "move_brick_sync", 13, width, height, 0, 6));
                structure[14] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[15] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[16] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                GameActivity.addEntity(new Entity(context, "move_brick", 16, width, height, 0, 5));
                structure[17] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[18] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[19] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                GameActivity.addEntity(new Entity(context, "move_brick", 19, width, height, 0, 7));

                structure[20] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[21] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[22] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[23] = new String[]{"brick1", "brick2", "", "", "", "", "", "", "brick1", "brick2"};
                structure[24] = new String[]{"brick1", "brick2", "brick2", "brick1", "brick2", "brick1", "", "", "brick1", "brick2"};
                structure[25] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                break;
            // flat earth, acts as case 0
            default:
                structure = new String[5][NUM_BLOCKS_Y];
                for (int i = 0; i < structure.length; i++) {
                    structure[i] = new String[]{"", "", "", "", "", "", "", "", "grass", "dirt"};
                }
        }
        return structure;
    }
}
