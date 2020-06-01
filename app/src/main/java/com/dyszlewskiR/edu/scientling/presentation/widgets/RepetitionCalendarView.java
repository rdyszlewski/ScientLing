package com.dyszlewskiR.edu.scientling.presentation.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.utils.resources.LocaleUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Razjelll on 29.12.2016.
 */

public class RepetitionCalendarView extends LinearLayout {

    private final int MAX_CALENDAR_COLUMNS = 42;

    private LinearLayout mHeader;
    private ImageView mPrevButton;
    private ImageView mNextButton;
    private TextView mDateTextView;
    private GridView mCalendarGrid;

    private Calendar mCalendar;
    private SimpleDateFormat mFormatter;

    public RepetitionCalendarView(Context context) {
        super(context);
        setupLayout(context);
    }

    public RepetitionCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupLayout(context);
    }

    public RepetitionCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupLayout(context);
    }

    private void setupLayout(Context context) {
        setupComponents(context);
        setButtonsListeners();
        setGridCellClickListener();

        Locale locale = LocaleUtils.getLocale(context);
        mCalendar = Calendar.getInstance(locale);
        mFormatter = new SimpleDateFormat("MMMM yyyyy", locale);
    }


    private void setupComponents(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.repetitions_calendar_view, this);
        mHeader = (LinearLayout) findViewById(R.id.calendar_header);
        mPrevButton = (ImageView) findViewById(R.id.calendar_prev_button);
        mNextButton = (ImageView) findViewById(R.id.calendar_next_button);
        mDateTextView = (TextView) findViewById(R.id.calendar_month_text_view);
        mCalendarGrid = (GridView) findViewById(R.id.calendar_grid_view);
    }

    private void setButtonsListeners() {
        mPrevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.add((Calendar.MONTH), -1);
                setupCalendarAdapter();
            }
        });

        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.add(Calendar.MONTH, 1);
                setupCalendarAdapter();
            }
        });
    }

    private void setGridCellClickListener() {
        mCalendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO tutaj jakaś akcja
            }
        });
    }


    private void setupCalendarAdapter() {

        List<Date> dayValueInCells = new ArrayList<>();
        //TODO pobranie z bazy powtórki
        Calendar calendar = (Calendar) mCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while (dayValueInCells.size() < MAX_CALENDAR_COLUMNS) {
            dayValueInCells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        String date = mFormatter.format(calendar.getTime());
        mDateTextView.setText(date);
        //TODO adapter


    }

    /*private void updateCalendat(){
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar =  (Calendar)currentDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) -1;
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);
        while(cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add((Calendar.DAY_OF_MONTH),1);
        }
        ((CalendarAdapter) mCalendarGrid.getAdapter()).updateData(cells);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy");
        mDateTextView.setValues(simpleDateFormat.format(currentDate.getTime()));
    }*/
}
