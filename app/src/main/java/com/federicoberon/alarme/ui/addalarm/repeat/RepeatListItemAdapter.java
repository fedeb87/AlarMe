package com.federicoberon.alarme.ui.addalarm.repeat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.federicoberon.alarme.R;

import java.util.List;

public class RepeatListItemAdapter extends ArrayAdapter<String> {

    private final String currentlySelected;
    private int color;

    public RepeatListItemAdapter(Context context, int resource, List<String> objects, String current, boolean isOn) {
        super(context, resource, objects);

        updateList(context, isOn);
        currentlySelected = current;
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) convertView = new RepeatListItem(getContext(), this.color);

        String name = getItem(position);
        RepeatListItem packView = (RepeatListItem) convertView;

        // set name and checked state of vibration item
        packView.setRepeatName(name);
        if (name != null && name.equals(currentlySelected)) {
            packView.setChecked(true);
        }

        return convertView;
    }

    public void updateList(Context context, boolean isEnabled) {
        if(isEnabled)
            this.color = ContextCompat.getColor(context, R.color.white);
        else
            this.color = ContextCompat.getColor(context, R.color.white_transparent);
    }
}