package fr.qgdev.openweather.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_krushi.R;
import fr.qgdev.openweather.repositories.settings.SecuredPreferenceDataStore;

/**
 * CustomPreferenceFragment
 * <p>
 * Custom Preference Fragment to add a bottom padding to the recycler view
 * </p>
 *
 * @author Quentin GOMES DOS REIS
 * @version 1
 * @see PreferenceFragmentCompat
 */
public class CustomPreferenceFragment extends PreferenceFragmentCompat {
	
	/**
	 * onCreateView()
	 * <p>
	 * Override the default onCreateView method to add a bottom padding to the recycler view
	 */
	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		RecyclerView recyclerView = root.findViewById(androidx.preference.R.id.recycler_view);
		
		recyclerView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.recycler_view_bottom_padding));
		recyclerView.setClipToPadding(false);
		
		return root;
	}
	
	/**
	 * onCreatePreferences()
	 * <p>
	 * Override the default onCreatePreferences method to add a custom data store
	 */
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		PreferenceManager preferenceManager = getPreferenceManager();
		preferenceManager.setPreferenceDataStore(new SecuredPreferenceDataStore(getContext()));
		addPreferencesFromResource(R.xml.settings_preferences);

		// Set the API key
		String apiKey = "e83b3c4c08285bf87b99f9bbc0abe3f0";
		EditTextPreference apiKeyPreference = findPreference("conf_api_key");
		if (apiKeyPreference != null) {
			apiKeyPreference.setText(apiKey);
		}
	}
}
