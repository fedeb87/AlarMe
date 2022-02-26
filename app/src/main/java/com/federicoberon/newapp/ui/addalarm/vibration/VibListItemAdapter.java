package com.federicoberon.newapp.ui.addalarm.vibration;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.federicoberon.newapp.R;

import java.util.List;

/**
 * The adapter in charge of creating the list of vibrations from individual vibrationListItems.
 */
public class VibListItemAdapter extends ArrayAdapter<String> {

    /** The item that is set when VibrationPicker is launched */
    private final String currentlySelected;
    private int color;

    /**
     * Default constructor with added currentlySelected parameter to check the currently selected
     * VibrationListItem view.
     */
    public VibListItemAdapter(Context context, int resource, List<String> objects, String current, boolean isOn) {
        super(context, resource, objects);

        updateList(context, isOn);
        currentlySelected = current;
    }

    /**
     * REturns the list item view at position i.
     * @param position Position of requested list item view
     * @param convertView The resused view of position i. If null, re-build the view.
     * @param parent ViewGroup parent
     * @return VibrationListItem view casted as view. The new view that should be shown at i
     */
    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) convertView = new VibrationListItem(getContext(), this.color);

        String name = getItem(position);
        VibrationListItem packView = (VibrationListItem) convertView;

        // set name and checked state of vibration item
        packView.setVibrationName(name);
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