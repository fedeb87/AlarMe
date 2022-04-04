package com.federicoberon.alarme.ui.addalarm.repeat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.federicoberon.alarme.R;

public class RepeatListItem extends LinearLayout implements Checkable {
    private final AppCompatRadioButton checkBox;

    public RepeatListItem(Context context, int color) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item_vibration, this, true);
        checkBox = v.findViewById(R.id.selection_checkbox);
        checkBox.setTextColor(color);
        checkBox.setTextSize(20);
        checkBox.setPadding(40, 0,0,0);
    }

    public String getRepeatName() {
        return (String)(checkBox.getText());
    }

    public void setRepeatName(String name) {
        checkBox.setText(name);
    }

    @Override
    public boolean isChecked() {
        return checkBox.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    @Override
    public void toggle() {
        checkBox.toggle();
    }
}
