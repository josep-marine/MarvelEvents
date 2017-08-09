package marine.josep.marvelevents.service;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marine.josep.marvelevents.dao.EventDao;
import marine.josep.marvelevents.dao.ThumbnailDao;
import marine.josep.marvelevents.model.Event;

public class EventService extends MarvelRequestService<Event> {

    private static final String PATH = "events";
    private static final Integer LIMIT = 10;
    private static EventService instance;
    private Context context;

    private List<Event> eventList = new ArrayList<>();

    private EventService(Context context) {
        super(Event.class);
        this.context = context;
    }

    public static EventService getInstance(Context context) {
        if (instance == null) {
            instance = new EventService(context);
        }
        return instance;
    }

    public void getAll(final String modifiedSince, final SyncListListener syncListListener) {

        Map<String, String> params = new HashMap<>();
        params.put("offset", "0");
        params.put("limit", String.valueOf(LIMIT));
        params.put("modifiedSince", modifiedSince);
        params.put("orderBy","name");

        getAll(params, modifiedSince, syncListListener);
    }

    private void getAll(final Map<String, String> params, final String modifiedSince, final SyncListListener syncListListener) {

        super.getList(context, PATH, params, new ResponseListListener() {
            @Override
            public void onResponseList(List<?> list, Integer total, Integer count, Integer offset) {
                eventList.addAll((List<Event>) list);
                if (eventList.size() < total) {

                    Map<String, String> paramsIter = params;
                    paramsIter.put("offset", String.valueOf(offset + LIMIT));

                    getAll(params, modifiedSince, syncListListener);
                } else {

                    List<Event> localEventList = EventDao.getInstance(context).getAll();
                    for (Event event : eventList) {
                        if (localEventList.contains(event)) {
                            EventDao.getInstance(context).update(event);
                            ThumbnailDao.getInstance(context).update(event.getThumbnail());
                        } else {
                            EventDao.getInstance(context).create(event);
                            ThumbnailDao.getInstance(context).create(event.getThumbnail());
                        }
                    }
                    localEventList = EventDao.getInstance(context).getAll();
                    syncListListener.onSyncList(localEventList);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onError(VolleyError error) {

            }
        }, true);

    }

    public void getThumbnail(final Event event, final ThumbnailResponse thumbnailResponse) {

        if (event.getThumbnail() != null) {
            String url = event.getThumbnail().getPath() + "/landscape_medium." + event.getThumbnail().getExtension();
            super.getBitmap(context, url, new ResponseBitmapListener() {
                @Override
                public void onResponseBitmap(Bitmap bitmap) {
                    if (bitmap != null) {
                        event.setThumbBitmap(bitmap);
                        EventDao.getInstance(context).update(event);
                        thumbnailResponse.onThumbnailResponse();
                    }
                }
            });
        }
    }

    public interface ThumbnailResponse {
        void onThumbnailResponse();
    }
}
