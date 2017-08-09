package marine.josep.marvelevents.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import marine.josep.marvelevents.R;
import marine.josep.marvelevents.request.MyRequestQueue;
import marine.josep.marvelevents.util.SecurityUtil;

public class MarvelRequestService<T> {

    private static AlertDialog dialog;
    private Class<T> typeParameterClass;

    public MarvelRequestService(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    protected void getList(Context context, String path, Map<String, String> params, final ResponseListListener responseListListener, final ErrorListener errorListener, boolean showLoadingDialog) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.loading);
        builder.setCancelable(false);
        dialog = builder.create();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, prepareUrl(context, path, params), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    List<T> objectList = new ArrayList<>();
                    JSONObject data = response.getJSONObject("data");
                    JSONArray resutls = data.getJSONArray("results");

                    int resultIndex = 0;
                    while (resultIndex < resutls.length()) {
                        objectList.add(new Gson().fromJson(resutls.getJSONObject(resultIndex).toString(), typeParameterClass));
                        resultIndex++;
                    }

                    responseListListener.onResponseList(objectList, data.getInt("total"), data.getInt("count"), data.getInt("offset"));


                } catch (JSONException je) {
                    Log.i("MDM", je.toString());
                }finally {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }
        }, errorListener != null ? new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onError(error);
            }
        } : null);

        MyRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);

        if(showLoadingDialog){
            dialog.show();
        }

    }

    public void getBitmap(Context context, String url, final ResponseBitmapListener responseBitmapListener){

        MyRequestQueue.getImageLoaderInstance(context).get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                responseBitmapListener.onResponseBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.setMessage(error.getMessage());
            }
        });
    }

    private String prepareUrl(Context context, String path, Map<String, String> params) {

        Long timeStamp = new Date().getTime();
        StringBuffer url = new StringBuffer(context.getString(R.string.marvel_url_root));
        url.append(path);
        url.append("?apikey=" + context.getString(R.string.marvel_public_apiKey));
        url.append("&ts=" + timeStamp);
        url.append("&hash=" + SecurityUtil.getMD5Hash(timeStamp + context.getString(R.string.marvel_private_apiKey) + context.getString(R.string.marvel_public_apiKey)));
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url.append("&");
                url.append(entry.getKey());
                url.append("=");
                url.append(entry.getValue());
            }
        }

        return url.toString();
    }

    public interface ResponseBitmapListener {
        void onResponseBitmap(Bitmap bitmap);
    }

    public interface ResponseObjectListener {
        void onResponseObject(Object object);
    }

    public interface ResponseListListener {
        void onResponseList(List<?> list, Integer total, Integer count, Integer offset);
    }

    public interface SyncListListener {
        void onSyncList(List<?> list);
    }

    public interface ErrorListener {
        void onError(VolleyError error);
    }
}
