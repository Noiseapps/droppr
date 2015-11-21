package pl.siiletscode.droppr;

import android.app.Activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;

import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.model.Event;

/**
 * Created by Walen on 2015-11-21.
 */
public class EventDetailsActivity extends Activity {
    @Bean
    public DropprConnector connector;
    private Event event;



    @AfterViews
    void init(){

    }

}
