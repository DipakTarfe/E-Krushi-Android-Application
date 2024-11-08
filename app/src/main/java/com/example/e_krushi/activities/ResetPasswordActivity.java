package com.example.e_krushi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.e_krushi.R;
import com.example.e_krushi.utils.Constants;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class ResetPasswordActivity extends AppCompatActivity {

    TextInputLayout email;
    Button resetPassword;
    ImageView back;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Set the navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        
        email = findViewById(R.id.forgetPassword_email);
        resetPassword = findViewById(R.id.reset_btn);
        back = findViewById(R.id.back_forget);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Logged in to the system
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                requestOTP(email.getEditText().getText().toString());
                Intent intent = new Intent(ResetPasswordActivity.this,VerifyOTP.class);
                intent.putExtra("email", email.getEditText().getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }

    private void requestOTP(final String email) {
        String url = Constants.FORGET_PASSWORD_URL;

        // Create a StringRequest using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the OTP response
                        handleOTPResponse(response);
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        StyleableToast.makeText(ResetPasswordActivity.this, "Server Error", R.style.failedToast).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to the HTTP request (if needed)
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", String.valueOf(email));

                return map;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void handleOTPResponse(String response) {
        if (response.equals("All fields are required")) {
            // Handle error when fields are empty
            StyleableToast.makeText(this, "All fields are required", R.style.failedToast).show();
        } else if (response.equals("Reset Password Failed")) {
            // Handle error when reset password fails
            StyleableToast.makeText(this, "Reset Password Failed", R.style.failedToast).show();
        } else if (response.equals("Database connection error")) {
            // Handle error when database connection fails
            StyleableToast.makeText(this, "Database connection error", R.style.failedToast).show();
        } else {
            // OTP received successfully
            StyleableToast.makeText(this, "OTP: " + response, R.style.successToast).show();
            // Use the OTP value as needed
        }
    }
}