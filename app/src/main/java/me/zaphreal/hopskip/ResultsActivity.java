package me.zaphreal.hopskip;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        TextView score = findViewById(R.id.final_score);
        score.setText("Score: " + getIntent().getStringExtra("score"));

        ((ImageView)findViewById(R.id.results_player)).setImageResource(MainActivity.player.getDrawableID());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
