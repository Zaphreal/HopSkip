package com.example.hopskip;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerTouchEventListener implements View.OnTouchListener {
    float dx, dy;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

//        switch (event.getAction()){
//            //case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_DOWN:
//                dx = v.getX() - event.getRawX();
//                dy = v.getY() - event.getRawY();
//                System.out.println("ACTION_DOWN: " + v.getX() + ", " + v.getY() + " -> " + event.getRawX()+ ", " + event.getRawY());
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                v.animate()
//                        .x(event.getRawX() + dx)
//                        .y(event.getRawY() + dy)
//                        .setDuration(0)
//                        .start();
//                System.out.println("ACTION_UP: " + event.getRawX() + dx + ", " + event.getRawY() + dy);
//
//                if (event.getRawY() + dy < GameActivity.FLOOR_HEIGHT) {
//                    GameActivity.handlePhysics();
//                }
//
//                break;
////            case MotionEvent.ACTION_MOVE:
////                System.out.println("ACTION_MOVE: " + v.getX() + ", " + v.getY() + " -> " + event.getRawX()+ ", " + event.getRawY());
////                break;
//            default:
//                return false;
//        }
        return true;
    }
}
