package com.example.e_krushi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.example.e_krushi.utils.Constants;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;


public class SignUp extends AppCompatActivity {

    Button callLogin, signup_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout name, email, phone, password;

    String url= Constants.SIGNUP_URL;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set the navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        callLogin = findViewById(R.id.login_screen);
        image = findViewById(R.id.logoImage);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        name =  findViewById(R.id.name);
        email =  findViewById(R.id.email);
        phone =  findViewById(R.id.phone);
        password =  findViewById(R.id.password);
        signup_btn = findViewById(R.id.signup_btn);


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register_user(name.getEditText().getText().toString(),email.getEditText().getText().toString(),phone.getEditText().getText().toString(),password.getEditText().getText().toString());
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        //Go back to the LogIn screen when click on Have already account
        callLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void register_user(final String name, final String email, final String phone, final String password){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Failed to register")){
                    StyleableToast.makeText(getApplicationContext(),response,R.style.failedToast).show();
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                }else {
                    if(response.equals("User already exists")){
                        StyleableToast.makeText(getApplicationContext(),"User already exists",R.style.failedToast).show();
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                    }else {
                        StyleableToast.makeText(getApplicationContext(), response, R.style.successToast).show();
                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                        finish();
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                    }
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
                map.put("name", String.valueOf(name));
                map.put("email", String.valueOf(email));
                map.put("phone", String.valueOf(phone));
                map.put("password", String.valueOf(password));

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}