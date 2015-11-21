package pl.siiletscode.droppr;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.siiletscode.droppr.model.Event;
import pl.siiletscode.droppr.util.Consts;

/**
 * Created by Walen on 2015-11-21.
 */
public class EventListRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final List<Event> eventList;
    private final EventListCallbacks callbacks;
    private final LocationManager locationService;
    private Location lastKnownLocation;

    public interface EventListCallbacks{
        void onItemSelected(Event event);
    }

    public EventListRecyclerAdapter(@NonNull Context context, @NonNull List<Event> eventList, @NonNull EventListCallbacks callbacks) {
        this.eventList = eventList;
        this.callbacks = callbacks;
        locationService = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            lastKnownLocation = locationService.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            lastKnownLocation = null;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.event_list_item, parent, false);
        //itemView.setOnClickListener(this);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final Event event = eventList.get(position);
        holder.date.setText(event.getEventTime().toString(Consts.FORMATTER));
        if(lastKnownLocation != null) {
            holder.distance.setText(String.format("%.2f km", event.getDistance(lastKnownLocation)/1000));
        }
        holder.title.setText(event.getName());
        holder.type.setText(event.getEventType());
        holder.itemView.setOnClickListener(v -> callbacks.onItemSelected(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


}
