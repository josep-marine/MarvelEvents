package marine.josep.marvelevents.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

import marine.josep.marvelevents.R;
import marine.josep.marvelevents.databinding.ListItemEventBinding;
import marine.josep.marvelevents.databinding.ListItemEventExpandBinding;
import marine.josep.marvelevents.model.Event;
import marine.josep.marvelevents.service.EventService;

public class EventListAdapter extends BaseExpandableListAdapter {

    private List<Event> eventList;
    private Context context;

    public EventListAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public int getGroupCount() {
        return eventList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return eventList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Event event = (Event) getGroup(groupPosition);
        context=parent.getContext();

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.listItemEventBinding = DataBindingUtil.inflate(inflater, R.layout.list_item_event, parent, false);
        View view = viewHolder.listItemEventBinding.getRoot();
        view.setTag(viewHolder);

        viewHolder.listItemEventBinding.setEvent(event);
        viewHolder.listItemEventBinding.executePendingBindings();
        viewHolder.listItemEventBinding.setHandler(this);

        if (event.getThumbBitmap() == null) {
            EventService.getInstance(parent.getContext()).getThumbnail(event, new EventService.ThumbnailResponse() {
                @Override
                public void onThumbnailResponse() {
                    notifyDataSetChanged();
                }
            });
        }

        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.listItemEventExpandBinding = DataBindingUtil.inflate(inflater, R.layout.list_item_event_expand, parent, false);
        View view = viewHolder.listItemEventExpandBinding.getRoot();
        view.setTag(viewHolder);

        viewHolder.listItemEventExpandBinding.setEvent(eventList.get(groupPosition));
        viewHolder.listItemEventExpandBinding.executePendingBindings();
        viewHolder.listItemEventExpandBinding.setHandler(this);

        return view;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewHolder {
        ListItemEventBinding listItemEventBinding;
        ListItemEventExpandBinding listItemEventExpandBinding;
    }

}
