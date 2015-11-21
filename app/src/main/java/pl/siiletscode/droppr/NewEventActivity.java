package pl.siiletscode.droppr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.List;

import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.RESTConnection.LoggedInUser;
import pl.siiletscode.droppr.model.Event;
import pl.siiletscode.droppr.model.Location;
import pl.siiletscode.droppr.util.Consts;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_new_event)
public class NewEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final float ZOOM = 16f;
    @ViewById
    Toolbar toolbar;
    @ViewById
    EditText newEventName;
    @ViewById
    TextView dateData;
    @ViewById
    Spinner newEventType;
    @FragmentById
    SupportMapFragment mapFragment;
    @Bean
    DropprConnector connector;
    @Bean
    LoggedInUser loggedInUser;
    private MutableDateTime dateTime;
    private boolean validDate;
    private LatLng latLng;
    private String eventType;
    private GoogleMap map;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        connector.getEventTypes().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::setSpinnerAdapter, this::onDownloadFailed);
        mapFragment.getMapAsync(this::onMapReady);
    }

    private void onDownloadFailed(Throwable throwable) {

    }

    private void setSpinnerAdapter(List<String> strings) {
        final String[] items = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            final String str = strings.get(i);
            items[i] = str;
        }

        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, items);
        newEventType.setAdapter(stringArrayAdapter);
        newEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventType = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
        setMapUiSettings(map);
        final android.location.Location myLocation = map.getMyLocation();
        map.moveCamera(CameraUpdateFactory.zoomTo(ZOOM));
        if (myLocation != null) {
            final LatLng target = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            final CameraUpdate update = CameraUpdateFactory.newLatLng(target);
            map.moveCamera(update);
        }
        map.setOnMapClickListener(latLng -> {
            this.latLng = latLng;
            map.clear();
            MarkerOptions options = new MarkerOptions();
            options.position(latLng).title(getString(R.string.newEvent)).draggable(true);
            map.addMarker(options);
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                NewEventActivity.this.latLng = marker.getPosition();
            }
        });

        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            android.location.Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(userLocation == null) {
                userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())));
        } catch(SecurityException ex) {}
    }

    private void setMapUiSettings(GoogleMap map) {
        final UiSettings uiSettings = map.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
    }


    @Click(R.id.fab)
    void onFabClick() {
        if(latLng == null) {
            Snackbar.make(toolbar, R.string.selectLocation, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (validDate) {
            final Event event = new Event();
            event.setName(newEventName.getText().toString().trim());
            event.setEventTime(dateTime.toString());
            event.setEventType(eventType);
            event.setHost(loggedInUser.getUser().getId());
            event.setLat(latLng.latitude);
            event.setLng(latLng.longitude);
            connector.createEvent(event).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(this::onEventCreated, this::onAddFailed);
        } else {
            Snackbar.make(toolbar, R.string.selectValidDate, Snackbar.LENGTH_LONG).show();
        }
    }

    private void onAddFailed(Throwable throwable) {
        Snackbar.make(toolbar, R.string.failedToAddEvent, Snackbar.LENGTH_LONG).show();
    }

    private void onEventCreated(Event event) {
        EventDetailsActivity_.intent(this).event(event).start();
    }

    @OptionsItem(android.R.id.home)
    void onHome() {
        finish();
    }

    @Click({R.id.dateData, R.id.dateTitle})
    void onAddDateClick() {
        validDate = false;
        final DateTime now = DateTime.now();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, now.getYear(), now.getMonthOfYear() - 1, now.getDayOfMonth());
        datePickerDialog.getDatePicker().setMinDate(now.getMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateTime = new MutableDateTime(year, monthOfYear + 1, dayOfMonth, 0, 0, 0, 0);
        final DateTime now = DateTime.now();
        new TimePickerDialog(this, this, now.getHourOfDay(), now.getMinuteOfHour(), true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        dateTime.setHourOfDay(hourOfDay);
        dateTime.setMinuteOfHour(minute);
        validDate = true;
        dateData.setText(dateTime.toString(Consts.FORMATTER));
    }
}
