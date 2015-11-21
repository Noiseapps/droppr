package pl.siiletscode.droppr.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import pl.siiletscode.droppr.EventDetailsActivity_;
import pl.siiletscode.droppr.EventListRecyclerAdapter;
import pl.siiletscode.droppr.R;
import pl.siiletscode.droppr.model.Event;

@EFragment(R.layout.fragment_event_list)
public class EventListFragment extends Fragment {

    @ViewById
    RecyclerView eventsList;
    @ViewById
    TextView emptyView;
    private EventListRecyclerAdapter adapter;
    private List<Event> eventList;

    @AfterViews
    void init() {
        eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @OptionsItem(R.id.actionFilter)
    public void onFilter() {
        Logger.d("filter");
    }

    @OptionsItem(R.id.actionSort)
    public void onSort() {
        Logger.d("sort");
    }

    @OptionsItem(R.id.actionSettings)
    void onShowSettings() {
        Logger.d("settings");
    }

    public void setEvents(List<Event> eventList) {
        this.eventList = eventList;
        adapter = new EventListRecyclerAdapter(getActivity(), eventList, this::showEvent);
        eventsList.setAdapter(adapter);
        setEmptyView();
    }

    private void setEmptyView() {
        if(eventList.isEmpty()) {
            eventsList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            eventsList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEvent(Event event) {
        EventDetailsActivity_.intent(this).event(event).start();
    }
}
