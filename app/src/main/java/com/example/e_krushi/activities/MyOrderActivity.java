package com.example.e_krushi.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_krushi.R;
import com.example.e_krushi.adapters.OrderAdapter;
import com.example.e_krushi.model.Order;
import com.example.e_krushi.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.LocalDate;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyOrderActivity extends AppCompatActivity implements OrderAdapter.OnItemClickListener {
    private RecyclerView rvMyOrder;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("My Order");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        rvMyOrder = findViewById(R.id.rv_myorder);
        orderAdapter = new OrderAdapter(this, new ArrayList<>());
        orderAdapter.setOnItemClickListener(this);

        rvMyOrder.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false);
        rvMyOrder.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvMyOrder.setLayoutManager(layoutManager);
        rvMyOrder.setItemAnimator(new DefaultItemAnimator());
        rvMyOrder.setAdapter(orderAdapter);

        // Initialize ThreeTenABP
        AndroidThreeTen.init(this);

        // Fetch order products
        SharedPreferences sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        String userID = sharedPreferences.getString("user_id", "");
        fetchOrderDetails(userID);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(Order order, int position) {
        Gson gson = new Gson();
        String orderJson = gson.toJson(order);

        Intent intent = new Intent(MyOrderActivity.this, TrackingActivity.class);
        intent.putExtra("order", orderJson);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchOrderDetails(String userID) {
        String url = Constants.FETCH_ORDER_DETAILS_URL;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")) {
                        JSONArray orderDetailsArray = object.getJSONArray("orderList");
                        List<Order> orderList = new ArrayList<>();

                        for (int i = 0; i < orderDetailsArray.length(); i++) {
                            JSONObject orderObject = orderDetailsArray.getJSONObject(i);

                            // Extract order details from the JSON response
                            String orderImage = Constants.PRODUCTS_IMAGE_URL + orderObject.getString("image");
                            String orderStatus = orderObject.getString("order_status");
                            String orderName = orderObject.getString("product_name");
                            String orderPrice = orderObject.getString("total_fees");
                            String orderDate = orderObject.getString("order_created_at");
                            String orderID = orderObject.getString("order_id");
                            String productID = orderObject.getString("id");


                            if (orderDate != null) {
                                // Parse the orderDate into a LocalDateTime object using the appropriate DateTimeFormatter
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDateTime orderDateTime;
                                try {
                                    orderDateTime = LocalDateTime.parse(orderDate, formatter);
                                } catch (DateTimeParseException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                // Extract the date portion (trim the time part)
                                LocalDate orderDateOnly = orderDateTime.toLocalDate();

                                // Add 4 days to the orderDateOnly
                                LocalDate newOrderDateOnly = orderDateOnly.plusDays(4);

                                // Format the newOrderDateOnly using DateTimeFormatter
                                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                String formattedOrderDateOnly = newOrderDateOnly.format(outputFormatter);



                                // Create a new Order object and add it to the orderList
                                ProgressBar progressBar = findViewById(R.id.progressBar1);
                                progressBar.setVisibility(View.GONE);
                                Order order = new Order(orderImage, orderStatus, orderName, orderPrice, orderDate,formattedOrderDateOnly,orderID,productID);
                                orderList.add(order);
                            }
                        }

                        // Update the UI with the fetched order list
                        ProgressBar progressBar = findViewById(R.id.progressBar1);
                        progressBar.setVisibility(View.GONE);
                        orderAdapter.setOrderList(orderList);
                    } else {
                        String message = object.getString("message");
                        if(message.equals("Order not found")){
                            ProgressBar progressBar = findViewById(R.id.progressBar1);
                            progressBar.setVisibility(View.GONE);

                            RecyclerView myOrderRecyclerView = findViewById(R.id.rv_myorder);
                            myOrderRecyclerView.setVisibility(View.GONE);

                            TextView emptyOrderTextView = findViewById(R.id.emptyOrderText);
                            emptyOrderTextView.setVisibility(View.VISIBLE);

                            TextView shopNowTextView = findViewById(R.id.shopNow);
                            shopNowTextView.setVisibility(View.VISIBLE);

                            shopNowTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MyOrderActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            });

                            ImageView emptyOrderImageView = findViewById(R.id.emptyOrderImage);
                            emptyOrderImageView.setVisibility(View.VISIBLE);

                        }else {
                            //Do Nothing
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                ProgressBar progressBar = findViewById(R.id.progressBar1);
                progressBar.setVisibility(View.GONE);
                StyleableToast.makeText(MyOrderActivity.this, "Error fetching orders", R.style.failedToast).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Add the user_id parameter to the request
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userID);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
