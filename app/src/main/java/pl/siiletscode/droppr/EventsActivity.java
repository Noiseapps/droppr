package pl.siiletscode.droppr;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import pl.siiletscode.droppr.fragments.EventListFragment_;
import pl.siiletscode.droppr.fragments.EventsMapFragment_;

@EActivity(R.layout.activity_events)
public class EventsActivity extends AppCompatActivity {

    public static final int NEW_EVENT_REQUEST = 1804;
    @ViewById
    Toolbar toolbar;
    @ViewById(R.id.container)
    ViewPager mViewPager;
    @ViewById(R.id.tabs)
    TabLayout tabLayout;

    private SectionsPagerAdapter mSectionsPagerAdapter;


    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Click(R.id.fab)
    void onFabClick() {
        NewEventActivity_.intent(this).startForResult(NEW_EVENT_REQUEST);
    }

//    @OptionsItem(R.id.actionSort)
//    void onSortList() {
//
//    }
//
//    void onFilterList() {
//
//    }

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
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EventListFragment_.builder().build();
                case 1:
                    return EventsMapFragment_.builder().build();
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
