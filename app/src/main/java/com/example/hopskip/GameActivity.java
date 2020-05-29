package com.example.hopskip;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    final int DELAY = 10;
    final float ACC = 2f;
    public static final int NUM_BLOCKS_X = 12;
    public static final int NUM_BLOCKS_Y = 8;

    Handler handler = new Handler();
    Runnable playerRunnable;
    float vX, vY, blockW, blockH, scrollSpeed, scrollAccel;
    int screenHeight, screenWidth, floorHeight;
    long gameTimeInMilliseconds = 0;
    float distanceScore = 0, backtrack = 0;
    int coinsCollected = 0;
    boolean gamePaused = false, userTouchingPlayer = false;

    BlockColumnGenerator gen;
    ArrayList<Block[]> blockList = new ArrayList<>();
    ArrayList<RelativeLayout> columnLayouts = new ArrayList<>();
    String[][] currStruct;
    int indexOfStructure = -1, columnIndex = 0;

    RelativeLayout rl;
    ImageView pView, indicator;
    TextView distanceScoreView;
    LinearLayout rightMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        blockW = (float)screenWidth / NUM_BLOCKS_X;
        blockH = (float)screenHeight / NUM_BLOCKS_Y;

        rl = findViewById(R.id.block_layout);
        ViewGroup.LayoutParams params = rl.getLayoutParams();
        params.width = screenWidth;
        params.height = screenHeight;
        rl.setLayoutParams(params);

        gen = new BlockColumnGenerator(blockW, blockH, this);
        initBlockList();

        indicator = new ImageView(this);
        indicator.setImageDrawable(getDrawable(R.drawable.indicator));

        distanceScoreView = findViewById(R.id.distance_score);
        distanceScoreView.setWidth((int)(blockW * 1.25));
        distanceScoreView.setHeight((int)blockH);

        rightMenu = findViewById(R.id.right_menu);
        ViewGroup.LayoutParams rightMenuParams = rightMenu.getLayoutParams();
        rightMenuParams.width = (int)(blockW*2.5);
        rightMenu.setLayoutParams(rightMenuParams);

        ImageView pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    togglePauseMenu();
                    return true;
                }
                return event.getAction() == MotionEvent.ACTION_DOWN;
            }
        });

        pView = findViewById(R.id.player);
        pView.setImageDrawable(getDrawable(R.drawable.frog));
        pView.setOnTouchListener(this);
        ConstraintLayout.LayoutParams playerSizeParams = (ConstraintLayout.LayoutParams)pView.getLayoutParams();
        playerSizeParams.width = (int)(blockW * 0.9);
        playerSizeParams.height = (int)(blockH * 0.9);
        pView.setLayoutParams(playerSizeParams);

        scrollAccel = -0.2f;
        scrollSpeed = scrollAccel;
        handlePhysics();
    }

    // If onPause() is not included the threads will double up when you
    // reload the activity

    @Override
    protected void onPause() {
        // stop handling physics when game tabs out
//        if (!gamePaused) {
//            togglePauseMenu();
//        }
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus && handler.hasCallbacks(playerRunnable)) {
            if (!gamePaused) {
                togglePauseMenu();
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onBackPressed() {
        // generate or destroy pause menu, then handle accordingly
        togglePauseMenu();
    }

    private void togglePauseMenu() {
        if (!gamePaused) {

            handler.removeCallbacks(playerRunnable);

            float menuHeight = screenHeight * 0.95f;
            RelativeLayout pauseLayout = new RelativeLayout(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
            pauseLayout.setLayoutParams(params);
            pauseLayout.setId(90000000);
            rl.addView(pauseLayout);

            ImageView filter = new ImageView(this);
            filter.setImageResource(R.drawable.dark_filter);
            filter.setScaleType(ImageView.ScaleType.FIT_XY);
            filter.setLayoutParams(params);
            pauseLayout.addView(filter);

            ImageView pauseMenu = new ImageView(this);
            pauseMenu.setImageResource(R.drawable.pause_menu);
            pauseMenu.setScaleType(ImageView.ScaleType.FIT_XY);
            params = new RelativeLayout.LayoutParams((int) (menuHeight / 1.5f), (int) menuHeight);
            pauseMenu.setLayoutParams(params);
            pauseLayout.addView(pauseMenu);
            pauseMenu.setX(((float) screenWidth / 2f) - ((menuHeight / 1.5f) / 2f));
            pauseMenu.setY(((float) screenHeight / 2f) - (menuHeight / 2f));

            final ImageView resumeButton = new ImageView(this);
            resumeButton.setImageResource(R.drawable.button_resume);
            resumeButton.setScaleType(ImageView.ScaleType.FIT_XY);
            params = new RelativeLayout.LayoutParams((int) (((7f / 9) * menuHeight) / 1.5f), (int) ((4f / 27) * menuHeight));
            resumeButton.setLayoutParams(params);
            pauseLayout.addView(resumeButton);
            resumeButton.setX(((float) screenWidth / 2) - ((((7f / 9) * menuHeight) / 1.5f) / 2f));
            resumeButton.setY(((float) screenHeight / 2) - (menuHeight / 2) + ((5f / 18) * menuHeight));
            resumeButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        togglePauseMenu();
                        return true;
                    }
                    return event.getAction() == MotionEvent.ACTION_DOWN;
                }
            });

            ImageView restartButton = new ImageView(this);
            restartButton.setImageResource(R.drawable.button_restart);
            restartButton.setScaleType(ImageView.ScaleType.FIT_XY);
            params = new RelativeLayout.LayoutParams((int) (((7f / 9) * menuHeight) / 1.5f), (int) ((4f / 27) * menuHeight));
            restartButton.setLayoutParams(params);
            pauseLayout.addView(restartButton);
            restartButton.setX(((float) screenWidth / 2) - ((((7f / 9) * menuHeight) / 1.5f) / 2f));
            restartButton.setY(((float) screenHeight / 2) - (menuHeight / 2) + ((14f / 27) * menuHeight));
            restartButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        recreate();
                        return true;
                    }
                    return event.getAction() == MotionEvent.ACTION_DOWN;
                }
            });

            ImageView quitButton = new ImageView(this);
            quitButton.setImageResource(R.drawable.button_quit);
            quitButton.setScaleType(ImageView.ScaleType.FIT_XY);
            params = new RelativeLayout.LayoutParams((int) (((7f / 9) * menuHeight) / 1.5f), (int) ((4f / 27) * menuHeight));
            quitButton.setLayoutParams(params);
            pauseLayout.addView(quitButton);
            quitButton.setX(((float) screenWidth / 2) - ((((7f / 9) * menuHeight) / 1.5f) / 2f));
            quitButton.setY(((float) screenHeight / 2) - (menuHeight / 2) + ((41f / 54) * menuHeight));
            quitButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        finish();
                        return true;
                    }
                    return event.getAction() == MotionEvent.ACTION_DOWN;
                }
            });
            gamePaused = true;

        } else {
            rl.removeView(findViewById(90000000));
            gamePaused = false;
            handlePhysics();
        }
    }

    //---------------------------Block Generation-----------------------------\\

    private void initBlockList() {
        blockList.add(gen.generate(new String[]{"dirt"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "", "grass", "dirt"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "", "", "grass", "dirt"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "", "grass", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "grass", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "grass", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "", "coin", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "coin", "grass", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "move_brick", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "coin", "", "", "", "", "", "grass"}));
        blockList.add(gen.generate(new String[]{"", "", "", "", "", "", "", "grass"}));
        for (int x = 0; x < NUM_BLOCKS_X + 2; x++) {
            generateBlockColumn(x);
        }
    }

    /**
     * Uses indexing system where 0 is leftmost column on screen to generate a RelativeLayout column of
     * Block ImageViews.
     *
     * @param idx the column index to spawn the block, where 0 is the leftmost column on screen
     */
    private void generateBlockColumn(int idx) {
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)blockW + 1, screenHeight);
        layout.setLayoutParams(layoutParams);
        layout.setX(idx * blockW);

        buildColumn(layout, idx);
    }

    private void appendBlockColumn() {
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)blockW + 1, screenHeight);
        layout.setLayoutParams(layoutParams);
        layout.setX(columnLayouts.get(columnLayouts.size() - 1).getX() + blockW);

        buildColumn(layout, blockList.size() - 1);
    }

    private void buildColumn(RelativeLayout layout, int index) {
        for (int y = 0; y < NUM_BLOCKS_Y; y++) {
            Block block = blockList.get(index)[y];
            if (block != null) {
                ImageView blockImg = block.getView();

                RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams((int) block.getWidth() + 1, (int) block.getHeight());
                blockImg.setLayoutParams(params);
                blockImg.setX((blockW - block.getWidth())/2);
                blockImg.setY((y * blockH) + ((blockH - block.getHeight())/2));
                layout.addView(blockImg);
            }
        }
        rl.addView(layout);
        columnLayouts.add(layout);
    }

    private String[][] getStructure(int idx) {
        String[][] structure;
        switch (idx) {
            // basic heaven stairs
            case 1:
                structure = new String[10][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[1] = new String[]{"", "", "", "", "", "", "grass", "dirt"};
                structure[2] = new String[]{"", "", "", "", "", "", "", "air"};
                structure[3] = new String[]{"", "", "", "", "", "", "", "air"};
                structure[4] = new String[]{"", "", "", "", "", "grass", ""};
                structure[5] = new String[]{"", "", "", "", "", "grass", ""};
                structure[6] = new String[]{"", "", "", "", "", "", "", "air"};
                structure[7] = new String[]{"", "", "", "", "", "", "", "air"};
                structure[8] = new String[]{"", "", "", "", "grass", ""};
                structure[9] = new String[]{"", "", "", "", "grass", ""};
                break;
            // basic float pyramid
            case 2:
                structure = new String[10][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[1] = new String[]{"", "", "", "", "grass", "", "", "grass"};
                structure[2] = new String[]{"", "", "", "", "grass", "", "", "grass"};
                structure[3] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[4] = new String[]{"", "", "grass", "", "", "", "", "grass"};
                structure[5] = new String[]{"", "", "grass", "", "", "", "", "grass"};
                structure[6] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[7] = new String[]{"", "", "", "", "grass", "", "", "grass"};
                structure[8] = new String[]{"", "", "", "", "grass", "", "", "grass"};
                structure[9] = new String[]{"", "", "", "", "", "", "", "grass"};
                break;
            // hopscotch tower
            case 3:
                structure = new String[9][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[1] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[2] = new String[]{"brick1", "brick2", "brick1", "brick2", "brick1", "", "", "grass"};
                structure[3] = new String[]{"coin", "brick2", "", "", "brick1", "", "", "grass"};
                structure[4] = new String[]{"", "", "", "", "brick1", "", "", "grass"};
                structure[5] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[6] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[7] = new String[]{"", "", "brick1", "", "", "", "brick1", "grass"};
                structure[8] = new String[]{"brick1", "", "brick1", "brick2", "brick1", "brick2", "brick1", "brick2"};
                break;
            // mission:impossible
            case 4:
                structure = new String[26][NUM_BLOCKS_Y];
                structure[0] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[1] = new String[]{"", "", "", "", "", "grass", "", "grass"};
                structure[2] = new String[]{"", "", "", "", "", "grass", "", "grass"};
                structure[3] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[4] = new String[]{"", "", "", "grass", "", "", "", "grass"};
                structure[5] = new String[]{"", "", "", "grass", "", "", "", "grass"};
                structure[6] = new String[]{"", "", "", "", "", "", "", "grass"};
                structure[7] = new String[]{"brick1", "", "brick2", "brick1", "brick2", "brick1", "brick2", "brick1"};
                structure[8] = new String[]{"brick1", "", "", "", "", "", "", "brick1"};
                structure[9] = new String[]{"brick1", "", "", "", "", "", "", "brick1"};
                structure[10] = new String[]{"brick1", "", "", "", "", "", "", "brick1"};
                structure[11] = new String[]{"brick1", "brick2", "", "", "", "", "", "brick1"};
                structure[12] = new String[]{"brick1", "brick2", "", "", "", "move_brick", "", ""};
                structure[13] = new String[]{"brick1", "brick2", "", "", "", "move_brick", "", ""};
                structure[14] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[15] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[16] = new String[]{"brick1", "brick2", "", "", "move_brick", "", "", ""};
                structure[17] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[18] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[19] = new String[]{"brick1", "brick2", "", "", "", "move_brick", "", ""};
                structure[20] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[21] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[22] = new String[]{"brick1", "brick2", "", "", "", "", "", ""};
                structure[23] = new String[]{"brick1", "brick2", "", "", "", "", "", "brick1"};
                structure[24] = new String[]{"brick1", "brick2", "brick2", "brick1", "brick2", "", "", "brick1"};
                structure[25] = new String[]{"", "", "", "", "", "", "", "grass"};
                break;
            // flat earth, acts as case 0
            default:
                structure = new String[5][NUM_BLOCKS_Y];
                for (int i = 0; i < structure.length; i++) {
                    structure[i] = new String[]{"", "", "", "", "", "", "", "grass"};
                }
        }
        return structure;
    }

    //----------------------------Game Physics------------------------------\\

    private void handlePhysics() {
        handler.postDelayed(playerRunnable = new Runnable() {
            public void run() {
                // increase scroll speed every 5 seconds
                gameTimeInMilliseconds += DELAY;
                if (gameTimeInMilliseconds % 5000 == 0) {
                    scrollSpeed += scrollAccel;
                }

                if (pView.getY() >= screenHeight || pView.getX() <= -pView.getWidth()) {
                    playerDied();
                    return;
                }

                // if hits right edge of screen, stop x-velocity
                if (pView.getX() + (float)pView.getWidth()/2 + vX >= screenWidth) {
                    vX = 0;
                }
                // if hits top edge of screen, stop y-velocity
                if (vY < 0 && pView.getY() <= -1 * (float)pView.getHeight()/2) {
                    vY = 0;
                }
                if ((onMovingPlatform() && vX != 0 && !userTouchingPlayer)) {
                    vX = 0;
                }

                if ((onGround() && vY > 0)) {
                    vX = 0;
                    vY = 0;
                    //return;
                } else {
                    vY += ACC;
                }

                ArrayList<Animator> animList = new ArrayList<>();
                ObjectAnimator pAnimX = ObjectAnimator.ofFloat(pView, "X", pView.getX() + vX + scrollSpeed);
                ObjectAnimator pAnimY = ObjectAnimator.ofFloat(pView, "Y", pView.getY()+pView.getHeight()+vY < floorHeight ? pView.getY() + vY: floorHeight - pView.getHeight());
                animList.add(pAnimX);
                animList.add(pAnimY);

                if (vX < 0) {
                    backtrack += vX / 10;
                } else {
                    if (backtrack < 0) {
                        backtrack += vX / 10;
                        if (backtrack > 0) {
                            distanceScore += backtrack;
                            backtrack = 0;
                        }
                    } else {
                        distanceScore += vX / 10;
                    }
                }
                String text = "";
                for (int i = 0; i < 4 - String.valueOf((int)distanceScore).length(); i++) {
                    text += "0";
                }
                text += String.valueOf((int)distanceScore);
                distanceScoreView.setText(text);

                // DIVIDER
                for (int i = columnLayouts.size() - 1; i >= 0; i--) {
                    RelativeLayout layout = columnLayouts.get(i);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(layout, "X", layout.getX() + scrollSpeed);
                    animList.add(anim);
                    for (int j = 0; j < blockList.get(i).length; j++) {
                        Block block = blockList.get(i)[j];
                        if (block != null && block.getScaleVelocity()[1] != 0) {
                            block.getScaleVelocity()[3] += (float)1/blockH;
                            block.getScaleVelocity()[4] = (float)((-block.getScaleVelocity()[1] * (Math.PI/2)) * Math.cos((Math.PI/block.getScaleVelocity()[2]) * block.getScaleVelocity()[3]));
                            anim = ObjectAnimator.ofFloat(block.getView(), "Y",
                                    block.getView().getY() + block.getScaleVelocity()[4]);
                            animList.add(anim);
                        }
                    }
                }
                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(animList);
                animSet.setInterpolator(new LinearInterpolator());
                animSet.setDuration(0);
                animSet.start();

                if (!columnLayouts.isEmpty() && columnLayouts.get(0).getX() <= -2 * blockW) {
                    blockList.remove(0);
                    rl.removeView(columnLayouts.get(0));
                    columnLayouts.remove(0);

                    if (indexOfStructure == -1) {
                        Random rand = new Random();
                        indexOfStructure = rand.nextInt(5);
                        columnIndex = 0;
                        currStruct = getStructure(indexOfStructure);
                        //System.out.println("New structure generated: index = " + indexOfStructure);
                    }
                    blockList.add(gen.generate(currStruct[columnIndex]));
                    columnIndex++;
                    appendBlockColumn();

                    if (columnIndex == currStruct.length) {
                        indexOfStructure = -1;
                    }
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
        float marginX = pView.getWidth() * 0.1f;
        float marginY = pView.getHeight() * 0.1f;
        float pX = pView.getX() + marginX;    //hitbox is center 80% of frog
        float pY = pView.getY() + marginY;
        float pWidth = pView.getWidth() - (2 * marginX);
        float pHeight = pView.getHeight() - (2 * marginY);
        floorHeight = screenHeight + pView.getHeight() + 1;

        for (RelativeLayout layout : columnLayouts) {      // iterate through all column layouts
            for (int i = 0; i < layout.getChildCount(); i++) {
                ImageView v = (ImageView)layout.getChildAt(i);
                float viewX = (layout.getX() + ((float)layout.getWidth() - v.getWidth())/2);

                // skip entire column if (Block right < player left) OR (player right < block left)
                if (viewX + v.getWidth() < pX || pX + pWidth < viewX) {
                    continue;        // if column more than a block away, skip it
                }

                // skip image if the block is air
                if (v.getDrawable() == null) {
                    continue;
                }

                if (v.getTag() != null && v.getTag().equals("coin")) {
                    if (( (pY + pHeight > v.getY() && pY < v.getY() + v.getHeight()) &&
                                ((vY <= 0 && pY < v.getY() + v.getHeight() && pY > v.getY() + v.getHeight() * 0.5)
                                || (vX > 0 && pX + pWidth > viewX && pX + pWidth < viewX + v.getWidth() * 0.5)
                                || (vX < 0 && pX < viewX + v.getWidth() && pX > viewX + v.getWidth() * 0.5)))
                        || (pY + pHeight <= v.getY() &&
                                viewX + v.getWidth() >= pView.getX() + 0.3*pView.getWidth() && pView.getX() + 0.7*pView.getWidth() >= viewX)) {
                        System.out.println((vY <= 0 && pY < v.getY() + v.getHeight() && pY > v.getY() + v.getHeight() * 0.5));
                        System.out.println((vX > 0 && pX + pWidth > viewX && pX + pWidth < viewX + v.getWidth() * 0.5));
                        System.out.println((vX < 0 && pX < viewX + v.getWidth() && pX > viewX + v.getWidth() * 0.5));
                        v.setTag("");
                        layout.removeView(v);
                        for (int j = 0; j < blockList.get(i).length; j++) {
                            if (blockList.get(i)[j] != null && blockList.get(i)[j].getView().equals(v)) {
                                blockList.get(i)[j] = null;
                            }
                        }
                        collectCoin();
                        continue;
                    }
                }

                //if player bottom is "lower" than block top, it is a potential wall and cannot be floor
                if (pY + pHeight > v.getY()) {
                    // if player top is lower than block bottom
                    if (pY >= v.getY() + v.getHeight()) {
                        continue;
                    }

                    // if moving up and player top is within bottom 30% of block
                    if (vY <= 0 && pY < v.getY() + v.getHeight() && pY > v.getY() + v.getHeight() * 0.7) {
                        //System.out.println("CEILING HIT: pY = " + pY + ", bBottom = " + v.getY() + v.getHeight());

                        pView.setY(v.getY() + v.getHeight() - marginY);
                        hitCeiling();
                        continue;
                    }
                    // if (moving right AND player right > block left within left 30% of block)
                    if (vX > 0 && pX + pWidth > viewX && pX + pWidth < viewX + v.getWidth() * 0.3) {
                        pView.setX(viewX - pWidth);
                        hitWall();
                        System.out.println("RIGHT WALL HIT: pRight = " + pX + pWidth + ", bLeft = " + viewX);
                    }
                    // else if (moving left and player left < block right within 30% of block)
                    else if (vX < 0 && pX < viewX + v.getWidth() && pX > viewX + v.getWidth() * 0.7) {
                        if (v.getTag() != null && v.getTag().equals("coin")) {
                            layout.removeView(v);
                            for (int j = 0; j < blockList.get(i).length; j++) {
                                if (blockList.get(i)[j] != null && blockList.get(i)[j].getView().equals(v)) {
                                    blockList.get(i)[j] = null;
                                }
                            }
                            collectCoin();
                            continue;
                        }
                        pView.setX(viewX + v.getWidth() - marginX);
                        hitWall();
                        System.out.println("LEFT WALL HIT: pLeft = " + pX + ", bRight = " + viewX + v.getWidth());
                    }
                }
                // if not wall or ceiling and if block height is higher than current highest block under player, update to block
                else if (viewX + v.getWidth() >= pView.getX() + 0.3*pView.getWidth() && pView.getX() + 0.7*pView.getWidth() >= viewX) {
                    if ((int) v.getY() < floorHeight) {
                        floorHeight = (int) v.getY();
                    }
                }
            }
        }
        return pView.getY() + pView.getHeight() >= floorHeight;// if not touching anything, return false
    }

    public boolean onMovingPlatform() {
        float marginX = pView.getWidth() * 0.1f;
        float marginY = pView.getHeight() * 0.1f;
        float pX = pView.getX() + marginX;    //hitbox is center 80% of frog
        float pY = pView.getY() + marginY;
        float pWidth = pView.getWidth() - (2 * marginX);
        float pHeight = pView.getHeight() - (2 * marginY);
        floorHeight = screenHeight + pView.getHeight() + 1;

        for (int i = 0; i < blockList.size(); i++) {
            for (int j = 0; j < blockList.get(i).length; j++) {

                if (blockList.get(i)[j] == null) {
                    continue;
                }

                RelativeLayout layout = columnLayouts.get(i);
                ImageView v = blockList.get(i)[j].getView();
                float viewX = (layout.getX() + ((float)layout.getWidth() - v.getWidth())/2);

                // skip entire column if (Block right < player left) OR (player right < block left)
                if (viewX + v.getWidth() < pX || pX + pWidth < viewX) {
                    continue;        // if column more than a block away, skip it
                }

                // skip if air or if block top than player bottom
                if (v.getDrawable() != null && pY + pHeight <= v.getY()
                        && blockList.get(i)[j] != null && blockList.get(i)[j].getScaleVelocity()[1] != 0) {

                    // if block height is higher than current highest block under player, update to block
                    if (viewX + v.getWidth() >= pView.getX() + 0.3*pView.getWidth() && pView.getX() + 0.7*pView.getWidth() >= viewX) {

                        if ((int) v.getY() < floorHeight) {
                            if (v.getTag() != null && v.getTag().equals("coin")) {
                                layout.removeView(v);
                                blockList.get(i)[j] = null;
                                collectCoin();
                                continue;
                            }
                            floorHeight = (int)v.getY() - (int)(v.getHeight()*0.1f);
                        }
                    }
                }
            }
        }
        return pView.getY() + pView.getHeight() >= floorHeight;// if not touching anything, return false
    }

    private void collectCoin() {
        coinsCollected += 1;
        TextView coinCount = findViewById(R.id.coin_count);
        coinCount.setText(String.valueOf(coinsCollected));
    }

    private void playerDied() {
        handler.removeCallbacks(playerRunnable);

        final Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("score", distanceScoreView.getText());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1000);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!onGround() && !onMovingPlatform()) {
            return false;
        }
        double squareDist = Math.pow(event.getRawX() - (v.getX() + (float)v.getWidth()/2), 2)
                + Math.pow(event.getRawY() - (v.getY() + (float)v.getHeight()/2), 2);
        double angle = Math.atan2((event.getRawY() - (v.getY() + (float)v.getHeight()/2)),
                (event.getRawX() - (v.getX() + (float)v.getWidth()/2)));

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(130, 130);
                indicator.setLayoutParams(params);
                indicator.setX(event.getRawX() - (float)indicator.getWidth()/2);
                indicator.setY(event.getRawY() - (float)indicator.getHeight()/2);
                if (indicator.getParent() == null) {
                    rl.addView(indicator);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                rl.removeView(indicator);
                float dx, dy;
                if (squareDist < Math.pow(1*blockW, 2)) {
                    dx = (v.getX() + ((float) v.getWidth() / 2)) - event.getRawX(); // inverted dx: origX - targetX
                    dy = (v.getY() + ((float) v.getHeight() / 2)) - event.getRawY(); // inverted dy: origY - targetY
                } else {
                    dx = -1 * (float)(Math.cos(angle) * 1.5*blockW);
                    dy = -1 * (float)(Math.sin(angle) * 1.5*blockW);
                }
                vX = dx*0.10f;                           // x-velocity
                vY = dy*0.20f;                           // y-velocity
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userTouchingPlayer = false;
                    }
                }, DELAY);
                break;

            case MotionEvent.ACTION_MOVE:

                ObjectAnimator animX = ObjectAnimator.ofFloat(indicator, "X", squareDist < Math.pow(1.5*blockW, 2) ?
                        event.getRawX() - (float)indicator.getWidth()/2 :
                        (v.getX() + (float)v.getWidth()/2) + (float)(Math.cos(angle) * 1*blockW) - (float)indicator.getWidth()/2);
                ObjectAnimator animY = ObjectAnimator.ofFloat(indicator, "Y", squareDist < Math.pow(1.5*blockW, 2) ?
                        event.getRawY() - (float)indicator.getHeight()/2 :
                        (v.getY() + (float)v.getHeight()/2) + (float)(Math.sin(angle) * 1*blockW) - (float)indicator.getHeight()/2);
                AnimatorSet animSet= new AnimatorSet();
                animSet.playTogether(animX, animY);
                animSet.setDuration(0);
                animSet.setInterpolator(new AnticipateInterpolator());
                animSet.start();
                break;

            default:
                return false;
        }
        userTouchingPlayer = true;
        return true;
    }
}
