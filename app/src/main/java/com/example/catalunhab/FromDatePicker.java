package com.example.catalunhab;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.sdaassign4_2019.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FromDatePicker extends MyDatePicker {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String TAG = "FromDatePicker";

    private DatePickerDialog dpd;
    Calendar now;
    Button toButton;
    TextView fromDateTextView;
    Calendar fromDate;

    public FromDatePicker() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.reservation_activity);
        now = Calendar.getInstance();

        dpd = DatePickerDialog.newInstance(
                FromDatePicker.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setOkText(R.string.ok);
        dpd.setOkColor(getColor(R.color.colorSurface));
        dpd.setCancelText(R.string.cancel);
        dpd.setCancelColor(getColor(R.color.colorSurface));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.autoDismiss(true);

        //            Calendar[] days = new Calendar[13];
//            for (int i = -6; i < 7; i++) {
//                Calendar day = Calendar.getInstance();
//                day.add(Calendar.DAY_OF_MONTH, i * 2);
//                days[i + 6] = day;
//            }
//            dpd.setSelectableDays(days);
//            dpd.setDisabledDays();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 3);
        dpd.setMinDate(now);
        dpd.setMaxDate(maxDate);

        dpd.setOnCancelListener(dialog -> {
            Log.d(TAG, "Dialog was cancelled");
            dpd = null;
        });
        dpd.show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        fromDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);

        fromDateTextView.setText(String.format("%s, %s", getString(R.string.picked_date), dateFormat.format(fromDate.getTime())));
        toButton.setEnabled(true);
        dpd = null;
    }
}
