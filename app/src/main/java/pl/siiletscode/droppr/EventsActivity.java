package pl.siiletscode.droppr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.RadioGroup;

import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.RESTConnection.LoggedInUser;
import pl.siiletscode.droppr.fragments.EventListFragment;
import pl.siiletscode.droppr.fragments.EventListFragment_;
import pl.siiletscode.droppr.fragments.EventsMapFragment;
import pl.siiletscode.droppr.fragments.EventsMapFragment_;
import pl.siiletscode.droppr.model.Event;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_events)
@OptionsMenu(R.menu.menu_events)
public class EventsActivity extends AppCompatActivity {

    public static final int NEW_EVENT_REQUEST = 1804;
    public static final int LOGIN_REQUEST = 3204;
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
    private int selectedOrder;
    @Bean
    public DropprConnector connector;
    @Bean
    public LoggedInUser userStorage;
    private List<Event> eventList;
    private ProgressDialog progressDialog;

    @AfterViews
    void init() {
        if(userStorage.getUser().getEmail() == null) {
            SignInActivity_.intent(this).startForResult(LOGIN_REQUEST);
        } else {
            downloadEvents();
        }
    }

    @OnActivityResult(LOGIN_REQUEST)
    void onLoginActivityReturned(int result) {
        if(result == RESULT_OK) {
            downloadEvents();
        } else {
            finish();
        }
    }

    private Subscription downloadEvents() {
        showProgress();
        return connector.
                getEventList().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(EventsActivity.this::initList, this::onDownloadFailed, this::hideProgress);
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.downloading);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideProgress() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void onDownloadFailed(Throwable throwable) {
        Logger.e(throwable, throwable.getMessage());
        hideProgress();
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
        fillAdapter();
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
        builder.setView(R.layout.dialog_sort);
        builder.setPositiveButton(R.string.sort, null);
        builder.setNegativeButton(R.string.cancel, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            final RadioGroup typeGroup = (RadioGroup) alertDialog.findViewById(R.id.sortType);
            selectedSort = typeGroup.getCheckedRadioButtonId();
            final RadioGroup orderGroup = (RadioGroup) alertDialog.findViewById(R.id.sortOrder);
            selectedOrder = orderGroup.getCheckedRadioButtonId();
            handleSortIssues();
            alertDialog.dismiss();
        });
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        builder.show();
    }

    private void handleSortIssues() {
        final boolean ascending = selectedOrder == R.id.ascending;
        switch (selectedSort) {
            case R.id.distance:
                sortByDistance(ascending);
                break;
            case R.id.sportType:
                sortByType(ascending);
                break;
            case R.id.participantCount:
                sortByParticipantCount(ascending);
                break;
            case R.id.timeToStart:
                sortByTime(ascending);
                break;
            default:
                break;
        }

    }

    private void sortByTime(boolean ascending) {
        final Comparator<Event> comparator = (lhs, rhs) -> {
            int result = (int) (lhs.getEventDateMilis() - rhs.getEventDateMilis());
            if(!ascending) result *= -1;
            return result;
        };
        Collections.sort(eventList, comparator);
        fillAdapter();
    }

    private void fillAdapter() {
        listFragment.setEvents(eventList);
        mapFragment.setEvents(eventList);
    }

    private void sortByParticipantCount(boolean ascending) {
        final Comparator<Event> comparator = (lhs, rhs) -> {
            int result = lhs.getParticipantCount() - rhs.getParticipantCount();
            if(!ascending) result *= -1;
            return result;
        };
        Collections.sort(eventList, comparator);
        fillAdapter();
    }

    private void sortByType(boolean ascending) {
        final Comparator<Event> comparator = (lhs, rhs) -> {
            int result = lhs.getEventType().compareTo(rhs.getEventType());
            if(!ascending) result *= -1;
            return result;
        };
        Collections.sort(eventList, comparator);
        fillAdapter();
    }

    private void sortByDistance(boolean ascending) {
//        Comparator comparator = new Comparator() {
//            @Override
//            public int compare(Object lhs, Object rhs) {
//                return 0;
//            }
//        }
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
