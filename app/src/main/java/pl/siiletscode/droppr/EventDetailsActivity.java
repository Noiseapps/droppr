package pl.siiletscode.droppr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.model.Event;
import pl.siiletscode.droppr.model.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Walen on 2015-11-21.
 */
@EActivity(R.layout.event_details)
public class EventDetailsActivity extends AppCompatActivity {
    @Bean
    public DropprConnector connector;
    public static final float ZOOM = 16f;
    @ViewById(R.id.eventName)
    TextView eventName;

    @ViewById
    Toolbar toolbar;
    @ViewById
    TextView eventDateText;
    @ViewById
    TextView eventDistance;
    @ViewById
    TextView ownerName;
    @ViewById
    ListView guestList;
    @FragmentById
    SupportMapFragment mapFragment;

    @Extra
    Event event;

    GoogleMap map;

    @AfterViews
    void init() {
        connector.getEventGuests(event.getId()).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> initGuestList(users));
    }

    void initGuestList(List<User> users) {
        event.setGuests(users);
        ParticipantListAdapter adapter = new ParticipantListAdapter(this, android.R.id.text1, (User[]) event.getGuests().toArray());
        guestList.setAdapter(adapter);
        eventDateText.setText(DateFormat.getDateTimeInstance().format(new Date(event.getEventDateMilis())));
        Location loc = new Location("");
        Location userLocation = new Location("");
        loc.setLongitude(event.getLon());
        loc.setLatitude(event.getLat());
        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }catch(SecurityException ex){

        }
        eventDistance.setText(Double.toString(loc.distanceTo(userLocation)));
        mapFragment.getMapAsync(this::onMapReady);
    }

    private void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setMyLocationEnabled(true);
        setEvent(event);
    }

    public void setEvent(Event event) {
        map.clear();
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(event.getName());
        markerOptions.position(new LatLng(event.getLat(), event.getLon()));
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), ZOOM));
    }

}
