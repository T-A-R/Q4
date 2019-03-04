package pro.quizer.quizerexit.model.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.List;

@Table(name = "Categories")
public class Category extends Model implements Serializable, Parcelable {

    @Column(name = "Name")
    public String name;

    // This method is optional, does not affect the foreign key creation.
    public List<Item> items() {
        return getMany(Item.class, "Category");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}