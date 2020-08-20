package pro.quizer.quizer3.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public boolean isEnabled(int position) {
        Log.d("T-L.SpinnerAdapter", "SpinnerAdapter: HERE ENABLED !!!");
        return position != 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("T-L.SpinnerAdapter", "getView: BLYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + position);
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, android.view.ViewGroup parent){
        Log.d("T-L.SpinnerAdapter", "getDropDownView: DAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        View v = convertView;
        if (v == null) {
            Context mContext = this.getContext();
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Androids orginal spinner view item
            v = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        }
        // The text view of the spinner list view
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
//        String val = reasonArray.get(position);
//        // remove the extra text here
//        tv.setText(val.replace(":False", ""));

        boolean disabled = !isEnabled(position);
        if(disabled){tv.setTextColor(Color.GRAY);}
        else{tv.setTextColor(Color.BLACK);}

        return v;
    }
}
