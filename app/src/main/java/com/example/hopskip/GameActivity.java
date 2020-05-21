package com.example.hopskip;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    final int DELAY = 20;
    final public int FLOOR_HEIGHT = (int)(Resources.getSystem().getDisplayMetrics().heightPixels * 0.6);
    final float ACC = 2f;

    Handler handler = new Handler();
    Runnable runnable;
    float vX, vY;

    RelativeLayout rl;
    ImageView player;
    ImageView indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        rl = findViewById(R.id.background);
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
        player = findViewById(R.id.player);
        player.setImageDrawable(getDrawable(R.drawable.frog));
        player.setOnTouchListener(this);

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
                player.animate()
                        .x(player.getX() + vX)
                        .y(player.getY() + vY)
                        .setDuration(0)
                        .start();

                handler.postDelayed(runnable, DELAY);
            }
        }, DELAY);
    }

    public boolean onGround() {
        if (player.getY() >= FLOOR_HEIGHT) {
            return true;
        }
        return false;
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
