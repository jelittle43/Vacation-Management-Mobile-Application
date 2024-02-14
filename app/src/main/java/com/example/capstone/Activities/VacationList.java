package com.example.capstone.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;



import com.example.capstone.R;
import com.example.capstone.database.Repository;
import com.example.capstone.entities.Excursion;
import com.example.capstone.entities.Vacation;
import com.example.capstone.util.PdfGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//inheritance
public class VacationList extends AppCompatActivity {
    private Repository repository;
    private VacationAdapter vacationAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the fab click event
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });

        //polymorphism

        searchView = findViewById(R.id.searchView);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        repository = new Repository(getApplication());

        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupSearchView();

        loadAllVacations();
    }

    private void loadAllVacations() {
        List<Vacation> allVacations = repository.getAllVacations();
        vacationAdapter.setVacations(allVacations);
    }

    private void filterVacations(String query) {
        Executor backgroundExecutor = Executors.newSingleThreadExecutor();

        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Vacation> filteredList = repository.searchVacationsByName(query);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            vacationAdapter.setVacations(filteredList);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterVacations(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVacations(newText);
                return true;
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(VacationList.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllVacations();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else if (item.getItemId() == R.id.generatepdf) {
            exportVacationsToPdf();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportVacationsToPdf() {
        List<Vacation> allVacations = repository.getAllVacations();
        List<Excursion> allExcursions = repository.getAllExcursions();

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/vacations_report.pdf";
        PdfGenerator.generateVacationsReport(allVacations, allExcursions, filePath);

        showToast("PDF generated successfully and saved to Downloads folder.");
    }
}
