package com.blogspot.jimzhou001.experimentmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blogspot.jimzhou001.experimentmanager.Note;
import com.blogspot.jimzhou001.experimentmanager.R;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {

    private int resourceId;

    public NoteAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView)view.findViewById(R.id.experimenttitle);
            viewHolder.date = (TextView)view.findViewById(R.id.tv_date);
            viewHolder.time = (TextView)view.findViewById(R.id.tv_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.title.setText(note.getTitle());
        viewHolder.date.setText(note.getDate());
        viewHolder.time.setText(note.getTime());
        return view;
    }

    class ViewHolder {
        TextView title;
        TextView date;
        TextView time;
    }

}
