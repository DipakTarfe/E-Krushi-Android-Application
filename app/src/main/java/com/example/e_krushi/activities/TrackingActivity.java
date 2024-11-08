package com.example.e_krushi.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_krushi.R;
import com.example.e_krushi.model.Order;
import com.example.e_krushi.utils.Constants;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class TrackingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Order Tracking");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        ImageView imageView = findViewById(R.id.productImage);
        TextView textView = findViewById(R.id.productName);
        TextView pricetextView = findViewById(R.id.orderPrice);
        TextView datetextView = findViewById(R.id.order_date);
        TextView expectedDate = findViewById(R.id.exp_date);
        TextView orderId = findViewById(R.id.order_id);
        Button cancelOrder = findViewById(R.id.cancel_order);
        Button mark_delivered = findViewById(R.id.mark_delivered);
        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Intent intent = getIntent();
        String orderJson = intent.getStringExtra("order");
        Gson gson = new Gson();
        Order order = gson.fromJson(orderJson, Order.class);

        // Set the data to the views
        textView.setText(order.getName());
        pricetextView.setText("₹ " +order.getOrderPrice());
        orderId.setText("Order ID: " +order.orderID());
        expectedDate.setText("Expected Delivary: " +order.expectedDate());
        datetextView.setText(""+order.getOrderDate());

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("isLoggedIn", "").equals("true")) {
                    String orderId = order.productID();
                    String userEmail = sharedPreferences.getString("userEmail", "");
                    cancelOrder(orderId,userEmail);
                } else {
                    //Do nathing
                }
            }
        });
        mark_delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("isLoggedIn", "").equals("true")) {
                    String orderId = order.orderID();
                    String userEmail = sharedPreferences.getString("userEmail", "");
                    markDelivered(orderId,userEmail);
                } else {
                    //Do nathing
                }
            }
        });
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder);// Placeholder image while loading


        Glide.with(this)
                .load(order.getImage()) // The image URL
                .apply(requestOptions)
                .into(imageView);
    }

    public void markDelivered(String orderId, String email) {
        String url = Constants.MARK_DELIVERED_URL;

        // Create a StringRequest using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        StyleableToast.makeText(TrackingActivity.this, response, R.style.failedToast).show();

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
                        StyleableToast.makeText(TrackingActivity.this, "Server Error", R.style.failedToast).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to the HTTP request (if needed)
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", String.valueOf(email));
                map.put("orderId", String.valueOf(orderId));

                return map;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void cancelOrder(String orderId, String email) {
        String url = Constants.CANCEL_ORDER_URL;

        // Create a StringRequest using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")){
                            StyleableToast.makeText(TrackingActivity.this, "Your Order has been Canceled", R.style.successToast).show();
                            Intent intent = new Intent(TrackingActivity.this,MyOrderActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);
                        }else {
                            StyleableToast.makeText(TrackingActivity.this, response, R.style.failedToast).show();

                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        StyleableToast.makeText(TrackingActivity.this, "Server Error", R.style.failedToast).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Add parameters to the HTTP request (if needed)
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", String.valueOf(email));
                map.put("id", String.valueOf(orderId));

                return map;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}