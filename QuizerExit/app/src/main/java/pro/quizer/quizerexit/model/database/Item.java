package pro.quizer.quizerexit.model.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

@Table(name = "Items")
public class Item extends Model implements Serializable, Parcelable {
        // If name is omitted, then the field name is used.
        @Column(name = "Name")
        public String name;

        @Column(name = "Category")
        public Category category;

        public Item() {
                super();
        }

        public Item(String name, Category category) {
                super();
                this.name = name;
                this.category = category;
        }

        @Override
        public int describeContents() {
                return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }
}
