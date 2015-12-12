package pl.siiletscode.droppr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import pl.siiletscode.droppr.R;

/**
 * Created by Walen on 2015-11-21.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView title;
    public TextView distance;
    public TextView date;
    public TextView type;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.eventNameText);
        distance = (TextView) itemView.findViewById(R.id.distanceFromUser);
        date = (TextView) itemView.findViewById(R.id.eventDate);
        type = (TextView) itemView.findViewById(R.id.eventType);
    }




}
