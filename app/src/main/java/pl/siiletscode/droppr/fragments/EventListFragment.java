package pl.siiletscode.droppr.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import pl.siiletscode.droppr.R;

@EFragment(R.layout.fragment_event_list)
public class EventListFragment extends Fragment {

    @ViewById
    RecyclerView eventsList;

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

}