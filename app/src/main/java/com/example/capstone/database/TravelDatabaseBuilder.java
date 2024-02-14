package com.example.capstone.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.capstone.dao.ExcursionDAO;
import com.example.capstone.dao.VacationDAO;
import com.example.capstone.entities.Excursion;
import com.example.capstone.entities.Vacation;


//a database component with the functionality to securely add, modify, and delete the data
@Database(entities = {Excursion.class, Vacation.class}, version=11, exportSchema = false)
public abstract class TravelDatabaseBuilder extends RoomDatabase {
    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    private static volatile TravelDatabaseBuilder INSTANCE;

    static TravelDatabaseBuilder getDatabase(final Context context){
        if (INSTANCE ==null){
            synchronized (TravelDatabaseBuilder.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),TravelDatabaseBuilder.class,"VacationScheduler.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
