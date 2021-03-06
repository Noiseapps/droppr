package pl.siiletscode.droppr.fragments;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SystemService;

import java.util.List;

import pl.siiletscode.droppr.EventDetailsActivity;
import pl.siiletscode.droppr.EventDetailsActivity_;
import pl.siiletscode.droppr.R;
import pl.siiletscode.droppr.model.Event;

@EFragment(R.layout.fragment_event_map)
public class EventsMapFragment extends Fragment implements LocationListener {

    public static final float ZOOM = 16f;
    public static final float TILT = 0f;
    public static final float BEARING = 0f;
    SupportMapFragment mapFragment;

    @SystemService
    LocationManager locationManager;
    private GoogleMap map;
    private List<Event> eventList;

    @AfterViews
    void init() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        try {
            locationManager.requestSingleUpdate(criteria, this, Looper.getMainLooper());
        } catch (SecurityException ex) {
            Logger.d("Failed to fetch location");
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this::onMapReady);
    }

    private void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
        setMapUiSettings(map);
        final Location myLocation = map.getMyLocation();
        map.moveCamera(CameraUpdateFactory.zoomTo(ZOOM));
        if(myLocation != null) {
            final LatLng target = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            final CameraUpdate update = CameraUpdateFactory.newLatLng(target);
            map.moveCamera(update);
        }
        showMarkers();
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

    @OptionsItem(R.id.actionFilter)
    public void onFilter() {
        Logger.d("filter");
    }

    @OptionsItem(R.id.actionSettings)
    void onShowSettings() {
        Logger.d("settings");
    }

    @Override
    public void onLocationChanged(Location location) {
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public void setEvents(List<Event> eventList) {
        this.eventList = eventList;
    }

    private void showMarkers() {
        map.clear();
        for (Event event : eventList) {
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(event.getName());
            markerOptions.position(new LatLng(event.getLat(), event.getLng()));

            map.addMarker(markerOptions);
        }

        map.setOnMarkerClickListener(marker -> {
            for (Event event : eventList) {
                final LatLng markerPosition = marker.getPosition();
                final LatLng eventPosition = new LatLng(event.getLat(), event.getLng());

                if(markerPosition.equals(eventPosition)) {
                    EventDetailsActivity_.intent(getActivity()).event(event).start();
                    return true;
                }
            }
            return false;
        });
    }
}
