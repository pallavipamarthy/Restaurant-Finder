package com.example.sforestaurants.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.sforestaurants.R;
import java.util.HashSet;
import java.util.Set;

public class PrefUtils {

    private static final String COORDINATE_PREF = "coordinate_pref";

    public static void saveRestaurantCoordinates(Context context, HashSet<String> restaurantSet) {
        SharedPreferences.Editor editor = context.getSharedPreferences(COORDINATE_PREF, Context.MODE_PRIVATE).edit();
        editor.putStringSet(context.getResources().getString(R.string.restaurant_set_pref), restaurantSet);
        editor.apply();
    }

    public static Set<String> getRestaurantCoordinates(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(COORDINATE_PREF, Context.MODE_PRIVATE);
        return prefs.getStringSet(context.getResources().getString(R.string.restaurant_set_pref), new HashSet<String>());
    }
}
