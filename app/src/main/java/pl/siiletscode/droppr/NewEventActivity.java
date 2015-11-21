package pl.siiletscode.droppr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.SupportMapFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.util.ErrorDialogFragmentFactory;

@EActivity(R.layout.activity_new_event)
public class NewEventActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;
    @ViewById
    EditText newEventName;
    @ViewById
    Spinner newEventType;
    @FragmentById
    SupportMapFragment mapFragment;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
    }

    @Click(R.id.fab)
    void onFabClick() {
        // todo call api to add new event
    }

    @Click(R.id.eventAddDateButton)
    void onAddDateClick(){

    }

}
