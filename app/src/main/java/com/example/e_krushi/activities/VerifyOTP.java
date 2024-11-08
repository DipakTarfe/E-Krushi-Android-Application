package com.example.e_krushi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.example.e_krushi.R;
import com.example.e_krushi.utils.Constants;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class VerifyOTP extends AppCompatActivity {

    PinView pinView;
    Button verifyOTP;
    ImageView back;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        // Set the navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        pinView = findViewById(R.id.pinview);
        verifyOTP = findViewById(R.id.otp_code_btn);
        back = findViewById(R.id.back_otp);
        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    String email = extras.getString("email");
                    verifyOTP(pinView.getText().toString(),email);
                }else{
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    String userEmail = sharedPreferences.getString("userEmail", "");
                    verifyOTP(pinView.getText().toString(),userEmail);
                }
            }
        });
    }

    public void verifyOTP(final String otp,final String email) {
        String url = Constants.VERIFY_OTP_URL;

        // Create a StringRequest using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the OTP response
                        if (response.equals("success")) {
                            // Handle error when fields are empty
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);
                            // OTP received successfully
                            StyleableToast.makeText(VerifyOTP.this, "OTP Verified", R.style.successToast).show();
                            Intent intent = new Intent(VerifyOTP.this,SetNewPasswordActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finishAffinity();
                        }else {
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);
                            StyleableToast.makeText(VerifyOTP.this, "Invalid OTP", R.style.failedToast).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        StyleableToast.makeText(VerifyOTP.this, "Server Error", R.style.failedToast).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to the HTTP request (if needed)
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", String.valueOf(email));
                map.put("otp", String.valueOf(otp));

                return map;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}