package pro.quizer.quizerexit.model.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import pro.quizer.quizerexit.model.ElementDatabaseType;

@Table(name = "ElementDatabaseModel")
public class ElementDatabaseModel extends Model implements Serializable, Parcelable {

    public static final String TOKEN = "token";

    // like id
    @Column(name = TOKEN)
    public String token;

    @Column(name = "relative_id")
    public Integer relative_id;

    @Column(name = "relative_parent_id")
    public Integer relative_parent_id;

    @Column(name = "item_order")
    public Integer item_order;

    @Column(name = "duration")
    public Long duration;

    @Column(name = "click_rank")
    public Integer click_rank;

    @Column(name = "rank")
    public Integer rank;

    @Column(name = "value")
    public String value;

    @ElementDatabaseType
    @Column(name = "type")
    public String type;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}