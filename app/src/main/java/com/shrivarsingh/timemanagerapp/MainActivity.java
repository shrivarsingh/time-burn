package com.shrivarsingh.timemanagerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private TextClock currentTime;
    private TextView startTime;
    private TextView hoursOfWork;
    private SeekBar hoursOfWorkSelector;
    private float [] hoursOfWorkStep = {1.5f, 3.0f, 4.5f, 6.0f, 7.5f, 9.0f, 10.5f, 12.0f};
    private FloatingActionButton calculateTimes;
    private ListView timesList;

    FragmentManager fm = getSupportFragmentManager();
    Toast toast;
    Calendar timeManager = Calendar.getInstance();
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
    ArrayList<String> times;
    ArrayList<String> timesDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentTime = findViewById(R.id.dispCurrentTime);
        startTime = findViewById(R.id.startTime);
        hoursOfWork = findViewById(R.id.hoursOfWork);
        hoursOfWorkSelector = findViewById(R.id.hoursOfWorkSelector);
        calculateTimes = findViewById(R.id.calculateTimes);
        timesList = findViewById(R.id.timesList);
        times = new ArrayList<>();
        timesDisplay = new ArrayList<>();

        String timeOpened = simpleTimeFormat.format(timeManager.getTime());
        startTime.setText(timeOpened);
        hoursOfWork.setText("6.0");
        hoursOfWorkSelector.setProgress(3);
        hoursOfWorkSelector.setMax((hoursOfWorkStep.length - 1));

        currentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoFragment infoFragment = new InfoFragment();
                infoFragment.show(fm, "Info Text");
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Select time to start", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Time Picker");
            }
        });

        hoursOfWorkSelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float progressOffset = hoursOfWorkStep[progress];
                hoursOfWork.setText("" + progressOffset);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });

        calculateTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                times.clear();
                timesDisplay.clear();
                String stringStartTime = startTime.getText().toString();
                float hoursToWork = Float.parseFloat(hoursOfWork.getText().toString());
                times.add(stringStartTime);
                boolean workTime = true;

                Calendar timeManagerDisplay = Calendar.getInstance();

                try {
                    Date dateTimeStart = simpleTimeFormat.parse(stringStartTime);
                    timeManager.setTime(dateTimeStart);

                    float floatStartHour = Float.parseFloat(stringStartTime.substring(0, 2));
                    float floatStartMinHour = (Float.parseFloat(stringStartTime.substring(3))) / 60;
                    float floatStartTime = floatStartHour + floatStartMinHour;
                    float floatEndTime = floatStartTime + hoursToWork;
                    int interval = 0;

                    timeManager.setTime(dateTimeStart);
                    for(float i = floatStartTime; i < floatEndTime;){
                        if (workTime){
                            interval = 90;
                            timeManager.add(Calendar.MINUTE, interval);
                            String stringWorkTime = simpleTimeFormat.format(timeManager.getTime());
                            times.add(stringWorkTime);
                            timeManagerDisplay.setTime(timeManager.getTime());
                            timeManagerDisplay.add(Calendar.MINUTE, -interval);
                            String stringIntervalTime = simpleTimeFormat.format(timeManagerDisplay.getTime());
                            timesDisplay.add(stringIntervalTime + " -> " + stringWorkTime);
                            i = i + 1.5f;
                            workTime = false;
                        } else {
                            interval = 15;
                            timeManager.add(Calendar.MINUTE, interval);
                            String stringBreakTime = simpleTimeFormat.format(timeManager.getTime());
                            times.add(stringBreakTime);
                            timeManagerDisplay.setTime(timeManager.getTime());
                            timeManagerDisplay.add(Calendar.MINUTE, -interval);
                            String stringIntervalTime = simpleTimeFormat.format(timeManagerDisplay.getTime());
                            timesDisplay.add(stringIntervalTime + " -> " + stringBreakTime);
                            workTime = true;
                        }
                    }

                    workTime = true;
                    for(int j = 0; j < timesDisplay.size(); j++){
                        String timeInfo;
                        if (workTime){timeInfo = "Work Time"; workTime = false;}
                        else {timeInfo = "Break Time"; workTime = true;}
                        if (j == 0){timeInfo = "Start Time";}
                        if (j == (timesDisplay.size() - 1)){timeInfo = "End Time";}
                        timesDisplay.set(j, timesDisplay.get(j) + " | " + timeInfo);
                    }

                    toast = Toast.makeText(getApplicationContext(), "Times Updated", Toast.LENGTH_SHORT);
                    toast.show();


                } catch (ParseException e) {
                    e.printStackTrace();
                    toast = Toast.makeText(getApplicationContext(), "Cannot parse time", Toast.LENGTH_SHORT);
                    toast.show();
                }

                ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, timesDisplay);
                timesList.setAdapter(timeAdapter);
            }
        });

        timesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedTime = parent.getItemAtPosition(position).toString();
                clickedTime = clickedTime.substring(17);
                toast = Toast.makeText(getApplicationContext(), clickedTime, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String stringStartHour = String.format("%02d", hourOfDay);
        String stringStartMinute = String.format("%02d", minute);
        startTime.setText(stringStartHour + ":" + stringStartMinute);
    }
}
