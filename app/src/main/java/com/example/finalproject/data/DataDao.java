package com.example.finalproject.data;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.finalproject.data.models.HistoryModelEntity;
import com.example.finalproject.data.models.MessageModelEntity;

@Dao
public interface DataDao {

    @Query("SELECT * FROM history")
    List<HistoryModelEntity> getChats();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHistory(HistoryModelEntity historyModelEntity);

    @Query("Delete from history where id = :id")
    void deleteHistory(long id);

    @Query("Delete from history")
    void deleteHistory();

    @Query("SELECT * FROM messages where userId = :userId")
    List<MessageModelEntity> getMessages(long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMessage(MessageModelEntity messageModelEntity);

    @Query("Delete from messages where userId = :userId")
    void deleteMessages(long userId);

    @Query("Delete from messages")
    void deleteMessages();

}
