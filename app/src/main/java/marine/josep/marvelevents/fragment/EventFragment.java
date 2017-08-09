package marine.josep.marvelevents.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import marine.josep.marvelevents.R;
import marine.josep.marvelevents.databinding.FragmentEventBinding;
import marine.josep.marvelevents.model.Event;
import marine.josep.marvelevents.service.EventService;
import marine.josep.marvelevents.service.MarvelRequestService;
import marine.josep.marvelevents.util.Constants;
import marine.josep.marvelevents.viewmodel.EventListViewModel;

import static android.content.Context.MODE_PRIVATE;

public class EventFragment extends Fragment {

    private EventListViewModel eventListViewModel =  new EventListViewModel();
    private FragmentEventBinding binding;

    private Snackbar snackbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventBinding.inflate(inflater, container, false);
        binding.setEventListViewModel(eventListViewModel);

        snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.loading), Snackbar.LENGTH_INDEFINITE);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.MARVEL_EVENTS_CENTER_SHARED_PREFERENCE, MODE_PRIVATE);
        String modifiedSince = prefs.getString(Constants.EVENT_LAST_SYNC, "1912-23-06");
        EventService.getInstance(getActivity()).getAll(modifiedSince, new MarvelRequestService.SyncListListener() {
            @Override
            public void onSyncList(List<?> list) {

                eventListViewModel.items.addAll((List<Event>)list);
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.MARVEL_EVENTS_CENTER_SHARED_PREFERENCE, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.EVENT_LAST_SYNC, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                editor.commit();

            }
        });

        return binding.getRoot();
    }

}
