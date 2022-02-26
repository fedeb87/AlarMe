package com.federicoberon.newapp.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.federicoberon.newapp.ui.addalarm.AddAlarmViewModel;

import java.util.Calendar;

public class CustomDatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private final TextView mTextView;
    private final AddAlarmViewModel addAlarmViewModel;

    public CustomDatePicker(AddAlarmViewModel addAlarmViewModel, TextView textView){
        super();
        this.addAlarmViewModel = addAlarmViewModel;
        this.mTextView = textView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        addAlarmViewModel.setDate(year, month, day);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH,day);
        mTextView.setText(DateFormat.getDateFormat(requireContext()).format(cal.getTime()));
    }
}