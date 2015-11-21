package pl.siiletscode.droppr;

import android.content.Context;
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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;

import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.RESTConnection.LoggedInUser;
import pl.siiletscode.droppr.model.Event;
import pl.siiletscode.droppr.model.EventParticipants;
import pl.siiletscode.droppr.model.User;
import retrofit.client.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Walen on 2015-11-21.
 */
@EActivity(R.layout.event_details)
public class EventDetailsActivity extends AppCompatActivity {
    @Bean
    public DropprConnector connector;
    @Bean
    public LoggedInUser localUser;
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

    private EventParticipants participants;

    @Extra
    Event event;

    GoogleMap map;

    @AfterViews
    void init() {
        connector.getEventGuests(event.getId()).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initGuestList);
        mapFragment.getMapAsync(this::onMapReady);
    }

    void initGuestList(EventParticipants users) {
        participants = users;
        User[] usersArray = new User[participants.getParticipants().size()];
        usersArray = participants.getParticipants().toArray(usersArray);
        ParticipantListAdapter adapter = new ParticipantListAdapter(this, android.R.id.text1, usersArray);
        guestList.setAdapter(adapter);
        eventName.setText(event.getName());
        eventDateText.setText(DateFormat.getDateTimeInstance().format(event.getEventTime().toDate()));
        ownerName.setText(participants.getHost().getName() + " " + participants.getHost().getSurname());
        Location loc = new Location("");
        Location userLocation = new Location("");
        loc.setLongitude(event.getLng());
        loc.setLatitude(event.getLat());
        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }catch(SecurityException ex) {

        }
        eventDistance.setText(String.format("%.2f km", event.getDistance(userLocation) / 1000));

    }

    private void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setMyLocationEnabled(true);
        setEvent(event);
    }

    public void setEvent(Event event) {
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(event.getName());
        markerOptions.position(new LatLng(event.getLat(), event.getLng()));
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), ZOOM));
    }

    @Click(R.id.joinButton)
    public void joinEvent(){
        connector.addUserToEvent(event.getId(), localUser.getUser().getId()).subscribe(this::onUserJoined);
    }

    private void onUserJoined(Response r){
        init();
    }

}
