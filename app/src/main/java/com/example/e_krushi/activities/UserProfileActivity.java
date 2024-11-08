package com.example.e_krushi.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_krushi.R;
import com.example.e_krushi.utils.Constants;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class UserProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private TextView usernameTextView;
    private TextView useremailTextView;
    private TextInputEditText usernameEditText;
    private TextInputEditText useremailEditText;
    private TextInputEditText userphoneEditText;
    ImageView img_picker, profile_image;

    Button forgetPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("My Account");
            // Set the action bar background drawable
            GradientDrawable gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.gradient);
            actionBar.setBackgroundDrawable(gradientDrawable);
        }

        // Set gradient color to the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            Drawable gradientDrawable = getResources().getDrawable(R.drawable.gradient);
            window.setStatusBarColor(0); // Set a transparent color for the status bar
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white)); // Set a transparent color for the navigation bar
            window.setBackgroundDrawable(gradientDrawable);
        }

        profile_image = findViewById(R.id.profile_image);
        img_picker = findViewById(R.id.img_picker);
        usernameTextView = findViewById(R.id.full_name);
        useremailTextView = findViewById(R.id.user_email);
        usernameEditText = findViewById(R.id.username);
        useremailEditText = findViewById(R.id.useremail);
        userphoneEditText = findViewById(R.id.userphone);
        forgetPassword = findViewById(R.id.forgetPassword);
        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        img_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(UserProfileActivity.this)
                        .crop()
                        .cropOval()
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("isLoggedIn", "").equals("true")) {
                    String userEmail = sharedPreferences.getString("userEmail", "");
                    requestOTP(userEmail);
                    Intent intent = new Intent(UserProfileActivity.this, VerifyOTP.class);
                    startActivity(intent);
                    finishAffinity();
                }else{
                    //Do nothing
                }
            }
        });

        if (sharedPreferences.getString("isLoggedIn", "").equals("true")) {

            String userName = sharedPreferences.getString("userName", "");
            String userEmail = sharedPreferences.getString("userEmail", "");
            String userPhone = sharedPreferences.getString("userPhone", "");


            // Set the values in the TextView elements
            usernameTextView.setText(userName);
            useremailTextView.setText(userEmail);
            usernameEditText.setText(userName);
            useremailEditText.setText(userEmail);
            userphoneEditText.setText(userPhone);
        } else {

            //Do Nothing
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ImagePicker.REQUEST_CODE) {
                Uri uri = data.getData();

                // Retrieve the image bitmap
                Bitmap imageBitmap = getImageBitmap(uri);

                // Check if the bitmap is not null
                if (imageBitmap != null) {
                    String userEmail = sharedPreferences.getString("userEmail", "");
                    storeImageInDatabase(userEmail, imageBitmap);
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        StyleableToast.makeText(UserProfileActivity.this, "Server Error", R.style.failedToast).show();
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
    private void storeImageInDatabase(String userEmail, Bitmap imageBitmap) {
        String url = Constants.PROFILE_IMAGE_URL;

        // Convert the image to a base64-encoded string
        String imageData = bitmapToBase64(imageBitmap);

        // Create a StringRequest using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the API response
                        if (response.equals("success")) {
                            // Image stored successfully
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);
                            StyleableToast.makeText(UserProfileActivity.this, "Image stored successfully", R.style.successToast).show();
                        } else {
                            // Failed to store image
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);
                            StyleableToast.makeText(UserProfileActivity.this, "Failed to store image"+response, R.style.failedToast).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        StyleableToast.makeText(UserProfileActivity.this, "Server Error", R.style.failedToast).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to the HTTP request (if needed)
                Map<String, String> params = new HashMap<>();
                params.put("userEmail", userEmail);
                params.put("image", imageData);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    // Helper function to convert a Bitmap image to a base64-encoded string
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private Bitmap getImageBitmap(Uri uri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
