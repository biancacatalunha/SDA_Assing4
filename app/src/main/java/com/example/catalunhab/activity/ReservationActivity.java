package com.example.catalunhab.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.sdaassign4_2019.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
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
            new android.app.DatePickerDialog(
                    ReservationActivity.this,
                    (view1, year, month, dayOfMonth) -> Log.d(TAG, "Got clicked"),
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Show a datepicker when the dateButton is clicked
        dateButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();

            /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */
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

            Calendar[] days = new Calendar[13];
            for (int i = -6; i < 7; i++) {
                Calendar day = Calendar.getInstance();
                day.add(Calendar.DAY_OF_MONTH, i * 2);
                days[i + 6] = day;
            }
            dpd.setSelectableDays(days);

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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        orderSummary.setText(date);
        dpd = null;
    }
}
