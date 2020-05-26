package com.example.hopskip;

import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.widget.TextViewCompat;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        TextView score = findViewById(R.id.final_score);
        score.setText("Score: " + getIntent().getStringExtra("score"));
    }
}
