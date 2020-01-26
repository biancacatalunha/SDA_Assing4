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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * Show a date picker when the select date button is clicked
 * Create a new date picker if it is not yet created or initialize it otherwise
 * Reference:
 * Code adapted from - https://github.com/wdullaer/MaterialDateTimePicker
 */
public class ReservationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ReservationActivity";
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private DatePickerDialog dpd;
    Calendar now;
    Button fromButton;
    Button toButton;
    Button confirmButton;
    TextView fromDateTextView;
    TextView toDateTextView;
    TextView reservationSummary;
    TextView reservationIntro;
    Calendar toDate;
    Calendar fromDate;
    Intent intent;
    //Instead of having everything in one page separate each step
    //from -> to -> reservation summary -> confirm
    //with back buttons

    /**
     * Required empty public constructor
     */
    public ReservationActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_activity);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        fromButton = findViewById(R.id.fromButton);
        toButton = findViewById(R.id.toButton);
        confirmButton = findViewById(R.id.confirmButton);
        fromDateTextView = findViewById(R.id.fromDate);
        toDateTextView = findViewById(R.id.toDate);
        reservationSummary = findViewById(R.id.reservationSummary);
        reservationIntro = findViewById(R.id.reservationIntro);

        reservationIntro.setText(String.format("%s %s", getString(R.string.reservation_intro), intent.getStringExtra(BooksAdapter.BOOK_TITLE)));

        setOnClickListeners();
    }

    public void setOnClickListeners() {
        fromButton.setOnClickListener(v -> {
            dpd = getDatePicker();

            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.MONTH, 3);
            dpd.setMinDate(now);
            dpd.setMaxDate(maxDate);
            Calendar pickUp = Calendar.getInstance();
            Calendar dropOff = Calendar.getInstance();
            dropOff.add(Calendar.DATE, 3);
            ArrayList<Calendar> range = getDatesBetween(pickUp, dropOff);

            ArrayList<Date> rangeDate = new ArrayList<>();
            for (Calendar k : range) {
                rangeDate.add(k.getTime());
            }

            Calendar[] array = range.toArray(new Calendar[0]);

            Log.d(TAG, "Range is " + rangeDate.toString());

            dpd.setDisabledDays(array);


            dpd.setOnCancelListener(dialog -> {
                Log.d(TAG, "Dialog was cancelled");
                dpd = null;
            });
            dpd.show(getSupportFragmentManager(), TAG);
        });

        toButton.setOnClickListener(v -> {
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
        dpd.setTitle(String.format("%s %s", getString(R.string.reservation_intro), intent.getStringExtra(BooksAdapter.BOOK_TITLE)));

        return dpd;
    }

    /**
     * https://www.baeldung.com/java-between-dates
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static ArrayList<Calendar> getDatesBetween(Calendar startDate, Calendar endDate) {
        ArrayList<Calendar> datesInRange = new ArrayList<>();

        while (startDate.before(endDate)) {
            Calendar result = new GregorianCalendar();
            result.setTime(startDate.getTime());
            datesInRange.add(result);
            startDate.add(Calendar.DATE, 1);
        }
        return datesInRange;
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
        fromDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);

        fromDateTextView.setText(String.format("%s, %s", getString(R.string.picked_date), dateFormat.format(fromDate.getTime())));
        toButton.setEnabled(true);
        dpd = null;
    }
}
