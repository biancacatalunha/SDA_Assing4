package com.example.catalunhab.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sdaassign4_2019.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Show a date picker when the select date button is clicked
 * Create a new date picker if it is not yet created or initialize it otherwise
 * Reference:
 * Code adapted from - https://github.com/wdullaer/MaterialDateTimePicker
 */
public class ReservationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ReservationActivity";
    private DatePickerDialog dpd;
    private TextView orderSummary;

    /**
     * Required empty public constructor
     */
    public ReservationActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        Button dateButton = findViewById(R.id.date);
        orderSummary = findViewById(R.id.orderSummary);

        dateButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.MONTH, 3);

            if (dpd == null) {
                dpd = DatePickerDialog.newInstance(
                        ReservationActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
            } else {
                dpd.initialize(
                        ReservationActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
            }

//            Calendar[] days = new Calendar[13];
//            for (int i = -6; i < 7; i++) {
//                Calendar day = Calendar.getInstance();
//                day.add(Calendar.DAY_OF_MONTH, i * 2);
//                days[i + 6] = day;
//            }
//            dpd.setSelectableDays(days);
//            dpd.setDisabledDays();

            dpd.setOkText(R.string.ok);
            dpd.setOkColor(getColor(R.color.colorSurface));
            dpd.setCancelText(R.string.cancel);
            dpd.setCancelColor(getColor(R.color.colorSurface));
            dpd.setVersion(DatePickerDialog.Version.VERSION_2);
            dpd.setMinDate(now);
            dpd.setMaxDate(maxDate);

            dpd.setOnCancelListener(dialog -> {
                Log.d(TAG, "Dialog was cancelled");
                dpd = null;
            });
            dpd.show(getSupportFragmentManager(), TAG);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dpd = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(TAG);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    /**
     * When a date is picked, displays it to the user
     *
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link java.util.Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        orderSummary.setText(date);
        dpd = null;
    }
}
