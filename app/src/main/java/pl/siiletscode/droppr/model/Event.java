package pl.siiletscode.droppr.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Walen on 2015-11-20.
 */
public class Event implements Parcelable {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String name;
    private float lat;
    private float lon;

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    private long eventDateMilis;
    private String description;
    private List<User> guests;
    private String eventType;
    private int participantCount;

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    private User host;
    private int minParticipants;
    private int maxParticipants;

    public double getDistance(Location loc) {
        final float[] results = new float[1];
        Location.distanceBetween(lat, lon, loc.getLatitude(), loc.getLongitude(), results);
        return results[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEventDateMilis() {
        return eventDateMilis;
    }

    public void setEventDateMilis(long eventDate) {
        this.eventDateMilis = eventDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getGuests() {
        return guests;
    }

    public void setGuests(List<User> guests) {
        this.guests = guests;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public int getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(int minParticipants) {
        this.minParticipants = minParticipants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Event() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lon);
        dest.writeLong(this.eventDateMilis);
        dest.writeString(this.description);
        dest.writeTypedList(guests);
        dest.writeString(this.eventType);
        dest.writeInt(this.participantCount);
        dest.writeParcelable(this.host, 0);
        dest.writeInt(this.minParticipants);
        dest.writeInt(this.maxParticipants);
    }

    protected Event(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.lat = in.readFloat();
        this.lon = in.readFloat();
        this.eventDateMilis = in.readLong();
        this.description = in.readString();
        this.guests = in.createTypedArrayList(User.CREATOR);
        this.eventType = in.readString();
        this.participantCount = in.readInt();
        this.host = in.readParcelable(User.class.getClassLoader());
        this.minParticipants = in.readInt();
        this.maxParticipants = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
