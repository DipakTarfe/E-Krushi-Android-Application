package fr.qgdev.openweather;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.e_krushi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity
 * <p>
 * Main Activity
 * </p>
 *
 * @author Quentin GOMES DOS REIS
 * @version 1
 * @see AppCompatActivity
 */
public class WeatherActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle("Weather");
		}

		// Set the navigation bar color
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
		}

		FragmentContainerView fragmentContainerView = findViewById(R.id.nav_host_fragment);
		fragmentContainerView.setBackgroundColor(Color.rgb(	255, 255, 255));
		BottomNavigationView navView = findViewById(R.id.nav_view);
		
		//  NavigationBar for Live data, Forecasts or settings
		NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
		if (navHostFragment == null) throw new NullPointerException("NavHostFragment is null");
		NavController navController = navHostFragment.getNavController();
		NavigationUI.setupWithNavController(navView, navController);
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
