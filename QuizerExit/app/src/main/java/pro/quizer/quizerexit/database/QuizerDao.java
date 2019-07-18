package pro.quizer.quizerexit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import pro.quizer.quizerexit.database.model.ItemR;
import pro.quizer.quizerexit.model.database.Item;

@Dao
public interface QuizerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(List<Item> items);

    @Query("SELECT * FROM ItemR WHERE id = :itemId")
    List<Item> getItems(int itemId);

    @Delete
    void deleteItem(ItemR item);
}
