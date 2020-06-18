package me.zaphreal.hopskip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    private int screenWidth, screenHeight;
    public static Player player;
    private RelativeLayout panel1, panel2, panel3, panel4;
    private int shopPage = 1;
    int numPages = 2;

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

        panel1 = findViewById(R.id.panel1);
        params = panel1.getLayoutParams();
        params.width = (int)(screenWidth * 0.2);
        params.height = (int)(screenHeight * 0.725);
        panel1.setLayoutParams(params);

        panel2 = findViewById(R.id.panel2);
        params = panel2.getLayoutParams();
        params.width = (int)(screenWidth * 0.2);
        params.height = (int)(screenHeight * 0.725);
        panel2.setLayoutParams(params);

        panel3 = findViewById(R.id.panel3);
        params = panel3.getLayoutParams();
        params.width = (int)(screenWidth * 0.2);
        params.height = (int)(screenHeight * 0.725);
        panel3.setLayoutParams(params);

        panel4 = findViewById(R.id.panel4);
        params = panel4.getLayoutParams();
        params.width = (int)(screenWidth * 0.2);
        params.height = (int)(screenHeight * 0.725);
        panel4.setLayoutParams(params);

        final ImageView shopBack = findViewById(R.id.shop_back);
        params = shopBack.getLayoutParams();
        params.width = (int)(screenWidth * 0.115);
        params.height = (int)(screenHeight * 0.095);
        shopBack.setLayoutParams(params);
        shopBack.setImageResource(R.drawable.shop_back);
        shopBack.setColorFilter(Color.argb(100, 0, 0, 0), PorterDuff.Mode.DARKEN); // back is initially darkened

        final ImageView shopNext = findViewById(R.id.shop_next);
        params = shopNext.getLayoutParams();
        params.width = (int)(screenWidth * 0.115);
        params.height = (int)(screenHeight * 0.095);
        shopNext.setLayoutParams(params);
        shopNext.setImageResource(R.drawable.shop_next);

        shopBack.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (shopPage > 1) {
                        shopPage--;
                        openShopPage(shopPage);
                        if (shopPage == 1) {
                            shopBack.setColorFilter(Color.argb(100, 0, 0, 0), PorterDuff.Mode.DARKEN);
                        }
                        if (shopPage == numPages - 1) {
                            shopNext.clearColorFilter();
                        }
                    }
                    return true;
                }
                return event.getAction() == MotionEvent.ACTION_DOWN;
            }
        });

        shopNext.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (shopPage < numPages) {
                        shopPage++;
                        openShopPage(shopPage);
                        if (shopPage == 2) {
                            shopBack.clearColorFilter();
                        }
                        if (shopPage == numPages) {
                            shopNext.setColorFilter(Color.argb(100, 0, 0, 0), PorterDuff.Mode.DARKEN);
                        }
                    }
                    return true;
                }
                return event.getAction() == MotionEvent.ACTION_DOWN;
            }
        });


        shopLayout.setVisibility(View.VISIBLE);
        shopPage = 1;
        openShopPage(shopPage);
    }

    public void openShopPage(int page) {
        panel1.removeAllViews();
        panel2.removeAllViews();
        panel3.removeAllViews();
        panel4.removeAllViews();
        switch (page) {
            case 1:
                createShopItem(panel1,"Frog", 0, R.drawable.frog, 0);
                createShopItem(panel2,"Bunny", 0, R.drawable.bunny, 0);
                break;
            case 2:
                createShopItem(panel1,"Old Bunny", 0, R.drawable.old_bunny, 100000);
                break;
            default:
        }
    }

    public void createShopItem(RelativeLayout itemLayout, final String title, int type, final int imgResourse, final int price) {
        int layoutWidth = (int)(screenWidth * 0.2);
        int layoutHeight = (int)(screenHeight * 0.725);

        TextView titleView = new TextView(this);
        titleView.setWidth((int)(layoutWidth * 0.75));
        titleView.setHeight((int)(layoutHeight * 0.15));
        titleView.setText(title);
        titleView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        titleView.setX(layoutWidth * 0.125f);
        titleView.setY(layoutHeight * 0.1f);
        titleView.setTextColor(Color.WHITE);
        titleView.setTypeface(getResources().getFont(R.font.luxo));
        titleView.setGravity(Gravity.CENTER);
        itemLayout.addView(titleView);

        ImageView icon = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)(layoutWidth * 0.7), (int)(layoutHeight * 0.3));
        icon.setLayoutParams(params);
        icon.setImageResource(imgResourse);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setX(layoutWidth * 0.15f);
        icon.setY(layoutHeight * 0.4f);
        itemLayout.addView(icon);

        if (type == 0) {

            if (!player.hasCharacter(title)) {

                ImageView coinIcon = new ImageView(this);
                if (MainActivity.player.getDrawableID() == R.drawable.frog) {
                    coinIcon.setImageResource(R.drawable.coin_frog);
                } else if (MainActivity.player.getDrawableID() == R.drawable.bunny){
                    coinIcon.setImageResource(R.drawable.coin_bunny);
                } else {
                    coinIcon.setImageResource(R.drawable.old_bunny);
                }
                params = new RelativeLayout.LayoutParams((int)(layoutWidth * 0.2), (int)(layoutHeight * 0.075)); // height based on width for squareness
                coinIcon.setLayoutParams(params);
                coinIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                coinIcon.setX(layoutWidth * 0.175f);
                coinIcon.setY(layoutHeight * 0.84f);
                itemLayout.addView(coinIcon);


                TextView priceView = new TextView(this);
                priceView.setWidth((int)(layoutWidth * 0.45));
                priceView.setHeight((int)(layoutHeight * 0.125)); // 0.825 + 0.0625 = 0.8875 midline
                priceView.setText(String.valueOf(price));
                priceView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                priceView.setX(layoutWidth * 0.45f);
                priceView.setY(layoutHeight * 0.825f);
                priceView.setTypeface(getResources().getFont(R.font.crom));
                priceView.setGravity(Gravity.CENTER);
                priceView.setMaxLines(1);
                if (player.getBalance() < price) {
                    priceView.setTextColor(Color.rgb(255, 92, 92));
                } else {
                    priceView.setTextColor(Color.WHITE);
                }
                itemLayout.addView(priceView);

            } else {
                TextView ownedView = new TextView(this);
                ownedView.setWidth((int)(layoutWidth * 0.75));
                ownedView.setHeight((int)(layoutHeight * 0.15)); // 0.825 + 0.0625 = 0.8875 midline
                ownedView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                ownedView.setX(layoutWidth * 0.125f);
                ownedView.setY(layoutHeight * 0.8125f);
                ownedView.setTypeface(getResources().getFont(R.font.crom));
                ownedView.setGravity(Gravity.CENTER);
                ownedView.setMaxLines(1);
                if (player.getDrawableID() == imgResourse) {
                    ownedView.setText("Equipped");
                    ownedView.setTextColor(Color.rgb(125, 246, 81));
                } else {
                    ownedView.setText("Owned");
                    ownedView.setTextColor(Color.rgb(246, 224, 81));
                }

                itemLayout.addView(ownedView);
            }

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
                                openShopPage(shopPage);
                            }
                        } else {
                            player.setDrawableID(imgResourse);
                            openShopPage(shopPage);
                        }
                        return true;
                    }
                    return event.getAction() == MotionEvent.ACTION_DOWN;
                }
            });
        }
    }
}
