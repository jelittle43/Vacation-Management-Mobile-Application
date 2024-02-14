package com.example.capstone.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.database.Repository;
import com.example.capstone.entities.Excursion;
import com.example.capstone.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


//validation functionality
public class VacationDetails extends AppCompatActivity {

    Random random = new Random();
    int notificationId;

    String name;
    int vacationID;
    String hotel;
    String startDate;
    String endDate;

    EditText editName;
    EditText editHotel;
    EditText editStartDate;
    EditText editEndDate;

    Repository repository;
    Vacation currentVacation;
    int numExcursions;
    List<Excursion> filteredExcursions;  // Declare a field for filtered excursions

    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();

    private DatePickerDialog.OnDateSetListener startDatePickerDialog;
    private DatePickerDialog.OnDateSetListener endDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        repository = new Repository(getApplication());

        editName = findViewById(R.id.vacationname);
        editHotel = findViewById(R.id.hotelname);
        editStartDate = findViewById(R.id.startdate);
        editEndDate = findViewById(R.id.enddate);

        vacationID = getIntent().getIntExtra("vacID", -1);
        name = getIntent().getStringExtra("vacationName");
        hotel = getIntent().getStringExtra("vacationHotel");
        startDate = getIntent().getStringExtra("vacationStartDate");
        endDate = getIntent().getStringExtra("vacationEndDate");

        editName.setText(name);
        editHotel.setText(hotel);
        editStartDate.setText(startDate);
        editEndDate.setText(endDate);

        editStartDate = findViewById(R.id.startdate);
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(VacationDetails.this, startDatePickerDialog, myCalendarStart
                        .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editEndDate = findViewById(R.id.enddate);
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(VacationDetails.this, endDatePickerDialog, myCalendarEnd
                        .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                        myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, monthOfYear);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }
        };

        endDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, monthOfYear);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelEnd();
            }
        };

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacID", vacationID);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editStartDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    private void updateLabelEnd() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editEndDate.setText(sdf.format(myCalendarEnd.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.vacationsave) {
            Vacation vacation;
            String name = editName.getText().toString();
            String hotel = editHotel.getText().toString();
            String startDate = editStartDate.getText().toString();
            String endDate = editEndDate.getText().toString();


            if (!isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {

                Toast.makeText(VacationDetails.this, "Invalid date format. Please use MM/dd/yy", Toast.LENGTH_LONG).show();
                return true;
            }
            if (!isEndDateAfterStartDate(startDate, endDate)) {

                Toast.makeText(VacationDetails.this, "End date must be after start date", Toast.LENGTH_LONG).show();
                return true;
            }



            if (vacationID == -1) {
                if (repository.getAllVacations().size() == 0) vacationID = 1;
                else
                    vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getVacationID() + 1;

                vacation = new Vacation(vacationID, name, hotel, startDate, endDate);
                repository.insert(vacation);
                Toast.makeText(VacationDetails.this, "Vacation Saved", Toast.LENGTH_LONG).show();
            } else {
                try {
                    vacation = new Vacation(vacationID, name, hotel, startDate, endDate);
                    repository.update(vacation);
                    Toast.makeText(VacationDetails.this, "Vacation Updated", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
            }

            finish();
            return true;
        }

        if (item.getItemId() == R.id.vacationdelete) {
            for (Vacation vac : repository.getAllVacations()) {
                if (vac.getVacationID() == vacationID) currentVacation = vac;
            }

            numExcursions = 0;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getVacationID() == vacationID) ++numExcursions;
            }

            if (numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationName() + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
            }

            finish();
            return true;
        }

        if (item.getItemId() == R.id.startnotification) {
            String dateFromScreen = editStartDate.getText().toString();
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            notificationId = random.nextInt(1000) + 1;

            try{
                Long trigger = myDate.getTime();
                Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
                intent.putExtra("key", name + " is starting");
                PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this, notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);}
            catch (Exception e){
                e.printStackTrace();
            }

            Toast.makeText(VacationDetails.this, "Start notification scheduled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (item.getItemId() == R.id.endnotification) {
            String dateFromScreen = editEndDate.getText().toString();
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            notificationId = random.nextInt(1000) + 1;

            try{
                Long trigger = myDate.getTime();
                Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
                intent.putExtra("key", name + " is ending");
                PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this, notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);}
            catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(VacationDetails.this, "End notification scheduled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(item.getItemId()== R.id.share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, editName.getText().toString() + " " + editHotel.getText().toString() + " " +
                    editStartDate.getText().toString() + " " + editEndDate.getText().toString());
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Sending Vacation Details");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private boolean isValidDateFormat(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isEndDateAfterStartDate(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date startDateObj = sdf.parse(startDate);
            Date endDateObj = sdf.parse(endDate);
            return endDateObj.after(startDateObj);
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }
}