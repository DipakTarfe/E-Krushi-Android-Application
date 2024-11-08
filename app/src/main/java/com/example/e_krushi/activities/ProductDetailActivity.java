package com.example.e_krushi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.e_krushi.Login;
import com.example.e_krushi.R;
import com.example.e_krushi.SignUp;
import com.example.e_krushi.databinding.ActivityProductDetailBinding;
import com.example.e_krushi.model.Product;
import com.example.e_krushi.utils.Constants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class ProductDetailActivity extends AppCompatActivity {


    ActivityProductDetailBinding binding;
    Product currentProduct;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Set the navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        int id = getIntent().getIntExtra("id",0);
        double price = getIntent().getDoubleExtra("price",0);

        Glide.with(this)
                .load(image)
                .into(binding.productImage);

        getProductDetails(id);

        getSupportActionBar().setTitle(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getString("isLoggedIn", "").equals("true")) {
                    String userID = sharedPreferences.getString("user_id", "");
                    addToCartDatabase(userID,currentProduct);
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    Intent intent = new Intent(ProductDetailActivity.this,Login.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cart) {
            startActivity(new Intent(this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    void getProductDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.GET_PRODUCT_DETAILS_URL + id;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getString("status").equals("success")) {
                        JSONObject product = object.getJSONObject("product");
                        String description = product.getString("description");
                        binding.productDescription.setText(
                                Html.fromHtml(description)
                        );

                        currentProduct = new Product(
                                product.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + product.getString("image"),
                                product.getString("status"),
                                product.getDouble("price"),
                                product.getDouble("price_discount"),
                                product.getInt("stock"),
                                product.getInt("id")
                        );

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }
    public void addToCartDatabase(final String userId, Product product){
        String url = Constants.ADD_TO_CART_URL;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Product added to cart")) {
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                    binding.addToCartBtn.setEnabled(false);
                    binding.addToCartBtn.setText("Added in cart");
                    StyleableToast.makeText(ProductDetailActivity.this, "Cart updated successfully", R.style.successToast).show();
                } else {
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText(ProductDetailActivity.this, response, R.style.failedToast).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                StyleableToast.makeText(getApplicationContext(), "Server Error",R.style.failedToast).show();
            }
        }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("user_id", String.valueOf(userId));
                map.put("product_id", String.valueOf(product.getId()));
                map.put("quantity", "1");
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}