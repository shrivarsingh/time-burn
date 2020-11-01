package com.shrivarsingh.timemanagerapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar timePickerCalender = Calendar.getInstance();
        int timePickerHour = timePickerCalender.get(Calendar.HOUR_OF_DAY);
        int timePickerMinute = timePickerCalender.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), timePickerHour, timePickerMinute, DateFormat.is24HourFormat(getActivity()));
    }
}