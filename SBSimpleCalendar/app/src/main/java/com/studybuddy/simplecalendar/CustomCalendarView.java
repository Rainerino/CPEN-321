package com.studybuddy.simplecalendar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomCalendarView extends LinearLayout{

    private ImageButton NextButton;
    private ImageButton PreviousButton;
    private TextView CurrentDate;
    private GridView gridView;

    private static final int MAX_CALENDAR_DAYS = 42;

    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

    private Context context;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    private SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private MyGridAdapter myGridAdapter;
    private AlertDialog alertDialog;
    private List<Date> dates = new ArrayList<>();
    private List<Events> eventsList = new ArrayList<>();

    private DBOpenHelper dbOpenHelper;

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeLayout();
        setUpCalendar();

        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();
            }
        });

        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
            }
        });

        gridViewWeek();

        gridViewAddEvents();

        //gridViewShowEvents();

    }

    private void gridViewWeek(){
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String sunDate = eventDateFormat.format(dates.get(position));
              ///*
                String monDate = eventDateFormat.format(dates.get(position + 1));
                String tueDate = eventDateFormat.format(dates.get(position + 2));
                String wedDate = eventDateFormat.format(dates.get(position + 3));
                String thursDate = eventDateFormat.format(dates.get(position + 4));
                String friDate = eventDateFormat.format(dates.get(position + 5));
                String satDate = eventDateFormat.format(dates.get(position + 6));
                //*/

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);

                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_layout, null);

                RecyclerView sunRv = showView.findViewById(R.id.sunEvents);
                ///*
                RecyclerView monRv = showView.findViewById(R.id.monEvents);
                RecyclerView tueRv = showView.findViewById(R.id.tueEvents);
                RecyclerView wedRv = showView.findViewById(R.id.wedEvents);
                RecyclerView thursRv = showView.findViewById(R.id.thursEvents);
                RecyclerView friRv = showView.findViewById(R.id.friEvents);
                RecyclerView satRv = showView.findViewById(R.id.satEvents);
                //*/

                RecyclerView.LayoutManager sunlayoutManager = new LinearLayoutManager(showView.getContext());
            ///*
                RecyclerView.LayoutManager monlayoutManager = new LinearLayoutManager(showView.getContext());
                RecyclerView.LayoutManager tuelayoutManager = new LinearLayoutManager(showView.getContext());
                RecyclerView.LayoutManager wedlayoutManager = new LinearLayoutManager(showView.getContext());
                RecyclerView.LayoutManager thurslayoutManager = new LinearLayoutManager(showView.getContext());
                RecyclerView.LayoutManager frilayoutManager = new LinearLayoutManager(showView.getContext());
                RecyclerView.LayoutManager satlayoutManager = new LinearLayoutManager(showView.getContext());

                //*/

                //Sunday

                sunRv.setLayoutManager(sunlayoutManager);
                sunRv.setHasFixedSize(true);

                EventRecyclerAdapter sunEra = new EventRecyclerAdapter(showView.getContext(),
                        collectEventByDate(sunDate));

                sunRv.setAdapter(sunEra);
                sunEra.notifyDataSetChanged();

                //Monday
                ///*

                monRv.setLayoutManager(monlayoutManager);
                monRv.setHasFixedSize(true);

                EventRecyclerAdapter monEra = new EventRecyclerAdapter(showView.getContext(),
                        collectEventByDate(monDate));

                monRv.setAdapter(monEra);
                monEra.notifyDataSetChanged();

                //Tuesday

                tueRv.setLayoutManager(tuelayoutManager);
                tueRv.setHasFixedSize(true);

                EventRecyclerAdapter tueEra = new EventRecyclerAdapter(showView.getContext(),
                        collectEventByDate(tueDate));

                tueRv.setAdapter(tueEra);
                tueEra.notifyDataSetChanged();

                //Wednesday

                wedRv.setLayoutManager(wedlayoutManager);
                wedRv.setHasFixedSize(true);

                EventRecyclerAdapter wedEra = new EventRecyclerAdapter(showView.getContext(),
                        collectEventByDate(wedDate));

                wedRv.setAdapter(wedEra);
                wedEra.notifyDataSetChanged();

                //Thursday

                thursRv.setLayoutManager(thurslayoutManager);
                thursRv.setHasFixedSize(true);

                EventRecyclerAdapter thursEra = new EventRecyclerAdapter(showView.getContext(),
                        collectEventByDate(thursDate));

                thursRv.setAdapter(thursEra);
                thursEra.notifyDataSetChanged();

                //Friday

                friRv.setLayoutManager(frilayoutManager);
                friRv.setHasFixedSize(true);

                EventRecyclerAdapter friEra = new EventRecyclerAdapter(showView.getContext(),
                        collectEventByDate(friDate));

                friRv.setAdapter(friEra);
                friEra.notifyDataSetChanged();

                //Saturday

                satRv.setLayoutManager(satlayoutManager);
                satRv.setHasFixedSize(true);

                EventRecyclerAdapter satEra = new EventRecyclerAdapter(showView.getContext(),
                        collectEventByDate(satDate));

                satRv.setAdapter(satEra);
                satEra.notifyDataSetChanged();



                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.getWindow().setLayout(900, 900);
                alertDialog.show();

                return true;
            }
        });
    }

    private void gridViewAddEvents(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout, null);
                final EditText EventName = addView.findViewById(R.id.eventname);
                final TextView EventTime = addView.findViewById(R.id.eventtime);
                ImageButton SetTime = addView.findViewById(R.id.seteventtime);
                Button AddEvent = addView.findViewById(R.id.addevent);

                SetTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        c.setTimeZone(TimeZone.getDefault());
                                        SimpleDateFormat hformat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                        String event_Time = hformat.format(c.getTime());
                                        EventTime.setText(event_Time);
                                    }
                                }, hours, minutes, false);
                        timePickerDialog.show();
                    }
                });

                final String date = eventDateFormat.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));

                AddEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year);
                        setUpCalendar();
                        alertDialog.dismiss();
                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

    /*
    private void gridViewShowEvents(){
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormat.format(dates.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);

                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);

                RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);

                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),
                        collectEventByDate(date));

                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
    }*/

    private ArrayList<Events> collectEventByDate(String date){
        ArrayList<Events> arrayList = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readEvents(date, database);

        while (cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String Date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String Month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));

            Events events = new Events(event, time, Date, Month, Year);

            arrayList.add(events);
        }
        cursor.close();

        dbOpenHelper.close();

        return arrayList;
    }



    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void collectEventsPerMonth(String month, String year){
        eventsList.clear();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readEventsPerMonth(month, year, database);

        while (cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String Month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));

            Events events = new Events(event, time, date, Month, Year);
            eventsList.add(events);
        }

        cursor.close();
        dbOpenHelper.close();
    }

    private void saveEvent(String event, String time, String date, String month, String year){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.saveEvent(event, time, date, month, year, database);
        dbOpenHelper.close();
        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();
    }

    private void initializeLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);

        NextButton = view.findViewById(R.id.nextBtn);
        PreviousButton = view.findViewById(R.id.previousBtn);
        CurrentDate = view.findViewById(R.id.current_date);
        gridView = view.findViewById(R.id.gridview);
    }

    private void setUpCalendar(){
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);

        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        collectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while (dates.size()<MAX_CALENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        myGridAdapter = new MyGridAdapter(context, dates, calendar, eventsList);
        gridView.setAdapter(myGridAdapter);
    }
}
