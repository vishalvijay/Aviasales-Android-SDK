package ru.aviasales.template.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import ru.aviasales.core.search.object.Flight;

public class Utils {

	public static final String PREFERENCES_NAME = "ru.aviasales";

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	public static String capitalizeFirstLetter(String original) {
		if (original.length() == 0)
			return original;
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public static int getRouteDurationInMin(List<Flight> flights) {
		if (flights == null) return 0;
		int duration = 0;
		for (int i = 0; i < flights.size(); i++) {
			duration += flights.get(i).getDuration();
			if (i > 0) {
				duration += flights.get(i).getDelay();
			}
		}
		return duration;
	}

	public static int convertDPtoPixels(Context context, float dps) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dps * scale + 0.5f);
	}

}
