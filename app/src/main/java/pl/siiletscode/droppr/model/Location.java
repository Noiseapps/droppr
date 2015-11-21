package pl.siiletscode.droppr.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
    private String lat;
    private String lng;

    public double getLng() {
        return Double.parseDouble(lng);
    }

    public void setLng(double lng) {
        this.lng = Double.toString(lng);
    }

    public double getLat() {
        return Double.parseDouble(lng);
    }

    public void setLat(double lat) {
        this.lat = Double.toString(lat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lat);
        dest.writeString(this.lng);
    }

    public Location() {
    }

    protected Location(Parcel in) {
        this.lat = in.readString();
        this.lng = in.readString();
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
