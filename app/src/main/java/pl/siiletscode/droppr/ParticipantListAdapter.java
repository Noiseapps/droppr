package pl.siiletscode.droppr;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.siiletscode.droppr.model.User;

/**
 * Created by Walen on 2015-11-21.
 */
public class ParticipantListAdapter extends ArrayAdapter<User>{
    private List<User> users;
    private Context context;
    int resourceId;

    public ParticipantListAdapter(Context context, int resource, User[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resourceId = resource;
        this.users = new ArrayList<>(Arrays.asList(objects));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);
        }
        User user = users.get(position);
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(user.getName() + " " + user.getSurname());
        return convertView;
    }
}
