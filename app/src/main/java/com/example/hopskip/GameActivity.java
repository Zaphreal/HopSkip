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

    final int DELAY = 20;

    final float ACC = 2f;

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
                if (onGround() && vY > 0) {
                    vX = 0;
                    vY = 0;
                    return;
                }
                vY += ACC;
                pView.animate()
                        .x(pView.getX() + vX)
                        .y(pView.getY() + vY)
                        .setDuration(0)
                        .start();

                handler.postDelayed(runnable, DELAY);
            }
        }, DELAY);
    }

    public boolean onGround() {

        Rect pBounds = new Rect((int)pView.getX()+(int)(pView.getWidth()*0.4), (int)pView.getY()+pView.getHeight(), (int)pView.getX()+pView.getWidth()/2, (int)pView.getY()+pView.getHeight()+(int)(pView.getHeight()*0.1));
        //pView.getHitRect(pBounds);                              // register player hitbox


        for (int i = 0; i < screen.getChildCount(); i++) {      // iterate through all views on screen
            View v = screen.getChildAt(i);
            if (v.getX()+pView.getWidth() < pView.getX() || v.getX() > pView.getX() + pView.getWidth()*2) {
                continue;
            }
            if (v instanceof ImageView && !v.equals(pView)) {   // if is image and not the player
                Rect bounds = new Rect();
                v.getHitRect(bounds);
//                if (bounds.contains((int)pView.getX() + pView.getWidth()/2, (int)pView.getY() + pView.getHeight())) {
//                    return true;
//                }
                System.out.println("pBounds: " + pBounds + ", bounds: " + bounds);
                if (Rect.intersects(pBounds, bounds)) {         // check if image and player intersect
                    return true;                                // if they do, return true, else continue
                }
            }
        }
        return false;                                           // if not touching anything, return false
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
                vX = dx*0.1f;                           // x-velocity
                vY = dy*0.15f;                          // y-velocity

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
