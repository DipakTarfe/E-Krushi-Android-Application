package com.example.e_krushi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.e_krushi.R;

public class RateUsActivity extends AppCompatActivity {

    Button myRating;
    RatingBar ratingBar;
    float myRate =0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);

        getSupportActionBar().setTitle("Rate Us");
        // Set gradient color to the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white)); // Set a transparent color for the navigation bar

        }

        myRating = findViewById(R.id.myRating);
        ratingBar = findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                String massege=null;

                myRate = ratingBar.getRating();

                switch ((int) rating){
                    case 1:
                        massege ="Sorry to hear that! :(";
                        break;
                    case 2:
                        massege = "You always accept suggeation!";
                        break;
                    case 3:
                        massege = "Good enougth!";
                        break;
                    case 4:
                        massege = "Great! Thank you";
                        break;
                    case 5:
                        massege = "Awesome! Thank you :)";
                        break;
                }
                Toast.makeText(RateUsActivity.this, massege, Toast.LENGTH_SHORT).show();
            }
        });

        myRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RateUsActivity.this, "Your Rating is: "+myRate, Toast.LENGTH_SHORT).show();
            }
        });
    }
}