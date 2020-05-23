package com.example.hopskip;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    final int DELAY = 10;

    final float ACC = 1f;

    Handler handler = new Handler();
    Runnable runnable;
    float vX, vY;
    public int floorHeight;

    ConstraintLayout screen;
    RelativeLayout rl;
    ImageView pView;
    ImageView indicator;
    View bottomHitbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        screen = findViewById(R.id.background);
        bottomHitbox = findViewById(R.id.bottom_hitbox);

        rl = findViewById(R.id.indicator_box);
        ViewGroup.LayoutParams params = rl.getLayoutParams();
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        params.width = size.x + 170;
        params.height = size.y + 170;
        rl.setLayoutParams(params);

        indicator = new ImageView(this);
        indicator.setImageDrawable(getDrawable(R.drawable.indicator));
        indicator.setVisibility(View.VISIBLE);
        //Set player character to selected image
        pView = findViewById(R.id.player);
        pView.setImageDrawable(getDrawable(R.drawable.frog));
        pView.setOnTouchListener(this);
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        handlePhysics();
        super.onResume();
    }

// If onPause() is not included the threads will double up when you
// reload the activity

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    public void handlePhysics() {
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                if (pView.getX() +pView.getWidth() + vX > screen.getRight()) {
                    vX = 0;
                }
                if (onGround() && vY > 0) {
                    vX = 0;
                    vY = 0;
                    return;
                }
                vY += ACC;
                pView.animate()
                        .x(pView.getX() + vX)
                        .y(pView.getY()+pView.getHeight()+vY < floorHeight ? pView.getY() + vY: floorHeight - pView.getHeight())
                        .setDuration(0)
                        .start();

                handler.postDelayed(runnable, DELAY);
            }
        }, DELAY);
    }

    public void hitCeiling() {
        vY = 0;
    }

    public void hitWall() {
        vX *= -0.2;
    }

    public boolean onGround() {
        float pX = pView.getX();
        float pY = pView.getY();
        float pWidth = pView.getWidth();
        float pHeight = pView.getHeight();
        floorHeight = screen.getHeight();

        for (int i = 0; i < screen.getChildCount(); i++) {      // iterate through all views on screen
            View v = screen.getChildAt(i);

            // skip view if (Block right < player left) OR (player right < block left)
            if (v.getX() + v.getWidth() < pX || pX + pWidth < v.getX()) {
                continue;
            }

            if (v instanceof ImageView && !v.equals(pView)) {   // if is image and not the player

                //if player bottom is "lower" than block top, it is a potential wall and cannot be floor
                if (pY + pHeight > v.getY()) {
                    // if player top is lower than block bottom
                    if (pY >= v.getY() + v.getHeight()) {
                        continue;
                    }

                    // if moving up and player top is higher than block bottom and player bottom is lower than block top
                    if (vY < 0 && pY < v.getY() + v.getHeight() && pY+pHeight > v.getY()) {
                        //TODO: hitting corners from the side will bound him down, avoiding a wall collision
                        pView.setY(v.getY() +v.getHeight());
                        hitCeiling();
                        continue;
                    }
                    // if (moving right AND player right >= block left)
                    if (vX > 0 && pX + pWidth > v.getX()) {
                        pView.setX(v.getX() - pWidth);
                        hitWall();
                    } else if (vX < 0 && pX < v.getX() + v.getWidth()) {
                        pView.setX(v.getX() + v.getWidth());
                        hitWall();
                    }
                }
                else if ((int)v.getY() < floorHeight) {
                    floorHeight = (int)v.getY();
                }
            }
        }
        //System.out.println("player bottom: " + pView.getY() + pView.getHeight() + ", floorHeight: " + floorHeight);
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
                vX = dx*0.05f;                           // x-velocity
                vY = dy*0.075f;                          // y-velocity

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
