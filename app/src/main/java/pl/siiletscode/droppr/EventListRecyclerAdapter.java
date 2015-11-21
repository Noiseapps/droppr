package pl.siiletscode.droppr;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.siiletscode.droppr.model.Event;

/**
 * Created by Walen on 2015-11-21.
 */
public class EventListRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final List<Event> eventList;
    private final EventListCallbacks callbacks;

    public interface EventListCallbacks{
        void onItemSelected(Event event);
    }

    public EventListRecyclerAdapter(@NonNull Context context, @NonNull List<Event> eventList, @NonNull EventListCallbacks callbacks) {
        this.eventList = eventList;
        this.callbacks = callbacks;
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
        holder.date.setText(DateFormat.getDateTimeInstance().format(new Date(eventList.get(position).getEventDateMilis())).toString());
        Location loc = new Location("");
        loc.setLatitude(0.0);
        loc.setLongitude(0.0);
        holder.distance.setText(Double.toString(eventList.get(position).getDistance(loc)));
        holder.title.setText(eventList.get(position).getName());
        holder.itemView.setOnClickListener(v -> callbacks.onItemSelected(eventList.get(position)));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


}
