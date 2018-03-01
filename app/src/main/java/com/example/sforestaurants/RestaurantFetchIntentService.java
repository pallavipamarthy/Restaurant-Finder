package com.example.sforestaurants;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sforestaurants.Utils.PrefUtils;
import com.example.sforestaurants.data.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

public class RestaurantFetchIntentService extends IntentService {

    private String mPlacesURL;
    HashSet<String> mRestaurantSet;

    public RestaurantFetchIntentService() {
        super("CoordinatesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_GET_VISIBLE_COORDINATES.equals(action)) {
                mRestaurantSet = new HashSet<>();
                String latitude = intent.getStringExtra(Constants.EXTRA_LATITUDE);
                String longitude = intent.getStringExtra(Constants.EXTRA_LONGITUDE);
                handleActionGetRestaurantCoordinates(createPlacesUrl(latitude, longitude));
            }
        }
    }

    private void handleActionGetRestaurantCoordinates(String placesUrl) {
        fetchAllRestaurantCoordinates(this, placesUrl);
    }

    private String createPlacesUrl(String latitude, String longitude) {
        mPlacesURL = Constants.PLACES_API_SEARCH + latitude + "," + longitude + Constants.PLACES_API_KEY;
        return mPlacesURL;
    }

    private void fetchAllRestaurantCoordinates(final Context context, String placesUrl) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, placesUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String nextPageToken;
                            String nextPageURL = null;
                            if (response.has("next_page_token")) {
                                nextPageToken = response.getString("next_page_token");
                                nextPageURL = Constants.PLACES_BASE_URL + Constants.PLACES_API_KEY + "&pagetoken=" + nextPageToken;
                            }
                            JSONArray resultsArrayObj = response.getJSONArray("results");
                            for (int i = 0; i < resultsArrayObj.length(); i++) {
                                JSONObject restaurantObj = resultsArrayObj.getJSONObject(i);
                                JSONObject geometryObj = restaurantObj.getJSONObject("geometry");
                                JSONObject locationObj = geometryObj.getJSONObject("location");
                                String latitude = locationObj.getString("lat");
                                String longitude = locationObj.getString("lng");
                                String placeid = restaurantObj.getString("place_id");

                                String resString = "{lat:" + latitude + "," +
                                        "lng:" + longitude + "," +
                                        "place_id:" + placeid + "}";
                                mRestaurantSet.add(resString);
                            }
                            if (nextPageURL == null) {
                                PrefUtils.saveRestaurantCoordinates(context, mRestaurantSet);
                                Intent fetchCompletedIntent = new Intent(Constants.ACTION_FETCH_COMPLETE);
                                context.sendBroadcast(fetchCompletedIntent);
                            } else {
                                // Google restricting pagination in a loop
                                // Not able to fetch next page, coding for it anyway
                                fetchAllRestaurantCoordinates(context, nextPageURL);
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
        RestaurantApp.getInstance().addToRequestQueue(jsonObjReq);
    }
}
