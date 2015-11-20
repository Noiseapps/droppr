package pl.siiletscode.droppr.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SystemService;

import pl.siiletscode.droppr.R;

@EFragment(R.layout.fragment_event_map)
@OptionsMenu(R.menu.menu_events_map)
public class EventsMapFragment extends Fragment implements LocationListener {

    SupportMapFragment mapFragment;

    @SystemService
    LocationManager locationManager;
    private GoogleMap map;

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
        final CameraPosition position = new CameraPosition(new LatLng(55.2f, 18.4f), 12f, 0f, 0f);
        final CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
        map.moveCamera(update);
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
}
