package marine.josep.marvelevents.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class MyRequestQueue {

    private static Context mCtx;
    private static MyRequestQueue instance;
    private static ImageLoader.ImageCache imageCache;
    private static ImageLoader imageLoaderInstance;
    private RequestQueue requestQueue;

    private MyRequestQueue(Context context) {
        mCtx = context;
        requestQueue = getRequestQueue();
    }

    private static synchronized ImageLoader.ImageCache getImageLoaderInstance() {
        if (imageCache == null) {
            imageCache =  new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            };
        }
        return imageCache;
    }

    public static synchronized MyRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new MyRequestQueue(context);
        }
        return instance;
    }

    public static synchronized ImageLoader getImageLoaderInstance(Context context) {
        if (imageLoaderInstance == null) {
            imageLoaderInstance =  new ImageLoader(getInstance(context).requestQueue, getImageLoaderInstance());
        }
        return imageLoaderInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
