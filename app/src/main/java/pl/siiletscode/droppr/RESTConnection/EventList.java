package pl.siiletscode.droppr.RESTConnection;

import java.util.List;

import pl.siiletscode.droppr.model.Event;
import rx.Observable;

/**
 * Created by Walen on 2015-11-20.
 */
public class EventList {
    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
