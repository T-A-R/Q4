package pro.quizer.quizerexit.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import pro.quizer.quizerexit.database.CategoryConverter;

@Entity
public class ItemR {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "Name")
    private String name;

    @ColumnInfo(name = "Category")
    @TypeConverters({CategoryConverter.class})
    private CategoryR category;

    public ItemR() { }

    public ItemR(int id, String name, CategoryR category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryR getCategory() {
        return category;
    }

    public void setCategory(CategoryR category) {
        this.category = category;
    }
}
