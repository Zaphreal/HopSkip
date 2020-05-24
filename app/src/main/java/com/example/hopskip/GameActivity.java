package com.example.hopskip;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    final int DELAY = 20;
    final float ACC = 0.1f;
    public static final int NUM_BLOCKS_X = 12;
    public static final int NUM_BLOCKS_Y = 8;

    Handler handler = new Handler();
    Handler scrollHandler = new Handler();
    Runnable playerRunnable, scrollRunnable;
    float vX, vY, blockW, blockH, scrollSpeed, scrollAccel;
    int floorHeight, screenHeight, screenWidth;

    ArrayList<Block[]> blockList = new ArrayList<>();
    ArrayList<RelativeLayout> columnLayouts = new ArrayList<>();

    ConstraintLayout screen;
    RelativeLayout rl;
    ImageView pView;
    ImageView indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        screen = findViewById(R.id.background);

        rl = findViewById(R.id.block_layout);
        ViewGroup.LayoutParams params = rl.getLayoutParams();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;

        rl.setLayoutParams(params);

        blockW = (float)metrics.widthPixels / NUM_BLOCKS_X;
        blockH = (float)metrics.heightPixels / NUM_BLOCKS_Y;

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        indicator = new ImageView(this);
        indicator.setImageDrawable(getDrawable(R.drawable.indicator));
        indicator.setVisibility(View.VISIBLE);
        //Set player character to selected image
        pView = findViewById(R.id.player);
        pView.setImageDrawable(getDrawable(R.drawable.frog));
        pView.setOnTouchListener(this);

        initBlockList();
        generateBlocks();

        scrollSpeed = -0.15f;
        scrollAccel = -0.05f;
        //increaseScrollVelocity();
        handleScroll();
        handlePhysics();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

    }

    @Override
    protected void onResume() {
        // handle physics when game starts up or resumes
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        handlePhysics();
        super.onResume();
    }

    // If onPause() is not included the threads will double up when you
    // reload the activity

    @Override
    protected void onPause() {
        // stop handling physics when game tabs out
        handler.removeCallbacks(playerRunnable);
        scrollHandler.removeCallbacks(scrollRunnable);
        super.onPause();
    }

    //---------------------------Block Generation-----------------------------\\

    private void initBlockList() {
//        for (int x = 0; x < blockList.length; x++) {
//            for (int y = 7; y < blockList[x].length; y++) { // SET BACK TO ZERO POTENTIALLY
//                ImageView v = new ImageView(this);
//                v.setImageResource(R.drawable.grass);
//                v.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                blockList[x][y] = new Block(blockW, blockH, v);
//            }
//        }
        BlockColumnGenerator gen = new BlockColumnGenerator(blockW, blockH, this);
        blockList.add(gen.generate(new String[]{"grass", "dirt"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "", "grass", "dirt"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "", "", "grass", "dirt"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "", "grass", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "grass", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "grass", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
    }

    private void generateBlocks() {
        for (int x = 0; x < NUM_BLOCKS_X; x++) {
            RelativeLayout layout = new RelativeLayout(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)blockW, screenHeight);
            layout.setLayoutParams(layoutParams);
            layout.setX(x * blockW);
            layout.setY(0); //POSSIBLY REMOVE?

            for (int y = 0; y < NUM_BLOCKS_Y; y++) {
                if (blockList.get(x)[y] != null) {
                    ImageView block = blockList.get(x)[y].getView();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) blockList.get(x)[y].getWidth(), (int) blockList.get(x)[y].getHeight());
                    block.setLayoutParams(params);
                    //block.setX(x * blockW);
                    block.setY(y * blockH);
                    layout.addView(block);
                    //rl.addView(block);
                    //System.out.println("Block [x=" + x + "][y=" + y + "] = " + x * blockW + ", " + y * blockH);
                }
            }
            rl.addView(layout);
            columnLayouts.add(layout);
        }
    }

    //----------------------------Block Physics-------------------------------\\

    private void handleScroll() {
        scrollHandler.postDelayed(scrollRunnable = new Runnable() {
            @Override
            public void run() {

//                boolean firstColumnEmpty = false;
//                for (int i = columnLayouts.size() - 1; i >= 0; i--) {
//                    RelativeLayout layout = columnLayouts.get(i);
//                    if (layout.getX() > -blockW) {
//                        layout.animate()
//                                .x(layout.getX() + scrollSpeed)
//                                .y(layout.getY())
//                                .setDuration(0)
//                                .start();
//                    } else {
//                        firstColumnEmpty = true;
//                    }
//                }
//
//                if (firstColumnEmpty && !blockList.isEmpty()) {
//                    blockList.remove(0);
//                }
                scrollHandler.postDelayed(scrollRunnable, DELAY);
            }
        }, DELAY);
    }

    private void increaseScrollVelocity() {
        scrollHandler.postDelayed(new Runnable() {
            public void run() {
                scrollSpeed += scrollAccel;
                System.out.println("Scroll speed increased to " + scrollSpeed);
                increaseScrollVelocity();
            }
        }, 3000);
    }

    //----------------------------Player Physics------------------------------\\

    private void handlePhysics() {
        handler.postDelayed(playerRunnable = new Runnable() {
            public void run() {
                //scrollSpeed += scrollAccel;

//                for (int x = 0; x < blockList.size(); x++) {
//
//                    for (int y = 0; y < blockList.get(x).length; y++) {
//                        if (blockList.get(x)[y] != null) {
//                            Block block = blockList.get(x)[y];
//                            if (block.getView().getX() > -block.getWidth()) {
//                                block.getView().animate()
//                                        .x(block.getView().getX() + block.getScaleVelocity()[0] * scrollSpeed)
//                                        .y(block.getView().getY() + block.getScaleVelocity()[1])
//                                        .setDuration(0)
//                                        .start();
//
//                                if (x == 0) {
//                                    firstColumnEmpty = false;
//                                }
//                            }
//                        }
//                    }
//                }
                if (pView.getX() + pView.getWidth() + vX > screenWidth) {
                    vX = 0;
                }
                if (onGround() && vY > 0) {
                    vX = 0;
                    vY = 0;
                    //return;
                } else {
                    vY += ACC;
                }
                pView.animate()
                        .x(pView.getX() + vX + scrollSpeed)
                        .y(pView.getY()+pView.getHeight()+vY < floorHeight ? pView.getY() + vY: floorHeight - pView.getHeight())
                        .setDuration(0)
                        .start();

                // DIVIDER
                boolean firstColumnEmpty = false;
                for (int i = columnLayouts.size() - 1; i >= 0; i--) {
                    RelativeLayout layout = columnLayouts.get(i);
                    if (layout.getX() > -blockW) {
                        layout.animate()
                                .x(layout.getX() + scrollSpeed)
                                .y(layout.getY())
                                .setDuration(0)
                                .start();
                    } else {
                        firstColumnEmpty = true;
                    }
                }

                if (firstColumnEmpty && !blockList.isEmpty()) {
                    blockList.remove(0);
                }
                // DIVIDER
                handler.postDelayed(playerRunnable, DELAY);
            }
        }, DELAY);
    }

    public void hitCeiling() {
        vY = 0;
    }

    public void hitWall() {
        vX *= -0.2;
    }

    /**
     * Handles all collision detection and returns if the player is on a block
     * @return true if player on top of a block, false otherwise
     */
    public boolean onGround() {
        float pX = pView.getX();
        float pY = pView.getY();
        float pWidth = pView.getWidth();
        float pHeight = pView.getHeight();
        floorHeight = screenHeight;

        for (RelativeLayout layout : columnLayouts) {      // iterate through all views on screen
            for (int i = 0; i < layout.getChildCount(); i++) {
                ImageView v = (ImageView)layout.getChildAt(i);

                // skip view if (Block right < player left) OR (player right < block left)
                if (layout.getX() + v.getWidth() < pX || pX + pWidth < layout.getX()) {
                    break;        // if column more than a block away, skip it
                }

                //if player bottom is "lower" than block top, it is a potential wall and cannot be floor
                if (pY + pHeight > v.getY()) {
                    // if player top is lower than block bottom
                    if (pY >= v.getY() + v.getHeight()) {
                        continue;
                    }

                    // if moving up and player top is within bottom 20% of block
                    if (vY <= 0 && pY < v.getY() + v.getHeight() && pY > v.getY() + v.getHeight() * 0.8) {
                        //System.out.println("CEILING HIT: pY = " + pY + ", bBottom = " + v.getY() + v.getHeight());
                        pView.setY(v.getY() + v.getHeight());
                        hitCeiling();
                        continue;
                    }
                    // if (moving right AND player right > block left within left 20% of block)
                    if (vX > 0 && pX + pWidth > layout.getX() && pX + pWidth < layout.getX() + v.getWidth() * 0.2) {

                        pView.setX(layout.getX() - pWidth);
                        hitWall();
                        //System.out.println("RIGHT WALL HIT: pRight = " + pX + pWidth + ", bLeft = " + layout.getX());
                    }
                    // else if (moving left and player left < block right within 20% of block)
                    else if (vX < 0 && pX < layout.getX() + v.getWidth() && pX > layout.getX() + v.getWidth() * 0.8) {
                        pView.setX(layout.getX() + v.getWidth());
                        hitWall();
                        //System.out.println("LEFT WALL HIT: pLeft = " + pX + ", bRight = " + layout.getX() + v.getWidth());
                    }
                }
                // else if block height is higher than current highest block under player, update to block
                else if ((int) v.getY() < floorHeight) {
                    floorHeight = (int) v.getY();
                }
            }
        }
        return pView.getY() + pView.getHeight() >= floorHeight;// if not touching anything, return false
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!onGround()) {
            return false;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(130, 130);
                indicator.setLayoutParams(params);
                indicator.setX(event.getRawX());
                indicator.setY(event.getRawY());
                rl.addView(indicator);
                indicator.bringToFront();
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                rl.removeView(indicator);

                float dx = (v.getX() + ((float)v.getWidth()/2)) - event.getRawX(); // inverted dx: origX - targetX
                float dy = (v.getY() + ((float)v.getHeight()/2)) - event.getRawY(); // inverted dy: origY - targetY
                vX = dx*0.025f;                           // x-velocity
                vY = dy*0.05f;                          // y-velocity

                handlePhysics();
                break;

            case MotionEvent.ACTION_MOVE:
                indicator.bringToFront();
                indicator.animate()
                        .x(event.getRawX())
                        .y(event.getRawY())
                        .setDuration(0)
                        .start();
                break;

            default:
                return false;
        }
        return true;
    }
}
