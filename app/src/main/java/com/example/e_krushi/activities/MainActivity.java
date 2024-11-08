package com.example.e_krushi.activities;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.e_krushi.adapters.AiAdapter;
import com.google.android.material.navigation.NavigationView;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.e_krushi.Login;
import com.google.android.material.textfield.TextInputLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_krushi.R;
import com.example.e_krushi.adapters.CategoryAdapter;
import com.example.e_krushi.adapters.ProductAdapter;
import com.example.e_krushi.databinding.ActivityMainBinding;
import com.example.e_krushi.model.Category;
import com.example.e_krushi.model.Product;
import com.example.e_krushi.utils.Constants;
import com.google.android.material.navigation.NavigationView;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MaterialSearchBar searchBar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextInputLayout email;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ModelAi>aiList;
    AiAdapter aiAdapter;
    BroadcastReceiver broadcastReceiver;

    private TextView usernameTextView;
    private TextView useremailTextView;

    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;

    ProductAdapter productAdapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchBar = findViewById(R.id.searchBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        toolbar = findViewById(R.id.toolbar);
        email = findViewById(R.id.email);
        View headerView = navigationView.getHeaderView(0);
        usernameTextView = headerView.findViewById(R.id.username);
        useremailTextView = headerView.findViewById(R.id.useremail);

        broadcastReceiver = new ConnectionReceiver();
        registerNetworkBroadcast();

        //hide or show items
        if (sharedPreferences.getString("isLoggedIn", "").equals("true")) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_login).setVisible(false);

            String userName = sharedPreferences.getString("userName", "");
            String userEmail = sharedPreferences.getString("userEmail", "");
            // Set the values in the TextView elements
            usernameTextView.setText(userName);
            useremailTextView.setText(userEmail);

        } else {
            usernameTextView.setText("Guest");
            useremailTextView.setText("guest@gmail.com");
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_logout).setVisible(false);
            menu.findItem(R.id.nav_profile).setVisible(false);
            menu.findItem(R.id.nav_cart).setVisible(false);
            menu.findItem(R.id.nav_order).setVisible(false);
        }


        navigationView.bringToFront();
        // Setup the ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    binding.searchBar.closeSearch();
                } else if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        binding.drawerLayout.openDrawer(GravityCompat.START);
                        navigationView.setCheckedItem(R.id.nav_home);
                    }
                }
            }
        });

        initCategories();
        initProducts();
        initSlider();
        initData();
        initRecyclerView();

    }

    private void initData() {

        aiList=new ArrayList<>();
        aiList.add(new ModelAi(R.drawable.plant_scan1,"Scan"));
        aiList.add(new ModelAi(R.drawable.weather ,"Weather"));
//        aiList.add(new ModelAi(R.drawable.newspaper ,"Daily News"));
    }

    private void initRecyclerView() {

        recyclerView=findViewById(R.id.aiList1);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        aiAdapter=new AiAdapter(aiList);
        recyclerView.setAdapter(aiAdapter);
        aiAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
    private void initSlider() {
        getRecentOffers();
    }

    void initCategories() {
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categories);

        getCategories();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
    }

    void getCategories() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("err", response);
                    JSONObject mainObj = new JSONObject(response);
                    if(mainObj.getString("status").equals("success")) {
                        JSONArray categoriesArray = mainObj.getJSONArray("categories");
                        for(int i =0; i< categoriesArray.length(); i++) {
                            JSONObject object = categoriesArray.getJSONObject(i);
                            Category category = new Category(
                                    object.getString("name"),
                                    Constants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                    object.getString("color"),
                                    object.getString("brief"),
                                    object.getInt("id")
                            );
                            categories.add(category);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        // DO nothing
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
    void getRecentProducts() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.GET_PRODUCTS_URL + "?count=8";
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")){
                    JSONArray productsArray = object.getJSONArray("products");
                    for(int i =0; i< productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        Product product = new Product(
                                childObj.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString("image"),
                                childObj.getString("status"),
                                childObj.getDouble("price"),
                                childObj.getDouble("price_discount"),
                                childObj.getInt("stock"),
                                childObj.getInt("id")

                        );
                        products.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> { });

        queue.add(request);
    }

    void getRecentOffers() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")) {
                    JSONArray offerArray = object.getJSONArray("news_infos");
                    for(int i =0; i < offerArray.length(); i++) {
                        JSONObject childObj =  offerArray.getJSONObject(i);
                        binding.carousel.addData(
                                new CarouselItem(
                                        Constants.NEWS_IMAGE_URL + childObj.getString("image"),
                                        childObj.getString("title")
                                )
                        );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
        queue.add(request);
    }

    void initProducts() {
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products);

        getRecentProducts();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_login:
                startActivity(new Intent(MainActivity.this, Login.class));
                finishAffinity();
                break;
            case R.id.nav_logout:
                editor.putString("isLoggedIn","false");
                editor.commit();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finishAffinity();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                String userEmail = sharedPreferences.getString("userEmail", "");
                fetchUserDetails(userEmail);
                break;
            case R.id.nav_share:
                Intent intent =new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body="Download this app";
                String sub="https://www.google.com/";
                intent.putExtra(Intent.EXTRA_TEXT,body);
                intent.putExtra(Intent.EXTRA_TEXT,sub);
                startActivity(Intent.createChooser(intent,"Share using"));
                break;
            case R.id.nav_rate:
                startActivity(new Intent(MainActivity.this, RateUsActivity.class));
                break;
            case R.id.nav_cart:
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                break;
            case R.id.nav_order:
                startActivity(new Intent(MainActivity.this, MyOrderActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                    String userPhone = jsonObject.getString("phone");

                    if(sharedPreferences.getString("isLoggedIn", "").equals("true")){
                        editor.putString("userName", userName);
                        editor.putString("userEmail", userEmail);
                        editor.putString("userPhone", userPhone);
                        editor.commit();
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
                Toast.makeText(getApplicationContext(),"Error: "+ error,Toast.LENGTH_SHORT).show();
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
    protected void registerNetworkBroadcast(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
    protected void unregisterNetworkBroadcast(){
        try {
            unregisterReceiver(broadcastReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkBroadcast();
    }
}