package com.example.e_krushi.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.example.e_krushi.R;
import com.example.e_krushi.adapters.CartAdapter;
import com.example.e_krushi.databinding.ActivityCartBinding;
import com.example.e_krushi.model.Product;
import com.example.e_krushi.utils.Constants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Cart");
        }

        // Set the navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        products = new ArrayList<>();

        Cart cart = TinyCartHelper.getCart();

        adapter = new CartAdapter(this, products, new CartAdapter.CartListener() {

            @Override
            public void onQuantityChanged() {
                double subtotal =0;
                binding.subtotal.setText(String.format("Total: ₹ %.2f", subtotal));
            }

            @Override
            public void onSaveClicked() {
                double subtotal =0;
                binding.subtotal.setText(String.format("Total: ₹ %.2f", subtotal));
            }
            @Override
            public void onDeleteClicked(Product product) {
                SharedPreferences sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
                String userID = sharedPreferences.getString("user_id", "");
                deleteCartProduct(userID,product.getId());
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);

        // Fetch cart products
        SharedPreferences sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        String userID = sharedPreferences.getString("user_id", "");
        fetchCartProducts(userID);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        double subtotal =0;
        subtotal = calculateSubtotal(products);
        binding.subtotal.setText(String.format("Total: ₹ %.2f", subtotal));

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this,CheckoutActivity.class);
                intent.putExtra("products", products);
                intent.putExtra("subtotal", calculateSubtotal(products));
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void fetchCartProducts(String userID) {
        String url = Constants.FETCH_CART_PRODUCT_URL;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")) {
                        JSONArray cartProductsArray = object.getJSONArray("cartProducts");
                        double subtotal = 0;
                        for (int i = 0; i < cartProductsArray.length(); i++) {
                            JSONObject cartProductObject = cartProductsArray.getJSONObject(i);

                            // Extract product details from the JSON response
                            String name = cartProductObject.getString("name");
                            String image = Constants.PRODUCTS_IMAGE_URL + cartProductObject.getString("image");
                            int id = cartProductObject.getInt("product_id");
                            int stock = cartProductObject.getInt("stock");
                            double price = cartProductObject.getDouble("price");
                            int quantity = cartProductObject.getInt("quantity");

                            // Create a new Product object and add it to the products list
                            Product product = new Product(name, image, "", price, 0.0, stock, id);
                            product.setQuantity(quantity);
                            products.add(product);
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);

                            // Calculate subtotal for the current product
                            subtotal = calculateSubtotal(products);

                        }
                        // Update the subtotal TextView
                        binding.subtotal.setText(String.format("Total: ₹ %.2f", subtotal));
                        adapter.notifyDataSetChanged();
                    } else {
                        String message = object.getString("message");
                        if(message.equals("Cart is empty")){
                            ProgressBar progressBar = findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);

                            RecyclerView cartListRecyclerView = findViewById(R.id.cartList);
                            cartListRecyclerView.setVisibility(View.GONE);

                            TextView emptyCartTextView = findViewById(R.id.emptyCartTextView);
                            emptyCartTextView.setVisibility(View.VISIBLE);

                            ImageView emptyCartImageView = findViewById(R.id.emptyCartImageView);
                            emptyCartImageView.setVisibility(View.VISIBLE);

                            TextView subtotal = findViewById(R.id.subtotal);
                            subtotal.setText("₹ 0.00");

                            binding.continueBtn.setEnabled(false);
                            binding.continueBtn.setText("Continue");
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
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                StyleableToast.makeText(CartActivity.this, "Error fetching cart", R.style.failedToast).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userID));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    public void deleteCartProduct(String userID, int productId) {
        String url = Constants.DELETE_CART_PRODUCT_URL;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("success")) {
                        StyleableToast.makeText(CartActivity.this, "Cart removed successfully", R.style.successToast).show();
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        // Refresh the cart products
                        products.clear();
                        fetchCartProducts(userID);
                        progressBar.setVisibility(View.VISIBLE);

                    } else {
                        String message = object.getString("message");
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                        StyleableToast.makeText(CartActivity.this, message, R.style.failedToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                StyleableToast.makeText(CartActivity.this, "Error deleting cart", R.style.failedToast).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userID));
                params.put("product_id", String.valueOf(productId));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    private double calculateSubtotal(ArrayList<Product> products) {
        double subtotal = 0.0;
        for (Product product : products) {
            double price = product.getPrice();
            int quantity = product.getQuantity();
            subtotal += (price * quantity);
        }
        return subtotal;
    }
}
