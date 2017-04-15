package shixia.moduletest.module_calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import shixia.moduletest.R;

/**
 * Created by ShiXiuwen on 2017/3/2.
 * Description:
 */

public class CalendarActivity extends AppCompatActivity {

    private Button btnNextMouth;
    private Button btnPreMouth;
    private CalendarView cvCalendar;
    private ScrollView svScrollView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        btnNextMouth = (Button) findViewById(R.id.btn_next_mouth);
        btnPreMouth = (Button) findViewById(R.id.btn_pre_mouth);
        cvCalendar = (CalendarView) findViewById(R.id.cv_calendar);

        svScrollView = (ScrollView) findViewById(R.id.sv_scroll);

        btnNextMouth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvCalendar.nextMonth();
            }
        });

        btnPreMouth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvCalendar.preMonth();
            }
        });

//        cvCalendar.setWeekViewVisible(false);
//        cvCalendar.setDetailDateViewVisible(false);
//        cvCalendar.initCalendarDate(2018,12);
        cvCalendar.setScrollConflictView(svScrollView);

        cvCalendar.setOnCalendarMonthChangeListener(new CalendarView.OnCalendarMonthChangeListener() {
            @Override
            public void onCalendarMonthChange(String currentPageData) {
                Toast.makeText(CalendarActivity.this, currentPageData, Toast.LENGTH_SHORT).show();
            }
        });

        cvCalendar.setOnSomedayClickListener(new CalendarView.OnSomedayClickListener() {
            @Override
            public void onSomedayClick(String data) {
                Toast.makeText(CalendarActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
