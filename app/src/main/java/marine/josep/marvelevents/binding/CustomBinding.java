package marine.josep.marvelevents.binding;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import marine.josep.marvelevents.adapter.EventListAdapter;
import marine.josep.marvelevents.model.Event;

public class CustomBinding {

    @BindingAdapter("items")
    public static void bindEventList(ExpandableListView expandableListView, ObservableArrayList<Event> items) {
        if (items != null) {
            EventListAdapter eventListAdapter = new EventListAdapter(items);
            expandableListView.setAdapter(eventListAdapter);
        }
    }

    @BindingAdapter({"android:src"})
    public static void bindThumb(ImageView imageView, Bitmap thumb) {
        imageView.setImageBitmap(thumb);
    }
}
