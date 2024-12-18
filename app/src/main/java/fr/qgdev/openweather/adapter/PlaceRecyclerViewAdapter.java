package fr.qgdev.openweather.adapter;

import static fr.qgdev.openweather.adapter.PlaceRecyclerViewAdapter.ViewType.COMPACT;
import static fr.qgdev.openweather.adapter.PlaceRecyclerViewAdapter.ViewType.EXTENDED;
import static fr.qgdev.openweather.adapter.PlaceRecyclerViewAdapter.ViewType.EXTENDED_DAILY;
import static fr.qgdev.openweather.adapter.PlaceRecyclerViewAdapter.ViewType.EXTENDED_FULLY;
import static fr.qgdev.openweather.adapter.PlaceRecyclerViewAdapter.ViewType.EXTENDED_HOURLY;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.example.e_krushi.R;
import fr.qgdev.openweather.customview.DailyForecastGraphView;
import fr.qgdev.openweather.customview.GaugeBarView;
import fr.qgdev.openweather.customview.HourlyForecastGraphView;
import fr.qgdev.openweather.dialog.AirQualityInfoDialog;
import fr.qgdev.openweather.dialog.WeatherAlertDialog;
import fr.qgdev.openweather.fragment.places.PlacesViewModel;
import fr.qgdev.openweather.metrics.AirQuality;
import fr.qgdev.openweather.metrics.CurrentWeather;
import fr.qgdev.openweather.repositories.FormattingService;
import fr.qgdev.openweather.repositories.places.Place;

/**
 * PlaceRecyclerViewAdapter
 * <p>
 * Used to generate places items in main section of the app.
 * But can be used like any other adapter.
 * </p>
 *
 * @author Quentin GOMES DOS REIS
 * @version 1
 * @see android.widget.BaseAdapter
 */
