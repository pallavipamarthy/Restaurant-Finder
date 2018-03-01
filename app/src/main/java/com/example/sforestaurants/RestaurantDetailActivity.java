package com.example.sforestaurants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.sforestaurants.data.Constants;
import com.example.sforestaurants.data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RestaurantDetailActivity extends AppCompatActivity {
    private static final String TAG = RestaurantDetailActivity.class.getSimpleName();

    private TextView mRestaurantNameView;
    private TextView mAddressView;
    private TextView mPhoneView;
    private TextView mRatingView;
    private TextView mWebsiteView;
    private TextView mHoursView;
    private NetworkImageView mPhotoView;
    private TextView mReviewsTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail_layout);

        getSupportActionBar().setLogo(R.drawable.gap_between_icon_title);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //
        String placeId = getIntent().getStringExtra(Constants.EXTRA_PLACE_ID);

        String detailUrl = Constants.PLACE_DETAIL_API + placeId;

        mRestaurantNameView = (TextView) findViewById(R.id.restaurant_name_view);
        mAddressView = (TextView) findViewById(R.id.restaurant_address_view);
        mPhoneView = (TextView) findViewById(R.id.phone_number);
        mRatingView = (TextView) findViewById(R.id.rating_text_view);
        mWebsiteView = (TextView) findViewById(R.id.website);
        mHoursView = (TextView) findViewById(R.id.hours_text_view);
        mPhotoView = (NetworkImageView) findViewById(R.id.restaurant_image_view);
        mReviewsTextView = (TextView) findViewById(R.id.reviews_layout_view);
        mProgressBar = (ProgressBar) findViewById(R.id.detail_progress_bar);

        mPhoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            }
        });

        getRestaurantDetails(detailUrl);
    }

    private void getRestaurantDetails(String restaurantDetailUrl) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, restaurantDetailUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String restaurantName = "";
                        String website = getString(R.string.not_available_string);
                        String address = getString(R.string.not_available_string);
                        String phone = getString(R.string.not_available_string);
                        String rating = getString(R.string.not_available_string);
                        String photoUrl = getString(R.string.not_available_string);
                        ArrayList<String> weekdayHoursList = new ArrayList<>();
                        ArrayList<Review> mReviewList = new ArrayList<>();
                        try {
                            JSONObject resultObj = response.getJSONObject("result");
                            if (resultObj.has("name")) {
                                restaurantName = resultObj.getString("name");
                            }
                            if (resultObj.has("photos")) {
                                JSONArray photoArray = resultObj.getJSONArray("photos");
                                String photoRef = photoArray.getJSONObject(0).getString("photo_reference");
                                photoUrl = Constants.PLACE_PHOTO_URL + photoRef;
                            }
                            if (resultObj.has("formatted_address")) {
                                address = resultObj.getString("formatted_address");
                            }
                            if (resultObj.has("formatted_phone_number")) {
                                phone = resultObj.getString("formatted_phone_number");
                            }
                            if (resultObj.has("rating")) {
                                rating = resultObj.getString("rating");
                            }
                            if (resultObj.has("opening_hours")) {
                                JSONObject openingHoursObj = resultObj.getJSONObject("opening_hours");
                                JSONArray weekdayTextArray = openingHoursObj.getJSONArray("weekday_text");

                                for (int i = 0; i < weekdayTextArray.length(); i++) {
                                    weekdayHoursList.add(weekdayTextArray.getString(i));
                                }
                            }
                            if (resultObj.has("reviews")) {
                                JSONArray reviewArray = resultObj.getJSONArray("reviews");
                                for (int i = 0; i < reviewArray.length(); i++) {
                                    JSONObject reviewObj = reviewArray.getJSONObject(i);
                                    String authorName = reviewObj.getString("author_name");
                                    String authorRating = reviewObj.getString("rating");
                                    String reviewText = reviewObj.getString("text");
                                    mReviewList.add(new Review(authorName, authorRating, reviewText));
                                }
                            }
                            if (resultObj.has("website")) {
                                website = resultObj.getString("website");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mRestaurantNameView.setText(restaurantName);
                            mWebsiteView.setText(website);
                            mRatingView.setText(rating);
                            mAddressView.setText(address);
                            mPhoneView.setText(phone);
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < weekdayHoursList.size(); i++) {
                                sb.append(weekdayHoursList.get(i) + "\n");
                            }
                            mHoursView.setText(sb.toString());
                            mPhotoView.setImageUrl(photoUrl, RestaurantApp.getInstance().getImageLoader());
                            StringBuilder reviewString = new StringBuilder();
                            for (int i = 0; i < mReviewList.size(); i++) {
                                reviewString.append(mReviewList.get(i).toString() + "\n");
                            }
                            mReviewsTextView.setText(reviewString);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG," Error fetching restaurant details " + error.toString());
            }
        });
        RestaurantApp.getInstance().addToRequestQueue(jsonObjReq);
    }
}
