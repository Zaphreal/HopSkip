package me.zaphreal.hopskip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    int screenWidth, screenHeight;
    public static Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;


        // UPDATE LATER TO ALLOW FOR ACCOUNTS / LOCAL STORAGE OF PLAYER
        player = new Player(R.drawable.frog);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // generate or destroy pause menu, then handle accordingly
        if (findViewById(R.id.shop_box).getVisibility() != View.GONE) {
            findViewById(R.id.shop_box).setVisibility(View.GONE);
        }
    }

    public void openShop(View view) {
        ConstraintLayout shopLayout = findViewById(R.id.shop_box);
        int shopWidth = (int)(screenWidth * 0.95);
        int shopHeight = (int)(screenHeight * 0.93);
        ViewGroup.LayoutParams params = shopLayout.getLayoutParams();
        params.width = shopWidth;
        params.height = shopHeight;
        shopLayout.setLayoutParams(params);

        RelativeLayout panel1 = findViewById(R.id.panel1);
        params = panel1.getLayoutParams();
        params.width = (int)(screenWidth * 0.2);
        params.height = (int)(screenHeight * 0.725);
        panel1.setLayoutParams(params);

        RelativeLayout panel2 = findViewById(R.id.panel2);
        params = panel2.getLayoutParams();
        params.width = (int)(screenWidth * 0.2);
        params.height = (int)(screenHeight * 0.725);
        panel2.setLayoutParams(params);

        RelativeLayout panel3 = findViewById(R.id.panel3);
        params = panel3.getLayoutParams();
        params.width = (int)(screenWidth * 0.2);
        params.height = (int)(screenHeight * 0.725);
        panel3.setLayoutParams(params);

        RelativeLayout panel4 = findViewById(R.id.panel4);
        params = panel4.getLayoutParams();
        params.width = (int)(screenWidth * 0.2);
        params.height = (int)(screenHeight * 0.725);
        panel4.setLayoutParams(params);

        ImageView shopBack = findViewById(R.id.shop_back);
        params = shopBack.getLayoutParams();
        params.width = (int)(screenWidth * 0.115);
        params.height = (int)(screenHeight * 0.095);
        shopBack.setLayoutParams(params);

        ImageView shopNext = findViewById(R.id.shop_next);
        params = shopNext.getLayoutParams();
        params.width = (int)(screenWidth * 0.115);
        params.height = (int)(screenHeight * 0.095);
        shopNext.setLayoutParams(params);

        shopLayout.setVisibility(View.VISIBLE);

        createShopItem(panel1,"Frog", 0, R.drawable.frog, 0);
        createShopItem(panel2,"Bunny", 0, R.drawable.bunny, 1);
    }

    public void createShopItem(RelativeLayout itemLayout, final String title, int type, final int imgResourse, final int price) {
        int layoutWidth = (int)(screenWidth * 0.2);
        int layoutHeight = (int)(screenHeight * 0.725);

        TextView titleView = new TextView(this);
        titleView.setWidth((int)(layoutWidth * 0.75));
        titleView.setHeight((int)(layoutHeight * 0.2));
        titleView.setText(title);
        titleView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        titleView.setX(layoutWidth * 0.125f);
        titleView.setY(layoutHeight * 0.1f);
        titleView.setTextColor(Color.WHITE);
        titleView.setTypeface(getResources().getFont(R.font.luxo));
        itemLayout.addView(titleView);

        ImageView icon = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)(layoutWidth * 0.8), (int)(layoutHeight * 0.4));
        icon.setLayoutParams(params);
        icon.setImageResource(imgResourse);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setX(layoutWidth * 0.1f);
        icon.setY(layoutHeight * 0.5f);
        itemLayout.addView(icon);

        if (type == 0) {
            itemLayout.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (!player.hasCharacter(title)) {
                            if (player.getBalance() >= price) {
                                player.modifyBalanceBy(-price);
                                player.unlockCharacter(title);
                                player.setDrawableID(imgResourse);
                            }
                        } else {
                            player.setDrawableID(imgResourse);
                        }
                        return true;
                    }
                    return event.getAction() == MotionEvent.ACTION_DOWN;
                }
            });
        }
    }
}
