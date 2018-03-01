package com.example.sforestaurants.data;

public class Constants {
    public static final String PLACES_API_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?type=restaurant&radius=1000&location=";
    public static final String PLACES_API_KEY = "&key=AIzaSyB-bpw0ollWA5AKpT11Y2CL2qPFs4kC_dk";
    public static final String ACTION_GET_VISIBLE_COORDINATES = "com.example.sforestaurants.action.ACTION_GET_VISIBLE_COORDINATES";
    public static final String ACTION_FETCH_COMPLETE = "com.example.sforestaurants.ACTION_FETCH_COMPLETE";

    public static final String EXTRA_PLACE_ID = "place_id";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";

    public static final String PLACE_DETAIL_API = "https://maps.googleapis.com/maps/api/place/details/json?" + PLACES_API_KEY + "&placeid=";
    public static final String PLACE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200" + PLACES_API_KEY + "&photoreference=";

    public static final String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
}
