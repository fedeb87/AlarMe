package com.federicoberon.newapp.ui;

import static java.lang.String.format;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.federicoberon.newapp.ui.addalarm.AddAlarmViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class CustomTimePicker extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private final TextInputEditText editTextTime;
    private final AddAlarmViewModel addAlarmViewModel;

    public CustomTimePicker(AddAlarmViewModel addAlarmViewModel, TextInputEditText editTextTime){
        super();
        this.addAlarmViewModel = addAlarmViewModel;
        this.editTextTime = editTextTime;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        addAlarmViewModel.setTime(hourOfDay, minute);
        editTextTime.setText(format("%s", DateFormat.getTimeFormat(requireContext()).format(cal.getTime())));
    }
}