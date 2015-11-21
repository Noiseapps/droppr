package pl.siiletscode.droppr;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.joda.time.DateTime;

import java.text.DateFormat;

import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.RESTConnection.LoggedInUser;
import pl.siiletscode.droppr.model.Event;
import pl.siiletscode.droppr.model.EventParticipants;
import pl.siiletscode.droppr.model.User;
import pl.siiletscode.droppr.util.Consts;
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
    private boolean userAlreadyIn;
    @ViewById
    Toolbar toolbar;
    @ViewById
    TextView eventDateText;
    @ViewById
    TextView eventDistance;
    @ViewById
    ProgressBar progressBar;
    @ViewById
    TextView ownerName;
    @ViewById
    ListView guestList;
    @FragmentById
    SupportMapFragment mapFragment;
    @ViewById
    FloatingActionButton joinButton;

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
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.eventDetails);
        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
    }
    @OptionsItem(android.R.id.home)
    void onHome() {
        finish();
    }

    void initGuestList(EventParticipants users) {
        progressBar.setVisibility(View.GONE);
        participants = users;
        User[] usersArray = new User[participants.getParticipants().size()];
        usersArray = participants.getParticipants().toArray(usersArray);
        ParticipantListAdapter adapter = new ParticipantListAdapter(this, android.R.id.text1, usersArray);
        guestList.setAdapter(adapter);
        eventName.setText(event.getName());
        eventDateText.setText(event.getEventTime().toString(Consts.FORMATTER));
        ownerName.setText(participants.getHost().getName() + " " + participants.getHost().getSurname());
        Location loc = new Location("");
        Location userLocation = new Location("");
        loc.setLongitude(event.getLng());
        loc.setLatitude(event.getLat());
        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(userLocation == null) {
                userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }catch(SecurityException ex) {}
        eventDistance.setText(String.format("%.2f km", event.getDistance(userLocation) / 1000));
        userAlreadyIn = false;
        for (User u: participants.getParticipants()) {
            if (u.getId().equals(localUser.getUser().getId())){
                userAlreadyIn = true;
            }
        }
        if (participants.getHost().getId().equals(localUser.getUser().getId())){
            joinButton.setVisibility(View.GONE);
        }else{
        if (userAlreadyIn){
            joinButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_remove_white_24dp));
        }else{
            joinButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_add_white_24px));
        }
        }

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
        if(!userAlreadyIn) {
            connector.addUserToEvent(event.getId(), localUser.getUser().getId()).subscribe(this::onUserJoined);
        }else{
            connector.removeUserFromEvent(event.getId(), localUser.getUser().getId()).subscribe(this::onUserJoined);
        }
    }

    private void onUserJoined(Response r){
        init();
    }

}
