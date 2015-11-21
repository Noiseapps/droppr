package pl.siiletscode.droppr.model;

import org.joda.time.DateTime;

/**
 * Created by Walen on 2015-11-20.
 */
public class Event {
    private String id;
    private String name;
    private String eventType;
    private String createdAt;
    private String eventTime;
    private Location location;
    private String host;
    private String[] guests;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public DateTime getCreatedAt() {
        return DateTime.parse(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getEventTime() {
        return DateTime.parse(eventTime);
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String[] getGuests() {
        return guests;
    }

    public void setGuests(String[] guests) {
        this.guests = guests;
    }

    public double getDistance(android.location.Location loc) {
        android.location.Location location = new android.location.Location("");
        location.setLatitude(this.location.getLat());
        location.setLongitude(this.location.getLng());
        return location.distanceTo(loc);
    }
}
