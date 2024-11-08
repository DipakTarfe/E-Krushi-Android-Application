package com.example.e_krushi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.e_krushi.activities.MainActivity;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_krushi.activities.ResetPasswordActivity;
import com.example.e_krushi.utils.Constants;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class Login extends AppCompatActivity {

    Button callSignUp, login_btn,forgetPassword;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout email,password;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String url= Constants.LOGIN_URL;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set the navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getString("isLoggedIn", "").equals("true")){
            startActivity(new Intent(Login.this,MainActivity.class));
            finishAffinity();
        }

        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logoImage);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        forgetPassword = findViewById(R.id.forgetPassword);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        //Logged in to the system
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_user(email.getEditText().getText().toString(),password.getEditText().getText().toString());
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }
        });


        //Go back to the SignUp screen when click on signup
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,SignUp.class);


                android.util.Pair[] pairs = new android.util.Pair[7];
                pairs[0] = new android.util.Pair(image,"logo_image");
                pairs[1] = new android.util.Pair(logoText,"logo_text");
                pairs[2] = new android.util.Pair(sloganText,"logo_desc");
                pairs[3] = new android.util.Pair(email,"email_tran");
                pairs[4] = new android.util.Pair(password,"password_tran");
                pairs[5] = new android.util.Pair(login_btn,"button_tran");
                pairs[6] = new android.util.Pair(callSignUp,"login_signup_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(intent,options.toBundle());
                finish();
            }
        });
    }
    public void login_user(final String email, final String password){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // This method is called when the server responds successfully
                if (response.equals("Logged in successfully")) {

                    //Session
                    editor.putString("isLoggedIn","true");
                    editor.commit();


                    //fatch userID
                    fetchUserDetails(email);
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);


                    // User is logged in successfully
                    StyleableToast.makeText(getApplicationContext(), "Logged In Successfully", R.style.successToast).show();
                    startActivity(new Intent(Login.this,MainActivity.class));
                    finishAffinity();
                } else {
                    // User not found or login failed
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(getApplicationContext(), "Login Failed!", R.style.failedToast).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                StyleableToast.makeText(getApplicationContext(),"Server Error",R.style.failedToast).show();
            }
        }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", String.valueOf(email));
                map.put("password", String.valueOf(password));

                return map;
        }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    public void fetchUserDetails(String email) {
        String userDetailsUrl = Constants.USER_DETAILS_URL; // Replace with your API endpoint for fetching user details

        StringRequest userDetailsRequest = new StringRequest(Request.Method.POST, userDetailsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String userName = jsonObject.getString("name");
                    String userEmail = jsonObject.getString("email");
                    String userID = jsonObject.getString("user_id");

                    if(sharedPreferences.getString("isLoggedIn", "").equals("true")){
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        editor.putString("userName", userName);
                        editor.putString("userEmail", userEmail);
                        editor.putString("user_id", userID);
                        editor.commit();
                        startActivity(intent);
                        finish();
                    }
                    //Session
                    editor.putString("isLoggedIn","true");
                    editor.commit();

                    // Store the name and email in variables or perform additional actions
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                StyleableToast.makeText(getApplicationContext(),"Server Error",R.style.failedToast).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Add the user details request to the Volley request queue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(userDetailsRequest);
    }
}