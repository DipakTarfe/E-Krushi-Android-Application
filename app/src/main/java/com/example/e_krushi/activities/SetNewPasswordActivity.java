package com.example.e_krushi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
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

public class SetNewPasswordActivity extends AppCompatActivity {


    TextInputLayout newPasword, confirmPassword;
    Button setPassword;
    ImageView back;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        // Set the navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        newPasword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        setPassword = findViewById(R.id.set_new_password_btn);
        back = findViewById(R.id.back_newPassword);
        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newPasword.getEditText().getText().toString().equals(confirmPassword.getEditText().getText().toString())){
                    StyleableToast.makeText(SetNewPasswordActivity.this,"Confirm password not maching",R.style.failedToast).show();
                }else {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.VISIBLE);
                        String email = extras.getString("email");
                        setPassword(newPasword.getEditText().getText().toString(),email);
                    }
                }
            }
        });
    }

    public void setPassword(final String newPassword,final String email) {
        String url = Constants.SET_NEW_PASSWORD_URL;

        // Create a StringRequest using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the OTP response
                        if (response.equals("success")) {
                            //if user login and he is reseting password he should be go in home other vise he go to login screen
                            if (sharedPreferences.getString("isLoggedIn", "").equals("true")) {
                                Intent intent = new Intent(SetNewPasswordActivity.this,MainActivity.class);
                                ProgressBar progressBar = findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.GONE);
                                StyleableToast.makeText(SetNewPasswordActivity.this, "Password has been updated", R.style.successToast).show();
                                startActivity(intent);
                                finishAffinity();

                            } else {
                                ProgressBar progressBar = findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(SetNewPasswordActivity.this,ResetPasswordSuccessActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        }else {
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);
                            StyleableToast.makeText(SetNewPasswordActivity.this, "Reset Password Failed", R.style.failedToast).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        StyleableToast.makeText(SetNewPasswordActivity.this, "Server Error", R.style.failedToast).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to the HTTP request (if needed)
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", String.valueOf(email));
                map.put("new_password", String.valueOf(newPassword));

                return map;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}