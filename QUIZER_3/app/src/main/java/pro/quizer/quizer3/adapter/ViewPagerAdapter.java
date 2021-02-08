package com.example.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private String[] mRanks;
    private String[] mCatNames;
    private String[] mCounters;
    private int[] mPictureIDs;

    public ViewPagerAdapter(Context context, String[] ranks, String[] names,
                            String[] counters, int[] resids) {
        this.mContext = context;
        this.mRanks = ranks;
        this.mCatNames = names;
        this.mCounters = counters;
        this.mPictureIDs = resids;
    }

    @Override
    public int getCount() {
        return mRanks.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TextView rankTextView;
        TextView nameTextView;
        TextView counterTextView;
        ImageView avatarImageView;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.pager, container,
                false);

        rankTextView = itemView.findViewById(R.id.textViewRank);
        nameTextView = itemView.findViewById(R.id.textViewName);
        counterTextView = itemView.findViewById(R.id.textViewCount);

        rankTextView.setText(mRanks[position]);
        nameTextView.setText(mCatNames[position]);
        counterTextView.setText(mCounters[position]);

        avatarImageView = itemView.findViewById(R.id.imageViewAvatar);
        avatarImageView.setImageResource(mPictureIDs[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}