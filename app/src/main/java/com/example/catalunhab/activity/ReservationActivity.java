/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.catalunhab.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.catalunhab.adapter.BooksAdapter;
import com.example.catalunhab.type.Reservation;
import com.example.sdaassign4_2019.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TreeMap;

import static com.example.catalunhab.activity.LoginActivity.userInfo;
import static com.example.catalunhab.fragment.SettingsFragment.NOT_SET;

/**
 * DCU - SDA - Assignment 4
 *
 * Book reservation activity
 * Allows the user to select a pick up and return date
 * Unavailable dates are disabled
 *
 * On a reservation confirmation the status is set to OPEN, the idea behind is if the return
 * date is the future, the reservation status is OPEN.
 * If the return date is in the past, the status is set to CLOSED. (A script would be run everyday
 * to set past reservations to CLOSED)
 * This logic was build in order to optimise the identification of book reserved dates that are
 * yet to come.
 *
 * @author Bianca Catalunha <bianca.catalunha2@mail.dcu.ie>
 * @since January 2020
 *
 * Reference:
 * Date Picker code adapted from - https://github.com/wdullaer/MaterialDateTimePicker
 */
public class ReservationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private static final String TAG = "ReservationActivity";
    private static final String STATUS_OPEN = "OPEN";
    private static final String FROM = "FROM";
    private static final String TO = "TO";
    private static final int MAX_CALENDAR_AVAILABILITY = 3;
    private static final String DATABASE_CHILD_NAME = "reservations";

    private DatePickerDialog dpd;
    private String calendarView;
    private Calendar now;
    private Button fromButton;
    private Button toButton;
    private Button confirmButton;
    private TextView fromDateTextView;
    private TextView toDateTextView;
    private TextView reservationSummary;
    private Calendar toDate;
    private Calendar fromDate;
    private String bookId;
    private Integer lastReservationId;

    /**
     * Required empty public constructor
     */
    public ReservationActivity() { }

    /**
     * - Gets view objects from the layout
     * - Gets data from the intent
     * - Sets text for the reservation intro
     * - Calls method to retrieve reservations from the database
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in #onSaveInstanceState.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_activity);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bookId = intent.getStringExtra(BooksAdapter.BOOK_ID);
        fromButton = findViewById(R.id.fromButton);
        toButton = findViewById(R.id.toButton);
        confirmButton = findViewById(R.id.confirmButton);
        fromDateTextView = findViewById(R.id.fromDate);
        toDateTextView = findViewById(R.id.toDate);
        reservationSummary = findViewById(R.id.reservationSummary);
        TextView reservationIntro = findViewById(R.id.reservationIntro);

        if(!userInfo.getName().equals(NOT_SET)) {
            reservationIntro.setText(String.format("%s %s %s", userInfo.getName(), getString(R.string.reservation_intro_with_user_name), intent.getStringExtra(BooksAdapter.BOOK_TITLE)));
        } else {
            reservationIntro.setText(String.format("%s %s", getString(R.string.reservation_intro), intent.getStringExtra(BooksAdapter.BOOK_TITLE)));
        }

        retrieveReservations();
    }

    /**
     * - Retrieves all reservations from the database
     * - Transforms the JSON into a Reservation object
     * - Stores it into a treeMap to keep them in order
     * - For each reservation referent to the book selected and with status OPEN, gets the dates
     * between pick up and return and add them all to a list. These are the dates the book is not available
     * - Stores the last reservation id into a variable or else 0
     * - Calls the onSetListener method *Note: The call was added here to prevent the listeners from being
     * set before the retrieval of reservations from the database
     */
    public void retrieveReservations() {
        FirebaseDatabase.getInstance().getReference().child(DATABASE_CHILD_NAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        TreeMap<String, Reservation> reservationTreeMap = new TreeMap<>();
                        ArrayList<Calendar> reservedDates = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Reservation reservation = snapshot.getValue(Reservation.class);
                            reservationTreeMap.put(Objects.requireNonNull(reservation).getReservationId(), reservation);

                            if (reservation.getBookId().equals(bookId) && reservation.getStatus().equals(STATUS_OPEN)) {
                                reservedDates.addAll(getDatesBetween(reservation.getPickUpDate(), reservation.getReturnDate()));
                            }
                        }

                        if(!reservationTreeMap.isEmpty()) {
                            lastReservationId = Integer.parseInt(reservationTreeMap.lastKey());
                        } else {
                            lastReservationId = 0;
                        }

                        Log.d(TAG, "Successfully retrieved " + reservationTreeMap.size() + " reservations from the database");
                        Log.d(TAG, "Blocked dates are " + calendarToDate(reservedDates).toString());

                        setOnClickListeners(reservedDates);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "Could not retrieve reservations from database");
                    }

                });
    }

    /**
     * Method to set an on click listener on both to and from buttons
     *
     * From button listener:
     * - Sets the calendar view to FROM so the onDataSet method is able to identify from which date
     * picker(from/to) the date is from
     * - Gets the Date Picker Dialog with common settings
     * - Sets the max date to 3 months from today
     * - Sets the min date to today, books can't be reserved in the past (obviously)
     * - Sets disabled dates. These are the dates the book is already reserved
     *
     * To button listener:
     * - Sets the calendar view to TO so the onDataSet method is able to identify from which date
     * picker(from/to) the date is from
     * - Gets the Date Picker Dialog with common settings
     * - Sets the max date to 3 months from the date the user chose as pick up
     * - Sets the min date to the date the user chose as pick up
     * - Sets disabled dates. These are the dates the book is already reserved
     *
     * @param reservedDates a list of unavailable dates for the current book
     */
    public void setOnClickListeners(ArrayList<Calendar> reservedDates) {
        fromButton.setOnClickListener(v -> {
            calendarView = FROM;
            dpd = getDatePickerCommon();

            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.MONTH, MAX_CALENDAR_AVAILABILITY);
            dpd.setMinDate(now);
            dpd.setMaxDate(maxDate);
            dpd.setDisabledDays(reservedDates.toArray(new Calendar[0]));
            dpd.show(getSupportFragmentManager(), TAG);

            Log.d(TAG, "Pick up calendar displayed");

            dpd.setOnCancelListener(dialog -> {
                Log.d(TAG, "Dialog was cancelled");
                dpd = null;
            });
        });

        toButton.setOnClickListener(v -> {
            calendarView = TO;
            dpd = getDatePickerCommon();

            Calendar maxDate = new GregorianCalendar();
            maxDate.setTime(fromDate.getTime());
            maxDate.add(Calendar.MONTH, 3);
            dpd.setMinDate(fromDate);
            dpd.setMaxDate(maxDate);
            dpd.setDisabledDays(reservedDates.toArray(new Calendar[0]));
            dpd.show(getSupportFragmentManager(), TAG);

            Log.d(TAG, "Return calendar displayed");

            dpd.setOnCancelListener(dialog -> {
                Log.d(TAG, "Dialog was cancelled");
                dpd = null;
            });
        });
    }

    /**
     * If the date picker is null, it will create a new instance based on the current date
     * Otherwise it will initialize the date picker with the current date
     *
     * Common settings for both to and from date pickers are:
     * - Ok and cancel text and colour
     * - Date picker version
     * - Auto dismiss set to true (When a date is clicked, the dialog closes)
     *
     * @return DatePickerDialog with a common view
     */
    private DatePickerDialog getDatePickerCommon() {
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

    /**
     * - Transforms the start and end Date object into a Calendar object
     * - While the start date is before the end date
     *      - Adds the start date to a list
     *      - Increments one to the start date
     *
     * Reference:
     * Get all dates between start/finish - https://www.baeldung.com/java-between-dates
     *
     * @param startDate book pick up date
     * @param endDate book return date
     * @return a list of dates in between the start and end date
     */
    public static ArrayList<Calendar> getDatesBetween(Date startDate, Date endDate) {
        ArrayList<Calendar> datesInRange = new ArrayList<>();
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (startCalendar.before(endCalendar)) {
            Calendar result = new GregorianCalendar();
            result.setTime(startCalendar.getTime());
            datesInRange.add(result);
            startCalendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    /**
     * On destroy, sets the date picker object to null
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        dpd = null;
    }

    /**
     * On resume, sets onDateSetListener if the date picker object is set
     */
    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(TAG);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    /**
     * When a date is picked, identifies from which date picker it comes from
     *
     * FROM:
     * - Stores the date the user wishes to pick up the book to a variable
     * - Displays the date chosen on the screen
     * - Enables the to button
     * - Sets the date picker object to null
     *
     * TO:
     * - Stores the date the user wishes to return the book to a variable
     * - Displays the date chosen on the screen
     * - Displays a summary of the reservation
     * - Enables the confirm button
     * - Sets an onClickListener on the confirm button calling a method to reserve the book
     * - Sets the date picker object to null
     *
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link java.util.Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(calendarView.equals(FROM)) {
            fromDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);

            fromDateTextView.setText(String.format("%s %s", getString(R.string.picked_date), dateFormat.format(fromDate.getTime())));
            toButton.setEnabled(true);
            dpd = null;
        } else if(calendarView.equals(TO)) {
            toDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            toDateTextView.setText(String.format("%s %s", getString(R.string.picked_date), dateFormat.format(toDate.getTime())));
            reservationSummary.setText(R.string.reservation_summary);
            confirmButton.setEnabled(true);
            confirmButton.setOnClickListener(v -> reservationConfirmation());
            dpd = null;
        }
    }

    /**
     * - Creates a new reservation object with an incremented reservation id and status set to OPEN
     * - Uploads it to the reservations schema
     * - Displays a message to the user
     * - Returns to the main screen
     */
    public void reservationConfirmation() {
        now = Calendar.getInstance();

        Reservation reservation = new Reservation(
                String.valueOf(lastReservationId + 1),
                userInfo.getId(),
                bookId,
                fromDate.getTime(),
                toDate.getTime(),
                STATUS_OPEN,
                now.getTime()
        );

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(DATABASE_CHILD_NAME);
        mDatabase.child(reservation.getReservationId()).setValue(reservation);

        Log.d(TAG, "Reservation successful");
        Toast toast = Toast.makeText(getApplicationContext(), "Reservation successful", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Transforms a list of Calendar to a list of Date
     *
     * @param reservedDates list of Calendar
     * @return list of Date
     */
    private ArrayList<Date> calendarToDate(ArrayList<Calendar> reservedDates) {
        ArrayList<Date> rangeDate = new ArrayList<>();
        for (Calendar k : reservedDates) {
            rangeDate.add(k.getTime());
        }
        return rangeDate;
    }
}
