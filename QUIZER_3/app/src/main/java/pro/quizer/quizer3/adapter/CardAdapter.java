package pro.quizer.quizer3.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.CardItem;

public class CardAdapter extends ArrayAdapter<CardItem> {
    private int resourceLayout;
    private Context mContext;
    private List<CardItem> mItems;
    private boolean isMulti;

    public CardAdapter(Context context, int resource, List<CardItem> items, boolean isMulti) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.mItems = items;
        this.isMulti = isMulti;
    }

    @Nullable
    @Override
    public CardItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View holder = convertView;

        if (holder == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            boolean isAutoZoom = true;
            MainActivity activity = (MainActivity) mContext;
            try {
                isAutoZoom = activity.isAutoZoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder = vi.inflate(isAutoZoom ? R.layout.holder_card_auto : R.layout.holder_card, null);
        }

        if (getItem(position) != null) {
            String text = Objects.requireNonNull(getItem(position)).getTitle();

            if (text != null) {
                boolean checked = Objects.requireNonNull(getItem(position)).isChecked();
                CardView cont = holder.findViewById(R.id.cont_card);
                TextView textView = holder.findViewById(R.id.text1);
                ImageView checker = holder.findViewById(R.id.checker);
                if (isMulti) {
                    checker.setImageResource(checked ? R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
                } else {
                    checker.setImageResource(checked ? R.drawable.radio_button_checked : R.drawable.radio_button_unchecked);
                }
                checker.setVisibility(View.VISIBLE);

                textView.setText(text);
                cont.setOnClickListener(v -> checkItem(position));
            }
        }

        return holder;
    }

    private void checkItem(int position) {
        mItems.get(position).setChecked(!mItems.get(position).isChecked());
        if(!isMulti || mItems.get(position).isUnChecker()) {
            for(int i = 0; i < mItems.size(); i++) {
                if(i != position) {
                    mItems.get(i).setChecked(false);
                }
            }
        }
        if(isMulti) {
            for(int i = 0; i < mItems.size(); i++) {
                if(i != position &&  mItems.get(i).isUnChecker()) {
                    mItems.get(i).setChecked(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<CardItem> getItems() {
        return mItems;
    }

    public void setItems(List<CardItem> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }
}
