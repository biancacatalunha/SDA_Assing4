package com.example.catalunhab.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.catalunhab.adapter.BooksAdapter;
import com.example.sdaassign4_2019.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Objects;

/**
 * Show a date picker when the select date button is clicked
 * Create a new date picker if it is not yet created or initialize it otherwise
 * Reference:
 * Code adapted from - https://github.com/wdullaer/MaterialDateTimePicker
 */
public class ReservationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ReservationActivity";
    private DatePickerDialog dpd;
    Calendar now;
    Button fromButton;
    Button toButton;
    Button confirmButton;
    TextView fromDate;
    TextView toDate;
    TextView reservationSummary;
    TextView reservationIntro;

    /**
     * Required empty public constructor
     */
    public ReservationActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_activity);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        fromButton = findViewById(R.id.fromButton);
        toButton = findViewById(R.id.toButton);
        confirmButton = findViewById(R.id.confirmButton);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        reservationSummary = findViewById(R.id.reservationSummary);
        reservationIntro = findViewById(R.id.reservationIntro);

        reservationIntro.setText(String.format("%s %s", getString(R.string.reservation_intro), intent.getStringExtra(BooksAdapter.BOOK_TITLE)));

        setOnClickListeners();
    }

    public void setOnClickListeners() {
        fromButton.setOnClickListener(v -> {


//            Calendar[] days = new Calendar[13];
//            for (int i = -6; i < 7; i++) {
//                Calendar day = Calendar.getInstance();
//                day.add(Calendar.DAY_OF_MONTH, i * 2);
//                days[i + 6] = day;
//            }
//            dpd.setSelectableDays(days);
//            dpd.setDisabledDays();
            dpd = getDatePicker();
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.MONTH, 3);
            dpd.setMinDate(now);
            dpd.setMaxDate(maxDate);

            dpd.setOnCancelListener(dialog -> {
                Log.d(TAG, "Dialog was cancelled");
                dpd = null;
            });
            dpd.show(getSupportFragmentManager(), TAG);
        });
    }

    private DatePickerDialog getDatePicker() {
        now = Calendar.getInstance();

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

        dpd.setOkText(R.string.ok);
        dpd.setOkColor(getColor(R.color.colorSurface));
        dpd.setCancelText(R.string.cancel);
        dpd.setCancelColor(getColor(R.color.colorSurface));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.autoDismiss(true);

        return dpd;
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
        fromDate.setText(date);
        dpd = null;
    }
}
