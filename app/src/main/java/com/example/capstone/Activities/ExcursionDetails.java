package com.example.capstone.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.database.Repository;
import com.example.capstone.entities.Excursion;
import com.example.capstone.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class ExcursionDetails extends AppCompatActivity {
    String excursionName;
   String excursionDate;
    int excursionID;
    int vacationID;
    EditText editName;
    EditText editDate;
    Repository repository;
    private DatePickerDialog.OnDateSetListener excursionDatePickerDialog;
    final Calendar myCalendarExcursion = Calendar.getInstance();

    Excursion currentExcursion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        repository=new Repository(getApplication());

        editName = findViewById(R.id.excursionName);
        excursionName = getIntent().getStringExtra("excursionName");
        editName.setText(excursionName);

        editDate = findViewById(R.id.excursionDate);
        excursionDate = getIntent().getStringExtra("excursionDate");
        editDate.setText(excursionDate);

        excursionID = getIntent().getIntExtra("excursionID", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ExcursionDetails.this, excursionDatePickerDialog, myCalendarExcursion
                        .get(Calendar.YEAR), myCalendarExcursion.get(Calendar.MONTH),
                        myCalendarExcursion.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        excursionDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendarExcursion.set(Calendar.YEAR, year);
                myCalendarExcursion.set(Calendar.MONTH, monthOfYear);
                myCalendarExcursion.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }
        };

        ArrayList<Vacation> vacationArrayList= new ArrayList<>();
        vacationArrayList.addAll(repository.getAllVacations());
        ArrayList<String> vacationNameList= new ArrayList<>();
        for(Vacation vacation:vacationArrayList){
            vacationNameList.add(vacation.getVacationName());
        }
        ArrayAdapter<String> vacationNameAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,vacationNameList);
        Spinner spinner=findViewById(R.id.spinner);
        spinner.setAdapter(vacationNameAdapter);



        }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDate.setText(sdf.format(myCalendarExcursion.getTime()));
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.excursionsave) {

            if (!isValidDateFormat(editDate.getText().toString())) {
                Toast.makeText(this, "Invalid date format. Please enter a valid date.", Toast.LENGTH_SHORT).show();
                return false;
            }
            Spinner spinner = findViewById(R.id.spinner);
            String selectedVacationName = spinner.getSelectedItem().toString();

            Vacation selectedVacation = null;
            for (Vacation vacation : repository.getAllVacations()) {
                if (vacation.getVacationName().equals(selectedVacationName)) {
                    selectedVacation = vacation;
                    break;
                }
            }

            if (selectedVacation != null) {

                if (!isDateDuringVacation(editDate.getText().toString(), selectedVacation)) {
                    Toast.makeText(this, "Excursion date is not during the associated vacation.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                Excursion excursion;
                if (excursionID == -1) {
                    if (repository.getAllExcursions().size() == 0)
                        excursionID = 1;
                    else
                        excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;

                    if (selectedVacation != null) {
                        vacationID = selectedVacation.getVacationID();
                    }

                    excursion = new Excursion(excursionID, editName.getText().toString(), editDate.getText().toString(), vacationID);
                    repository.insert(excursion);
                } else {

                    if (selectedVacation != null) {
                        vacationID = selectedVacation.getVacationID();
                    }

                    excursion = new Excursion(excursionID, editName.getText().toString(), editDate.getText().toString(), vacationID);
                    repository.update(excursion);
                }
                finish();
                return true;
            }
        }

        if (item.getItemId() == R.id.excursiondelete) {
            for (Excursion exc : repository.getAllExcursions()) {
                if (exc.getExcursionID() == excursionID) currentExcursion = exc;
            }
            repository.delete(currentExcursion);
            Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionName() + " was deleted", Toast.LENGTH_LONG).show();
            this.finish();
        }


            if (item.getItemId() == R.id.notify) {
                String dateFromScreen = editDate.getText().toString();
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Date myDate = null;
                try {
                    myDate = sdf.parse(dateFromScreen);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Random random = new Random();
                int notificationId = random.nextInt(1000) + 1;

                try {
                    Long trigger = myDate.getTime();
                    Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
                    intent.putExtra("key", excursionName);
                    PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
                } catch (Exception e) {

                }
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

    private boolean isValidDateFormat (String excursionDate){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            sdf.setLenient(false);
            sdf.parse(excursionDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isDateDuringVacation(String excursionDate, Vacation selectedVacation) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date date = sdf.parse(excursionDate);


            Date startDate = sdf.parse(selectedVacation.getStartDate());
            Date endDate = sdf.parse(selectedVacation.getEndDate());


            return date != null && (date.equals(startDate) || date.equals(endDate) || (date.after(startDate) && date.before(endDate)));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}