public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter<PlaceRecyclerViewAdapter.PlaceViewHolder> {
	
	private final Context context;
	private final FormattingService formattingService;
	private final PlacesViewModel placesViewModel;
	private final List<String> countryNames;
	private final List<String> countryCodes;
	
	/**
	 * PlaceRecyclerViewAdapter Constructor
	 * <p>
	 * Just build an PlaceRecyclerViewAdapter Object
	 * </p>
	 *
	 * @param context         Current context, only used for LayoutInflater
	 * @param placesViewModel Reference of the parent PlacesFragment
	 */
	public PlaceRecyclerViewAdapter(Context context, PlacesViewModel placesViewModel, FormattingService formattingService) {
		super();
		this.context = context;
		this.placesViewModel = placesViewModel;
		this.formattingService = formattingService;
		this.countryNames = Arrays.asList(context.getResources().getStringArray(R.array.countries_names));
		this.countryCodes = Arrays.asList(context.getResources().getStringArray(R.array.countries_codes));
	}


	/**
	 * add(int position)
	 * <p>
	 *     Need to be called when a new place is added.
	 * </p>
	 *
	 * @param position Position of the new place
	 */
	public void add(int position) {
		this.notifyItemInserted(position);
	}

	/**
	 * remove(int position)
	 * <p>
	 *     Need to be called when a place is deleted or removed.
	 * </p>
	 *
	 * @param position Position of the deleted place
	 */
	public void remove(int position) {
		notifyItemRemoved(position);
	}
	
	
	/**
	 * getCountryName(String countryCode)
	 * <p>
	 * Will find country name related to a country code
	 * </p>
	 *
	 * @param countryCode String Country code of a place
	 * @return Will return the corresponding country name
	 * @apiNote Country code must be present in string file resources
	 */
	private String getCountryName(String countryCode) {
		int index = this.countryCodes.indexOf(countryCode);
		return this.countryNames.get(index);
	}
	
	
	/**
	 * onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	 * <p>
	 * Will inflate created viewHolder
	 * </p>
	 *
	 * @param parent   ViewHolder parent
	 * @param viewType ViewHolder view type
	 * @return Will return the inflated placeViewHolder
	 * @see RecyclerView.Adapter<>
	 */
	@NonNull
	@Override
	public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.adapter_places, parent, false);
		return new PlaceViewHolder(context, view);
	}
	
	
	/**
	 * onBindViewHolder(@NonNull PlaceViewHolder holder, final int position)
	 * <p>
	 * Will fill PlaceViewHolder with data
	 * </p>
	 *
	 * @param holder   PlaceViewHolder which will be filled with place data
	 * @param position Place position in placeViewArrayList
	 * @see RecyclerView.Adapter<>
	 */
	@Override
	public void onBindViewHolder(@NonNull PlaceViewHolder holder, final int position) {
		
		Place place = placesViewModel.getPlaces().get(position);
		holder.bindData(context, formattingService, this, placesViewModel, place);
	}
	
	
	/**
	 * getItemViewType(int position)
	 * <p>
	 * Will return view type of the corresponding item to position
	 * </p>
	 *
	 * @param position Item position
	 * @return Will return the ViewType corresponding to the item at the given position
	 */
	@Override
	public int getItemViewType(int position) {
		return placesViewModel.getPlaceViewTypeFromPosition(position).toInt();
	}
	
	/**
	 * getItemCount()
	 * <p>
	 * Call PlacesFragment methods to know item count in array
	 * </p>
	 *
	 * @return Will return the number of displayed places
	 */
	@Override
	public int getItemCount() {
		return placesViewModel.getPlaces().size();
	}
	
	/**
	 * ViewType
	 * <p>
	 * Describes the state of a place card which can be:
	 * Compact, Extended, Extended with Hourly or Daily tabs extended or Fully Extended where all tabs are extended
	 * </p>
	 */
	public enum ViewType {
		UNDEFINED(-1),   //	Useful when place is removed
		COMPACT(0),
		EXTENDED(1),
		EXTENDED_HOURLY(2),
		EXTENDED_DAILY(3),
		EXTENDED_FULLY(4);
		
		private final int viewTypeID;
		
		ViewType(int id) {
			viewTypeID = id;
		}
		
		public static ViewType fromInt(int id) {
			switch (id) {
				default:
				case 0:
					return COMPACT;
				case 1:
					return EXTENDED;
				case 2:
					return EXTENDED_HOURLY;
				case 3:
					return EXTENDED_DAILY;
				case 4:
					return EXTENDED_FULLY;
			}
		}
		
		public int toInt() {
			return this.viewTypeID;
		}
	}
	
	
	/**
	 * PlaceViewHolder
	 * <p>
	 * Used to contain the card of a place
	 * </p>
	 *
	 * @author Quentin GOMES DOS REIS
	 * @version 1
	 * @see RecyclerView.ViewHolder
	 */
	public static class PlaceViewHolder extends RecyclerView.ViewHolder {
		
		private final MaterialCardView cardView;
		private final TextView cityNameTextView;
		private final TextView countryNameTextVIew;
		
		private final TextView temperatureTextView;
		private final TextView temperatureFeelsLikeTextView;
		
		private final TextView weatherDescription;
		private final ImageView weatherIcon;
		
		private final LinearLayout weatherAlertLayout;
		private final ImageView weatherAlertIcon;
		
		private final TextView windDirectionTextView;
		private final TextView windSpeedTextView;
		private final TextView windGustSpeedTextView;
		
		private final TextView humidityTextView;
		private final TextView pressureTextView;
		private final TextView visibilityTextView;
		
		private final TextView sunriseTextView;
		private final TextView sunsetTextView;
		private final TextView cloudinessTextView;
		
		private final ImageView airQualityCircle;
		private final TextView airQualityIndex;
		private final TextView airQualityMessage;
		private final GaugeBarView airQualityGaugeSO2;
		private final GaugeBarView airQualityGaugeNO2;
		private final GaugeBarView airQualityGaugePM10;
		private final GaugeBarView airQualityGaugePM25;
		private final GaugeBarView airQualityGaugeO3;
		private final GaugeBarView airQualityGaugeCO;
		
		private final ImageView airQualityInfoIcon;
		
		private final LinearLayout precipitationLayout;
		private final TextView rainTextView;
		private final TextView snowTextView;
		
		private final LinearLayout lastUpdateAvailableLayout;
		private final TextView lastUpdateAvailableTextView;
		
		private final LinearLayout detailedInformationsLayout;
		private final LinearLayout forecastInformationsLayout;
		
		private final LinearLayout hourlyForecastLayout;
		private final HorizontalScrollView hourlyForecastScrollview;
		private final LinearLayout hourlyForecast;
		private final ImageView hourlyForecastExpandIcon;
		
		private final HourlyForecastGraphView hourlyForecastGraphView;
		
		private final LinearLayout dailyForecastLayout;
		private final HorizontalScrollView dailyForecastScrollview;
		private final LinearLayout dailyForecast;
		
		private final DailyForecastGraphView dailyForecastGraphView;
		
		private final ImageView dailyForecastExpandIcon;
		
		
		/**
		 * PlaceViewHolder(@NonNull Context context, @NonNull View itemView)
		 * <p>
		 * Place ViewHolder constructor
		 * </p>
		 *
		 * @param context  Application context in order to access to resources
		 * @param itemView The inflated view of place
		 * @apiNote context and itemView shouldn't be null !
		 */
		public PlaceViewHolder(@NonNull Context context, @NonNull View itemView) {
			super(itemView);
			
			this.cardView = itemView.findViewById(R.id.card_place);
			this.cityNameTextView = itemView.findViewById(R.id.city_adapter);
			this.countryNameTextVIew = itemView.findViewById(R.id.country_adapter);
			this.temperatureTextView = itemView.findViewById(R.id.temperature_value);
			this.temperatureFeelsLikeTextView = itemView.findViewById(R.id.temperature_feelslike_value);
			this.weatherDescription = itemView.findViewById(R.id.weather_description_adapter);
			this.weatherIcon = itemView.findViewById(R.id.weather_icon_adapter);
			this.weatherAlertLayout = itemView.findViewById(R.id.weather_alert);
			this.weatherAlertIcon = itemView.findViewById(R.id.warning_icon);
			
			this.windDirectionTextView = itemView.findViewById(R.id.wind_direction_value);
			this.windSpeedTextView = itemView.findViewById(R.id.wind_speed_value);
			this.windGustSpeedTextView = itemView.findViewById(R.id.wind_gust_speed_value);
			this.humidityTextView = itemView.findViewById(R.id.humidity_value);
			this.pressureTextView = itemView.findViewById(R.id.pressure_value);
			this.visibilityTextView = itemView.findViewById(R.id.visibility_value);
			this.sunriseTextView = itemView.findViewById(R.id.sunrise_value);
			this.sunsetTextView = itemView.findViewById(R.id.sunset_value);
			this.cloudinessTextView = itemView.findViewById(R.id.cloudiness_value);
			
			this.airQualityCircle = itemView.findViewById(R.id.airquality_circle);
			this.airQualityIndex = itemView.findViewById(R.id.airquality_number);
			this.airQualityMessage = itemView.findViewById(R.id.airquality_text);
			
			this.airQualityGaugeSO2 = itemView.findViewById(R.id.air_quality_gauge_so2);
			this.airQualityGaugeNO2 = itemView.findViewById(R.id.air_quality_gauge_no2);
			this.airQualityGaugePM10 = itemView.findViewById(R.id.air_quality_gauge_pm10);
			this.airQualityGaugePM25 = itemView.findViewById(R.id.air_quality_gauge_pm2_5);
			this.airQualityGaugeO3 = itemView.findViewById(R.id.air_quality_gauge_o3);
			this.airQualityGaugeCO = itemView.findViewById(R.id.air_quality_gauge_co);
			this.airQualityInfoIcon = itemView.findViewById(R.id.air_quality_information_dialog);
			
			this.precipitationLayout = itemView.findViewById(R.id.precipitations);
			this.rainTextView = itemView.findViewById(R.id.rain_precipitations_current_value);
			this.snowTextView = itemView.findViewById(R.id.snow_precipitations_current_value);
			this.lastUpdateAvailableLayout = itemView.findViewById(R.id.last_update_available);
			this.lastUpdateAvailableTextView = itemView.findViewById(R.id.last_update_value);
			
			this.detailedInformationsLayout = itemView.findViewById(R.id.detailed_informations);
			this.forecastInformationsLayout = itemView.findViewById(R.id.forecast);
			
			this.hourlyForecastLayout = itemView.findViewById(R.id.hourly_forecast);
			this.hourlyForecastScrollview = itemView.findViewById(R.id.hourly_forecast_scroll_view);
			this.hourlyForecast = itemView.findViewById(R.id.hourly_forecast_layout);
			this.hourlyForecastExpandIcon = itemView.findViewById(R.id.hourly_forecast_expand_icon);
			this.hourlyForecastGraphView = itemView.findViewById(R.id.hourly_graphview);
			
			this.dailyForecastLayout = itemView.findViewById(R.id.daily_forecast);
			this.dailyForecastScrollview = itemView.findViewById(R.id.daily_forecast_scroll_view);
			this.dailyForecast = itemView.findViewById(R.id.daily_forecast_layout);
			this.dailyForecastExpandIcon = itemView.findViewById(R.id.daily_forecast_expand_icon);
			this.dailyForecastGraphView = itemView.findViewById(R.id.daily_forecast_graphView);
			
			setDrawableCompoundTextView(context, this.windDirectionTextView, R.drawable.wind_vane_material);
			setDrawableCompoundTextView(context, this.windSpeedTextView, R.drawable.windsock_material);
			setDrawableCompoundTextView(context, this.windGustSpeedTextView, R.drawable.wind_material);
			setDrawableCompoundTextView(context, this.humidityTextView, R.drawable.humidity_material);
			setDrawableCompoundTextView(context, this.pressureTextView, R.drawable.barometer_material);
			setDrawableCompoundTextView(context, this.visibilityTextView, R.drawable.visibility_24dp);
			setDrawableCompoundTextView(context, this.sunriseTextView, R.drawable.sunrise_material);
			setDrawableCompoundTextView(context, this.sunsetTextView, R.drawable.sunset_material);
			setDrawableCompoundTextView(context, this.cloudinessTextView, R.drawable.cloudy_material);
			
			//  Compact view
			if (context.getResources().getInteger(R.integer.env_variables_column_count) == 2)
				this.pressureTextView.setVisibility(View.GONE);
			else this.windGustSpeedTextView.setVisibility(View.GONE);
			
			this.visibilityTextView.setVisibility(View.GONE);
			this.sunriseTextView.setVisibility(View.GONE);
			this.sunsetTextView.setVisibility(View.GONE);
			this.cloudinessTextView.setVisibility(View.GONE);
			
		}
		
		
		/**
		 * setVisibilityState(PlaceViewHolder holder, Place place)
		 * <p>
		 * Set the visibility of the views depending on the current view type
		 * </p>
		 */
		private void setVisibilityState(@NonNull Context context, @NonNull ViewType viewType, @NonNull Place place) {
			switch (viewType) {
				
				//  Extended view
				case EXTENDED: {
					if (context.getResources().getInteger(R.integer.env_variables_column_count) == 2)
						pressureTextView.setVisibility(View.VISIBLE);
					else windGustSpeedTextView.setVisibility(View.VISIBLE);
					
					visibilityTextView.setVisibility(View.VISIBLE);
					sunriseTextView.setVisibility(View.VISIBLE);
					sunsetTextView.setVisibility(View.VISIBLE);
					cloudinessTextView.setVisibility(View.VISIBLE);
					
					detailedInformationsLayout.setVisibility(View.VISIBLE);
					weatherAlertIcon.setVisibility(View.GONE);
					
					forecastInformationsLayout.setVisibility(View.VISIBLE);
					hourlyForecastExpandIcon.setRotation(0);
					hourlyForecastLayout.setVisibility(View.GONE);
					dailyForecastExpandIcon.setRotation(0);
					dailyForecastLayout.setVisibility(View.GONE);
					
					lastUpdateAvailableLayout.setVisibility(View.VISIBLE);
					if (place.thereIsWeatherAlerts()) {
						weatherAlertLayout.setVisibility(View.VISIBLE);
					} else {
						weatherAlertLayout.setVisibility(View.GONE);
					}
					break;
				}
				
				//  Hourly extended view
				case EXTENDED_HOURLY: {
					if (context.getResources().getInteger(R.integer.env_variables_column_count) == 2)
						pressureTextView.setVisibility(View.VISIBLE);
					else windGustSpeedTextView.setVisibility(View.VISIBLE);
					
					visibilityTextView.setVisibility(View.VISIBLE);
					sunriseTextView.setVisibility(View.VISIBLE);
					sunsetTextView.setVisibility(View.VISIBLE);
					cloudinessTextView.setVisibility(View.VISIBLE);
					
					detailedInformationsLayout.setVisibility(View.VISIBLE);
					weatherAlertIcon.setVisibility(View.GONE);
					
					forecastInformationsLayout.setVisibility(View.VISIBLE);
					hourlyForecastExpandIcon.setRotation(180);
					hourlyForecastLayout.setVisibility(View.VISIBLE);
					dailyForecastExpandIcon.setRotation(0);
					lastUpdateAvailableLayout.setVisibility(View.VISIBLE);
					
					if (place.thereIsWeatherAlerts()) {
						weatherAlertLayout.setVisibility(View.VISIBLE);
					} else {
						weatherAlertLayout.setVisibility(View.GONE);
					}
					break;
				}
				
				//  Daily extended view
				case EXTENDED_DAILY: {
					if (context.getResources().getInteger(R.integer.env_variables_column_count) == 2)
						pressureTextView.setVisibility(View.VISIBLE);
					else windGustSpeedTextView.setVisibility(View.VISIBLE);
					
					visibilityTextView.setVisibility(View.VISIBLE);
					sunriseTextView.setVisibility(View.VISIBLE);
					sunsetTextView.setVisibility(View.VISIBLE);
					cloudinessTextView.setVisibility(View.VISIBLE);
					
					detailedInformationsLayout.setVisibility(View.VISIBLE);
					weatherAlertIcon.setVisibility(View.GONE);
					
					forecastInformationsLayout.setVisibility(View.VISIBLE);
					hourlyForecastExpandIcon.setRotation(0);
					hourlyForecastLayout.setVisibility(View.GONE);
					dailyForecastExpandIcon.setRotation(180);
					dailyForecastLayout.setVisibility(View.VISIBLE);
					lastUpdateAvailableLayout.setVisibility(View.VISIBLE);
					
					if (place.thereIsWeatherAlerts()) {
						weatherAlertLayout.setVisibility(View.VISIBLE);
					} else {
						weatherAlertLayout.setVisibility(View.GONE);
					}
					break;
				}
				//  Fully extended view
				case EXTENDED_FULLY: {
					if (context.getResources().getInteger(R.integer.env_variables_column_count) == 2)
						pressureTextView.setVisibility(View.VISIBLE);
					else windGustSpeedTextView.setVisibility(View.VISIBLE);
					
					visibilityTextView.setVisibility(View.VISIBLE);
					sunriseTextView.setVisibility(View.VISIBLE);
					sunsetTextView.setVisibility(View.VISIBLE);
					cloudinessTextView.setVisibility(View.VISIBLE);
					
					detailedInformationsLayout.setVisibility(View.VISIBLE);
					weatherAlertIcon.setVisibility(View.GONE);
					
					forecastInformationsLayout.setVisibility(View.VISIBLE);
					hourlyForecastExpandIcon.setRotation(180);
					hourlyForecastLayout.setVisibility(View.VISIBLE);
					dailyForecastExpandIcon.setRotation(180);
					dailyForecastLayout.setVisibility(View.VISIBLE);
					lastUpdateAvailableLayout.setVisibility(View.VISIBLE);
					
					if (place.thereIsWeatherAlerts()) {
						weatherAlertLayout.setVisibility(View.VISIBLE);
					} else {
						weatherAlertLayout.setVisibility(View.GONE);
					}
					break;
				}
				case COMPACT:
				default: {
					//  Compact view
					if (context.getResources().getInteger(R.integer.env_variables_column_count) == 2)
						pressureTextView.setVisibility(View.GONE);
					else windGustSpeedTextView.setVisibility(View.GONE);
					
					visibilityTextView.setVisibility(View.GONE);
					sunriseTextView.setVisibility(View.GONE);
					sunsetTextView.setVisibility(View.GONE);
					cloudinessTextView.setVisibility(View.GONE);
					
					detailedInformationsLayout.setVisibility(View.GONE);
					weatherAlertIcon.setVisibility(View.GONE);
					
					forecastInformationsLayout.setVisibility(View.GONE);
					hourlyForecastExpandIcon.setRotation(0);
					hourlyForecastLayout.setVisibility(View.GONE);
					dailyForecastExpandIcon.setRotation(0);
					dailyForecastLayout.setVisibility(View.GONE);
					lastUpdateAvailableLayout.setVisibility(View.GONE);
					
					if (place.thereIsWeatherAlerts()) {
						weatherAlertIcon.setVisibility(View.VISIBLE);
					} else {
						weatherAlertIcon.setVisibility(View.GONE);
					}
					
					break;
				}
			}
		}
		
		
		/**
		 * setListeners(Place place, PlaceViewHolder holder)
		 * <p>
		 * Will initialize all listeners in the PlaceViewHolder
		 * </p>
		 *
		 * @param place The place
		 */
		private void setListeners(@NonNull Context context, @NonNull FormattingService formattingService, @NonNull PlaceRecyclerViewAdapter placeRecyclerViewAdapter, @NonNull PlacesViewModel placesViewModel, @NonNull Place place) {
			Integer placeID = place.getProperties().getPlaceId();
			ViewType viewType = placesViewModel.getPlaceViewType(placeID);
			
			//  Set listener for the whole place card item
			cardView.setOnClickListener(v -> {
				if (viewType == COMPACT) {
					placesViewModel.setPlaceViewType(placeID, EXTENDED);
				} else {
					placesViewModel.setPlaceViewType(placeID, COMPACT);
				}
				placeRecyclerViewAdapter.notifyItemChanged(getAbsoluteAdapterPosition());
			});
			
			//  Set listener for the hourly weather forecast tab
			hourlyForecast.setOnClickListener(v -> {
				switch (viewType) {
					case EXTENDED:
						placesViewModel.setPlaceViewType(placeID, EXTENDED_HOURLY);
						break;
					case EXTENDED_HOURLY:
						placesViewModel.setPlaceViewType(placeID, EXTENDED);
						break;
					case EXTENDED_DAILY:
						placesViewModel.setPlaceViewType(placeID, EXTENDED_FULLY);
						break;
					case EXTENDED_FULLY:
						placesViewModel.setPlaceViewType(placeID, EXTENDED_DAILY);
						break;
					default:
						placesViewModel.setPlaceViewType(placeID, COMPACT);
						break;
				}
				placeRecyclerViewAdapter.notifyItemChanged(getAbsoluteAdapterPosition());
			});
			
			//  Set listener for the daily weather forecast tab
			dailyForecast.setOnClickListener(v -> {
				switch (viewType) {
					case EXTENDED:
						placesViewModel.setPlaceViewType(placeID, EXTENDED_DAILY);
						break;
					case EXTENDED_DAILY:
						placesViewModel.setPlaceViewType(placeID, EXTENDED);
						break;
					case EXTENDED_HOURLY:
						placesViewModel.setPlaceViewType(placeID, EXTENDED_FULLY);
						break;
					case EXTENDED_FULLY:
						placesViewModel.setPlaceViewType(placeID, EXTENDED_HOURLY);
						break;
					default:
						placesViewModel.setPlaceViewType(placeID, COMPACT);
						break;
				}
				placeRecyclerViewAdapter.notifyItemChanged(getAbsoluteAdapterPosition());
			});
			
			//  Set listener for the weather alerts tab
			weatherAlertLayout.setOnClickListener(v -> {
				final WeatherAlertDialog weatherAlertDialog = new WeatherAlertDialog(context,
						  place,
						  formattingService);
				weatherAlertDialog.build();
			});
			
			//  Set listener for the weather alert icon
			weatherAlertIcon.setOnClickListener(v -> {
				final WeatherAlertDialog weatherAlertDialog = new WeatherAlertDialog(context,
						  place,
						  formattingService);
				weatherAlertDialog.build();
			});
			
			airQualityInfoIcon.setOnClickListener(v -> {
				AppCompatActivity tmp = (AppCompatActivity) context;
				AirQualityInfoDialog.display(tmp.getSupportFragmentManager());
			});
		}
		
		public void bindData(@NonNull Context context, @NonNull FormattingService formattingService, @NonNull PlaceRecyclerViewAdapter placeRecyclerViewAdapter, @NonNull PlacesViewModel placesViewModel, @NonNull Place place) {
			setVisibilityState(context, placesViewModel.getPlaceViewType(place.getProperties().getPlaceId()), place);
			setListeners(context, formattingService, placeRecyclerViewAdapter, placesViewModel, place);
			
			cityNameTextView.setText(place.getGeolocation().getCity());
			countryNameTextVIew.setText(placeRecyclerViewAdapter.getCountryName(place.getGeolocation().getCountryCode()));
			
			CurrentWeather currentWeather = place.getCurrentWeather();
			AirQuality airQuality = place.getAirQuality();
			
			temperatureTextView.setText(formattingService.getFloatFormattedTemperature(currentWeather.getTemperature(), true));
			temperatureFeelsLikeTextView.setText(formattingService.getFloatFormattedTemperature(currentWeather.getTemperatureFeelsLike(), true));
			
			weatherDescription.setText(currentWeather.getWeatherDescription());
			
			final int weatherIconId;
			
			switch (currentWeather.getWeatherCode()) {
				
				//  Thunderstorm Group
				case 210:
				case 211:
				case 212:
				case 221:
					weatherIconId = R.drawable.thunderstorm_flat;
					break;
				
				//  Drizzle and Rain (Light)
				case 300:
				case 310:
				case 500:
				case 501:
				case 520:
					if (currentWeather.isDaytime()) {
						weatherIconId = R.drawable.rain_and_sun_flat;
					}
					//  Night
					else {
						weatherIconId = R.drawable.rainy_night_flat;
					}
					break;
				
				//Drizzle and Rain (Moderate)
				case 301:
				case 302:
				case 311:
				case 313:
				case 321:
				case 511:
				case 521:
				case 531:
					weatherIconId = R.drawable.rain_flat;
					break;
				
				//Drizzle and Rain (Heavy)
				case 312:
				case 314:
				case 502:
				case 503:
				case 504:
				case 522:
					weatherIconId = R.drawable.heavy_rain_flat;
					break;
				
				//  Snow
				case 600:
				case 601:
				case 620:
				case 621:
					if (currentWeather.isDaytime()) {
						weatherIconId = R.drawable.snow_flat;
					}
					//  Night
					else {
						weatherIconId = R.drawable.snow_and_night_flat;
					}
					break;
				
				case 602:
				case 622:
					weatherIconId = R.drawable.snow_flat;
					break;
				
				case 611:
				case 612:
				case 613:
				case 615:
				case 616:
					weatherIconId = R.drawable.sleet_flat;
					break;
				
				//  Atmosphere
				case 701:
				case 711:
				case 721:
				case 731:
				case 741:
				case 751:
				case 761:
				case 762:
				case 771:
				case 781:
					if (currentWeather.isDaytime()) {
						weatherIconId = R.drawable.fog_flat;
					}
					//  Night
					else {
						weatherIconId = R.drawable.fog_and_night_flat;
					}
					break;
				
				//  Sky
				case 800:
					//  Day
					if (currentWeather.isDaytime()) {
						weatherIconId = R.drawable.sun_flat;
					}
					//  Night
					else {
						weatherIconId = R.drawable.moon_phase_flat;
					}
					break;
				
				case 801:
				case 802:
				case 803:
					if (currentWeather.isDaytime()) {
						weatherIconId = R.drawable.clouds_and_sun_flat;
					}
					//  Night
					else {
						weatherIconId = R.drawable.cloudy_night_flat;
					}
					break;
				
				case 804:
					weatherIconId = R.drawable.cloudy_flat;
					break;
				
				//  Default
				//	Thunderstorm and rain
				default:
				case 200:
				case 201:
				case 202:
				case 230:
				case 231:
				case 232:
					weatherIconId = R.drawable.storm_flat;
					break;
			}
			
			weatherIcon.setImageDrawable(AppCompatResources.getDrawable(context, weatherIconId));
			
			//  Wind
			////    Wind Direction
			windDirectionTextView.setText(formattingService.getFormattedDirection(currentWeather.getWindDirection(), currentWeather.isWindDirectionReadable()));
			
			////  Wind speed and Wind gust Speed
			windSpeedTextView.setText(formattingService.getIntFormattedSpeed(currentWeather.getWindSpeed(), true));
			windGustSpeedTextView.setText(formattingService.getIntFormattedSpeed(currentWeather.getWindGustSpeed(), true));
			
			//  Humidity
			humidityTextView.setText(String.format("%d %%", currentWeather.getHumidity()));
			
			//  Pressure
			pressureTextView.setText(formattingService.getFormattedPressure(currentWeather.getPressure(), true));
			
			//  Visibility
			visibilityTextView.setText(formattingService.getIntFormattedDistance(currentWeather.getVisibility(), true));
			
			//  Sunrise and sunset
			sunriseTextView.setText(formattingService.getFormattedTime(new Date(currentWeather.getSunrise()), place.getProperties().getTimeZone()));
			sunsetTextView.setText(formattingService.getFormattedTime(new Date(currentWeather.getSunset()), place.getProperties().getTimeZone()));
			
			cloudinessTextView.setText(String.format("%d%%", currentWeather.getCloudiness()));
			
			//	Air quality
			airQualityIndex.setText(String.valueOf(airQuality.getAqi()));
			
			switch (airQuality.getAqi()) {
				
				case 5:
					airQualityCircle.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorUvExtreme)));
					airQualityMessage.setText(context.getText(R.string.air_quality_5));
					break;
				
				case 4:
					airQualityCircle.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorUvVeryHigh)));
					airQualityMessage.setText(context.getText(R.string.air_quality_4));
					break;
				
				case 3:
					airQualityCircle.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorUvHigh)));
					airQualityMessage.setText(context.getText(R.string.air_quality_3));
					break;
				
				case 2:
					airQualityCircle.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorUvModerate)));
					airQualityMessage.setText(context.getText(R.string.air_quality_2));
					break;
				
				case 1:
				default:
					airQualityCircle.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorUvLow)));
					airQualityMessage.setText(context.getText(R.string.air_quality_1));
					break;
			}
			
			airQualityGaugeSO2.setValue(airQuality.getSo2());
			airQualityGaugeNO2.setValue(airQuality.getNo2());
			airQualityGaugePM10.setValue(airQuality.getPm10());
			airQualityGaugePM25.setValue(airQuality.getPm2_5());
			airQualityGaugeO3.setValue(airQuality.getO3());
			airQualityGaugeCO.setValue(airQuality.getCo());
			
			//  Precipitations
			//  If There is no rain or snow, so there is nothing to show about precipitations
			boolean thereIsRain = currentWeather.thereIsRain();
			boolean thereIsSnow = currentWeather.thereIsSnow();
			
			
			if (thereIsRain || thereIsSnow) {
				precipitationLayout.setVisibility(View.VISIBLE);
				rainTextView.setText(formattingService.getFloatFormattedShortDistance(currentWeather.getRain(), true));
				snowTextView.setText(formattingService.getFloatFormattedShortDistance(currentWeather.getSnow(), true));
				
				precipitationLayout.findViewById(R.id.rain_precipitations).setVisibility(thereIsRain ? View.VISIBLE : View.GONE);
				precipitationLayout.findViewById(R.id.snow_precipitations).setVisibility(thereIsSnow ? View.VISIBLE : View.GONE);
			} else {
				precipitationLayout.setVisibility(View.GONE);
			}
			
			
			hourlyForecastGraphView.initialization(place.getHourlyWeatherForecastList(), place.getDailyWeatherForecastList(), formattingService, place.getProperties().getTimeZone());
			dailyForecastGraphView.initialization(place.getDailyWeatherForecastList(), place.getProperties().getTimeZone(), formattingService);
			
			hourlyForecastScrollview.scrollTo(0, 0);
			dailyForecastScrollview.scrollTo(0, 0);
			
			
			lastUpdateAvailableTextView.setText(String.format("%s %s", formattingService.getFormattedFullTimeHour(new Date(currentWeather.getDt()), place.getProperties().getTimeZone()), place.getProperties().getTimeZoneStringForm()));
		}
		
		/**
		 * dpToPx(@NonNull Context context, float dip)
		 * <p>
		 * Just a DP to PX converter method
		 * </p>
		 *
		 * @param context Application context in order to access to resources
		 * @param dip     DP value that you want to convert
		 * @return The DP converted value into PX
		 */
		private int dpToPx(@NonNull Context context, float dip) {
			return (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					dip,
					context.getResources().getDisplayMetrics());
		}

		/**
		 * setDrawableCompoundTextView(Context context, @NonNull TextView textView, @DrawableRes int id)
		 * <p>
		 * A method to set a drawable to a compound drawable of a textView
		 * </p>
		 *
		 * @param context  Application context in order to access to resources
		 * @param textView The textView where the compound drawable will be set
		 * @param id       The id of the drawable that will be set as a compound drawable
		 */
		private void setDrawableCompoundTextView(@NonNull Context context, @NonNull TextView textView, @DrawableRes int id) {
			int compoundDrawableSideSize = dpToPx(context, 20);
			Drawable drawable = AppCompatResources.getDrawable(context, id);
			assert drawable != null;
			drawable.setBounds(0, 0, compoundDrawableSideSize, compoundDrawableSideSize);
			textView.setCompoundDrawables(drawable, null, null, null);
		}
	}
}
