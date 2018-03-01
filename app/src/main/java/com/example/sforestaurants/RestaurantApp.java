package com.example.sforestaurants;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class RestaurantApp extends Application {

    private static RestaurantApp mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public static final String TAG = RestaurantApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized RestaurantApp getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

                        public void putBitmap(String url, Bitmap bitmap) {
                            mCache.put(url, bitmap);
                        }

                        public Bitmap getBitmap(String url) {
                            return mCache.get(url);
                        }
                    });
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag("coordinates");
        getRequestQueue().add(req);
    }
}
