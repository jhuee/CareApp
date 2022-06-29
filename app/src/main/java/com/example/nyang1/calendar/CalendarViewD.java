package com.example.nyang1.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nyang1.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

public class CalendarViewD extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private final Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int year = calendarView.getSelectedDate().getYear();
                int month = calendarView.getSelectedDate().getMonth()+1; //index가 0부터 시작하기 때문
                int day = calendarView.getSelectedDate().getDay();
                Intent intent = new Intent(CalendarViewD.this, GoogleCal.class);
                Log.d("날짜", "onDateSelected: " + year +" " + month + " " + day);
                String Syear = Integer.toString(year);
                String Smonth = Integer.toString(month);
                String Sday = Integer.toString(day);
                String Mday = Integer.toString(day+1);
                intent.putExtra("년", Syear);
                intent.putExtra("월", Smonth);
                intent.putExtra("일", Sday);
                intent.putExtra("m일", Mday);


                startActivity(intent);
            }
        });
        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator()
        );
    }





}