package pl.siiletscode.droppr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Walen on 2015-11-21.
 */
public class EventParticipants implements Parcelable {
    private User host;
    private List<User> participants;

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public User getHost() {

        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.host, flags);
        dest.writeList(this.participants);
    }

    public EventParticipants() {
    }

    protected EventParticipants(Parcel in) {
        this.host = in.readParcelable(User.class.getClassLoader());
        this.participants = new ArrayList<User>();
        in.readList(this.participants, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<EventParticipants> CREATOR = new Parcelable.Creator<EventParticipants>() {
        public EventParticipants createFromParcel(Parcel source) {
            return new EventParticipants(source);
        }

        public EventParticipants[] newArray(int size) {
            return new EventParticipants[size];
        }
    };
}
