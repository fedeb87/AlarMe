package com.federicoberon.alarme.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;

import java.util.Calendar;

public class CustomDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private final AddAlarmViewModel addAlarmViewModel;

    public CustomDatePicker(AddAlarmViewModel addAlarmViewModel){
        super();
        this.addAlarmViewModel = addAlarmViewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        // Create a new instance of DatePickerDialog and return it
        return datePicker;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH,day);

        // assigns current day if the user were selected a past date
        if(cal.get(Calendar.DAY_OF_YEAR)<currentCal.get(Calendar.DAY_OF_YEAR))
            addAlarmViewModel.setDate(currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH)
                    , currentCal.get(Calendar.DAY_OF_MONTH));
        else
            addAlarmViewModel.setDate(year, month, day);
    }
}