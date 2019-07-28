package com.example.finalproject.data;


import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.finalproject.data.models.HistoryModelEntity;
import com.example.finalproject.data.models.MessageModelEntity;
import com.example.finalproject.ui.App;

@androidx.room.Database(entities = {MessageModelEntity.class, HistoryModelEntity.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    private static final String DATABASE_NAME = "chat_base_main";

    private static Database INSTANCE;

    private static final Object lock = new Object();

    public abstract DataDao dataDao();

    public static Database getInstance(){
        synchronized (lock){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                        App.getContext(),
                        Database.class,
                        DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }

}
