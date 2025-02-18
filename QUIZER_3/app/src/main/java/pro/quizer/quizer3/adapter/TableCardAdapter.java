package pro.quizer.quizer3.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Objects;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.CardItem;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;

public class TableCardAdapter extends ArrayAdapter<CardItem> {
    private int resourceLayout;
    private Context mContext;
    private List<CardItem> mItems;

    public TableCardAdapter(Context context, int resource, List<CardItem> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.mItems = items;
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
            holder = vi.inflate(isAutoZoom ? R.layout.holder_table_card_auto : R.layout.holder_table_card, null);
        }

        if (getItem(position) != null) {

            String data = Objects.requireNonNull(getItem(position)).getTitle();
            String thumb = Objects.requireNonNull(getItem(position)).getThumb();

            CardView cont = holder.findViewById(R.id.cont_card);
            TextView title = holder.findViewById(R.id.card_title);
            ImageView titleImage = holder.findViewById(R.id.title_image);

//            title.setText(data);
            UiUtils.setTextOrHide(title, data);
            if (thumb != null && thumb.length() > 0) {
                titleImage.setVisibility(View.VISIBLE);
                showPic(titleImage, thumb);
            } else {
                titleImage.setVisibility(View.GONE);
            }

            cont.setOnClickListener(v -> {
                List<String> pics = Objects.requireNonNull(getItem(position)).getPic();
                if (pics != null && pics.size() > 0) {
                    try {
                        showAdditionalInfoDialog(pics);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        return holder;
    }

    public List<CardItem> getItems() {
        return mItems;
    }

    public void setItems(List<CardItem> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    private void showPic(ImageView view, String data) {
        if (data == null) {
            Picasso.with(mContext)
                    .load(R.drawable.image)
                    .into(view);
            return;
        }

        final String filePhotooPath = getFilePath(data);

        if (StringUtils.isEmpty(filePhotooPath)) {
            return;
        }

        view.setVisibility(View.VISIBLE);

        Picasso.with(mContext)
                .load(new File(filePhotooPath))
                .into(view);
    }

    private String getFilePath(final String data) {
        final String path = FileUtils.getFilesStoragePath(mContext);

        if (StringUtils.isEmpty(data)) {
            return Constants.Strings.EMPTY;
        }

        final String fileName = FileUtils.getFileName(data);

        return path + FileUtils.FOLDER_DIVIDER + fileName;
    }

    private void showAdditionalInfoDialog(List<String> data) {
        Log.d("T-A-R.TableCardAdapter", "showAdditionalInfoDialog: START");
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_table_question_additional_info, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        dialog.setView(mView);

        final TextView title = mView.findViewById(R.id.title);
        final SliderView sliderView = mView.findViewById(R.id.imageSlider);
        final TextView description = mView.findViewById(R.id.description);

        SliderAdapterExample adapter;

        description.setTypeface(description.getTypeface(), Typeface.ITALIC);

        title.setVisibility(View.GONE);
        description.setVisibility(View.GONE);

        if (data != null && data.size() > 0) {
            sliderView.setVisibility(View.VISIBLE);
            adapter = new SliderAdapterExample(mContext);
            adapter.renewItems(data);
            sliderView.setSliderAdapter(adapter);
            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(6);
            sliderView.setAutoCycle(true);
            sliderView.startAutoCycle();
        } else {
            sliderView.setVisibility(View.GONE);
        }

        dialog.setCancelable(true);

        final AlertDialog alertDialog = dialog.create();

        if (mContext != null) {
            alertDialog.show();
        }
    }

}
