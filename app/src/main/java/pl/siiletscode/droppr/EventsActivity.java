package pl.siiletscode.droppr;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.fragments.EventListFragment;
import pl.siiletscode.droppr.fragments.EventListFragment_;
import pl.siiletscode.droppr.fragments.EventsMapFragment;
import pl.siiletscode.droppr.fragments.EventsMapFragment_;
import pl.siiletscode.droppr.model.Event;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_events)
@OptionsMenu(R.menu.menu_events)
public class EventsActivity extends AppCompatActivity {

    public static final int NEW_EVENT_REQUEST = 1804;
    private EventListFragment listFragment;
    private EventsMapFragment mapFragment;
    @ViewById
    Toolbar toolbar;
    @ViewById(R.id.container)
    ViewPager mViewPager;
    @ViewById(R.id.tabs)
    TabLayout tabLayout;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int currentFragment;
    private String[] sorts;
    private int selectedSort;
    @Bean
    public DropprConnector connector;
    private List<Event> eventList;

    @AfterViews
    void init() {
        connector.getEventList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(EventsActivity.this::initList);
    }

    private void initList(List<Event> events) {
        eventList = events;
        sorts = getResources().getStringArray(R.array.sorts);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateMenu(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.actionSort).setVisible(currentFragment == 0);
        return super.onPrepareOptionsMenu(menu);
    }

    private void invalidateMenu(int position) {
        currentFragment = position;
        invalidateOptionsMenu();
    }

    @Click(R.id.fab)
    void onFabClick() {
        NewEventActivity_.intent(this).startForResult(NEW_EVENT_REQUEST);
    }

    @OptionsItem(R.id.actionFilter)
    void onFilter() {
        doFilter();
        listFragment.onFilter();
        mapFragment.onFilter();
        Logger.d("filter");
    }

    private void doFilter() {

    }

    @OptionsItem(R.id.actionSort)
    public void sort() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.selectSort);
        builder.setSingleChoiceItems(sorts, selectedSort, (dialog, which) -> selectedSort = which);
        builder.setPositiveButton(R.string.sort, (dialog, which) -> {
            handleSortIssues(selectedSort);
        });

        builder.show();
    }

    private void handleSortIssues(int which) {
        selectedSort = which;
        Comparator<Event> comparator = (lhs, rhs) -> 0;
        switch (which) {
            case 0:
                sortByDistance();
                break;
            case 1:
                sortByType();
                break;
            case 2:
                sortByParticipantCount();
                break;
            case 3:
                sortByTime();
                break;
            default:
                break;
        }
//        final List<Event> issues = new ArrayList<>(this.events);
//        Collections.sort(issues, comparator);
//        onListFiltered(issues);

    }

    private void sortByTime() {

    }

    private void sortByParticipantCount() {

    }

    private void sortByType() {

    }

    private void sortByDistance() {

    }
    @OptionsItem(R.id.actionSettings)
    void onShowSettings() {
        Logger.d("settings");
    }

    @OnActivityResult(NEW_EVENT_REQUEST)
    void onEventAdded(int result) {
        if(result == RESULT_OK) {
            Logger.d("Refresh");
            // todo refresh list
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            listFragment = EventListFragment_.builder().build();
            mapFragment = EventsMapFragment_.builder().build();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return listFragment;
                case 1:
                    return mapFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.eventList);
                case 1:
                    return getString(R.string.eventMap);
            }
            return null;
        }
    }
}
