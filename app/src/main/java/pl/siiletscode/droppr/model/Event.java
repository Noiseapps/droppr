package pl.siiletscode.droppr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Walen on 2015-11-20.
 */
public class Event implements Parcelable {
    @SerializedName("_id")
    private String id;
    private String name;
    private String eventType;
    private String createdAt;
    private String eventTime;
    private String lat;
    private String lng;

    public double getLng() {
        return Double.parseDouble(lng);
    }

    public void setLng(double lng) {
        this.lng = Double.toString(lng);
    }

    public double getLat() {
        return Double.parseDouble(lat);
    }

    public void setLat(double lat) {
        this.lat = Double.toString(lat);
    }
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

//    public Location getLocation() {
//        return location;
//    }
//
//    public void setLocation(Location location) {
//        this.location = location;
//    }

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
        if(loc == null) {
            return -1;
        }
        android.location.Location location = new android.location.Location("");
        location.setLatitude(this.getLat());
        location.setLongitude(this.getLng());
        return location.distanceTo(loc);
    }

    public Event() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.eventType);
        dest.writeString(this.createdAt);
        dest.writeString(this.eventTime);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.host);
        dest.writeStringArray(this.guests);
    }

    protected Event(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.eventType = in.readString();
        this.createdAt = in.readString();
        this.eventTime = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.host = in.readString();
        this.guests = in.createStringArray();
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
