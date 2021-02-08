package pro.quizer.quizer3.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.view.element.TouchImageView;

import static pro.quizer.quizer3.MainActivity.AVIA;
import static pro.quizer.quizer3.model.OptionsOpenType.NUMBER;

public class ViewPagerAdapter extends PagerAdapter {
    private MainActivity mContext;
    final private List<ElementItemR> elementsList;
    private Map<Integer, List<ElementItemR>> elementPages;
    private Map<Integer, AnswerState> statePages;
    private final OnViewElementClickListener onAnswerClickListener;
    private boolean isMulti;

    public ViewPagerAdapter(MainActivity context, List<ElementItemR> elementsList, Map<Integer, AnswerState> statePages, boolean isMulti, OnViewElementClickListener onAnswerClickListener) {
        this.mContext = context;
        this.elementsList = elementsList;
        this.isMulti = isMulti;
        this.statePages = statePages;
        this.onAnswerClickListener = onAnswerClickListener;
        elementPages = new HashMap<>();
        int pageNumber = 0;
        for (int i = 0; i < elementsList.size(); ) {
            List<ElementItemR> page = new ArrayList<>();
            for (int k = 0; k < 8; k++) {
                if (i < elementsList.size()) {
                    page.add(elementsList.get(i));
                }
                i++;
            }
            elementPages.put(pageNumber, page);
            pageNumber++;
        }
    }

    @Override
    public int getCount() {
        return elementPages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.holder_viewpager_avia, container,
                false);

        LinearLayout cont1 = itemView.findViewById(R.id.cont_element_1);
        LinearLayout cont2 = itemView.findViewById(R.id.cont_element_2);
        LinearLayout cont3 = itemView.findViewById(R.id.cont_element_3);
        LinearLayout cont4 = itemView.findViewById(R.id.cont_element_4);
        LinearLayout cont5 = itemView.findViewById(R.id.cont_element_5);
        LinearLayout cont6 = itemView.findViewById(R.id.cont_element_6);
        LinearLayout cont7 = itemView.findViewById(R.id.cont_element_7);
        LinearLayout cont8 = itemView.findViewById(R.id.cont_element_8);
        ZoomInImageView image1 = itemView.findViewById(R.id.pager_image_1);
        ZoomInImageView image2 = itemView.findViewById(R.id.pager_image_2);
        ZoomInImageView image3 = itemView.findViewById(R.id.pager_image_3);
        ZoomInImageView image4 = itemView.findViewById(R.id.pager_image_4);
        ZoomInImageView image5 = itemView.findViewById(R.id.pager_image_5);
        ZoomInImageView image6 = itemView.findViewById(R.id.pager_image_6);
        ZoomInImageView image7 = itemView.findViewById(R.id.pager_image_7);
        ZoomInImageView image8 = itemView.findViewById(R.id.pager_image_8);
        ImageView checker1 = itemView.findViewById(R.id.pager_checker_1);
        ImageView checker2 = itemView.findViewById(R.id.pager_checker_2);
        ImageView checker3 = itemView.findViewById(R.id.pager_checker_3);
        ImageView checker4 = itemView.findViewById(R.id.pager_checker_4);
        ImageView checker5 = itemView.findViewById(R.id.pager_checker_5);
        ImageView checker6 = itemView.findViewById(R.id.pager_checker_6);
        ImageView checker7 = itemView.findViewById(R.id.pager_checker_7);
        ImageView checker8 = itemView.findViewById(R.id.pager_checker_8);
        TextView title1 = itemView.findViewById(R.id.pager_answer_title_1);
        TextView title2 = itemView.findViewById(R.id.pager_answer_title_2);
        TextView title3 = itemView.findViewById(R.id.pager_answer_title_3);
        TextView title4 = itemView.findViewById(R.id.pager_answer_title_4);
        TextView title5 = itemView.findViewById(R.id.pager_answer_title_5);
        TextView title6 = itemView.findViewById(R.id.pager_answer_title_6);
        TextView title7 = itemView.findViewById(R.id.pager_answer_title_7);
        TextView title8 = itemView.findViewById(R.id.pager_answer_title_8);

        List<ElementItemR> items = elementPages.get(position);
        for (int i = 0; i < items.size(); i++) {
            final int relativeId = items.get(i).getRelative_id();
            final int itemPos = i;
            switch (i) {
                case 0:
                    cont1.setVisibility(View.VISIBLE);
                    title1.setText(items.get(0).getElementOptionsR().getTitle());
                    title1.setOnClickListener(v -> {
                        checkItem(position, itemPos);
                        onAnswerClickListener.onAnswerClick(relativeId, null, null);
                    });
                    setChecker(checker1, statePages.get(relativeId).isChecked());
                    showPic(image1, items.get(0));
                    image1.setOnClickListener(v -> showZoomDialog(items.get(0)));

                    break;
                case 1:
                    cont2.setVisibility(View.VISIBLE);
                    title2.setText(items.get(1).getElementOptionsR().getTitle());
                    title2.setOnClickListener(v -> {
                        checkItem(position, itemPos);
                        onAnswerClickListener.onAnswerClick(relativeId, null, null);
                    });
                    setChecker(checker2, statePages.get(relativeId).isChecked());
                    showPic(image2, items.get(1));
                    image2.setOnClickListener(v -> showZoomDialog(items.get(1)));
                    break;
                case 2:
                    cont3.setVisibility(View.VISIBLE);
                    title3.setText(items.get(2).getElementOptionsR().getTitle());
                    title3.setOnClickListener(v -> {
                        checkItem(position, itemPos);
                        onAnswerClickListener.onAnswerClick(relativeId, null, null);
                    });
                    setChecker(checker3, statePages.get(relativeId).isChecked());
                    showPic(image3, items.get(2));
                    image3.setOnClickListener(v -> showZoomDialog(items.get(2)));
                    break;
                case 3:
                    cont4.setVisibility(View.VISIBLE);
                    title4.setText(items.get(3).getElementOptionsR().getTitle());
                    title4.setOnClickListener(v -> {
                        checkItem(position, itemPos);
                        onAnswerClickListener.onAnswerClick(relativeId, null, null);
                    });
                    setChecker(checker4, statePages.get(relativeId).isChecked());
                    showPic(image4, items.get(3));
                    image4.setOnClickListener(v -> showZoomDialog(items.get(3)));
                    break;
                case 4:
                    cont5.setVisibility(View.VISIBLE);
                    title5.setText(items.get(4).getElementOptionsR().getTitle());
                    title5.setOnClickListener(v -> {
                        checkItem(position, itemPos);
                        onAnswerClickListener.onAnswerClick(relativeId, null, null);
                    });
                    setChecker(checker5, statePages.get(relativeId).isChecked());
                    showPic(image5, items.get(4));
                    image5.setOnClickListener(v -> showZoomDialog(items.get(4)));
                    break;
                case 5:
                    cont6.setVisibility(View.VISIBLE);
                    title6.setText(items.get(5).getElementOptionsR().getTitle());
                    title6.setOnClickListener(v -> {
                        checkItem(position, itemPos);
                        onAnswerClickListener.onAnswerClick(relativeId, null, null);
                    });
                    setChecker(checker6, statePages.get(relativeId).isChecked());
                    showPic(image6, items.get(5));
                    image6.setOnClickListener(v -> showZoomDialog(items.get(5)));
                    break;
                case 6:
                    cont7.setVisibility(View.VISIBLE);
                    title7.setText(items.get(6).getElementOptionsR().getTitle());
                    title7.setOnClickListener(v -> {
                        checkItem(position, itemPos);
                        onAnswerClickListener.onAnswerClick(relativeId, null, null);
                    });
                    setChecker(checker7, statePages.get(relativeId).isChecked());
                    showPic(image7, items.get(6));
                    image7.setOnClickListener(v -> showZoomDialog(items.get(6)));
                    break;
                case 7:
                    cont8.setVisibility(View.VISIBLE);
                    title8.setText(items.get(7).getElementOptionsR().getTitle());
                    title8.setOnClickListener(v -> {
                        checkItem(position, itemPos);
                        onAnswerClickListener.onAnswerClick(relativeId, null, null);
                    });
                    setChecker(checker8, statePages.get(relativeId).isChecked());
                    showPic(image8, items.get(7));
                    image8.setOnClickListener(v -> showZoomDialog(items.get(7)));
                    break;
            }
        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    private void showPic(ImageView view, ElementItemR element) {
        final List<ElementContentsR> contents = mContext.getMainDao().getElementContentsR(element.getRelative_id());
        if (contents != null && !contents.isEmpty()) {
            String data = contents.get(0).getData();
            final String filePhotoPath = getFilePath(data);

            if (StringUtils.isEmpty(filePhotoPath)) {
                return;
            }
            view.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(new File(filePhotoPath))
                    .into(view);
        }
    }

    private void showZoomPic(TouchImageView view, ElementItemR element) {
        final List<ElementContentsR> contents = mContext.getMainDao().getElementContentsR(element.getRelative_id());
        if (contents != null && !contents.isEmpty()) {
            String data = contents.get(0).getData();
            final String filePhotoPath = getFilePath(data);

            if (StringUtils.isEmpty(filePhotoPath)) {
                return;
            }
            File image = new File(filePhotoPath);
            Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
//            bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
            view.setVisibility(View.VISIBLE);
            view.setImageBitmap(myBitmap);
        }
    }

    private void showZoomDialog(final ElementItemR element) {
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_pic_zoom, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        dialog.setView(mView);

        final ZoomInImageView inImageView = mView.findViewById(R.id.zoom_image);

        final List<ElementContentsR> contents = mContext.getMainDao().getElementContentsR(element.getRelative_id());
        if (contents != null && !contents.isEmpty()) {
            String data = contents.get(0).getData();
            final String filePhotoPath = getFilePath(data);

            if (StringUtils.isEmpty(filePhotoPath)) {
                return;
            }
            inImageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(new File(filePhotoPath))
                    .into(inImageView);
        }

//        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.create();

        inImageView.setOnClickListener(v -> alertDialog.dismiss());

        if (!mContext.isFinishing()) {
            alertDialog.show();
        }
    }

    private String getFilePath(final String data) {
        final String path = FileUtils.getFilesStoragePath(mContext);
        if (StringUtils.isEmpty(data)) {
            return Constants.Strings.EMPTY;
        }
        final String fileName = FileUtils.getFileName(data);
        return path + FileUtils.FOLDER_DIVIDER + fileName;
    }

    private void setChecker(ImageView checker, boolean checked) {
        if (isMulti) {
            checker.setImageResource(checked ? R.drawable.checkbox_checked_red : R.drawable.checkbox_unchecked_red);
        } else {
            checker.setImageResource(checked ? R.drawable.radio_button_checked_red : R.drawable.radio_button_unchecked_red);
        }
    }

    public interface OnViewElementClickListener {
        void onAnswerClick(int relativeId, Boolean enabled, String answer);
    }

    private void checkItem(int page, int position) {
        ElementItemR element = elementPages.get(page).get(position);
        AnswerState state = statePages.get(element.getRelative_id());
        if (state.isChecked() && !isMulti) {
            return;
        }
        if (element.getElementOptionsR().isAuto_check()) return;
        state.setChecked(!state.isChecked());
        if (!isMulti || element.getElementOptionsR().isUnchecker()) {
            for (Map.Entry<Integer, AnswerState> answerState : statePages.entrySet()) {
                if (!answerState.getKey().equals(element.getRelative_id())) {
                    answerState.getValue().setChecked(false);
                }
            }

//            for (int i = 0; i < mItems.size(); i++) {
//                if (i != position) {
//                    mItems.get(i).setChecked(false);
//                }
//            }
        }
        if (isMulti) {
            for (Map.Entry<Integer, AnswerState> answerState : statePages.entrySet()) {
                if (!answerState.getKey().equals(element.getRelative_id()) && element.getElementOptionsR().isUnchecker()) {
                    answerState.getValue().setChecked(false);
                }
            }

//            for (int i = 0; i < mItems.size(); i++) {
//                if (i != position && mItems.get(i).isUnChecker()) {
//                    mItems.get(i).setChecked(false);
//                }
//            }
        }
        notifyDataSetChanged();
    }

    public Map<Integer, AnswerState> getStatePages() {
        return statePages;
    }

    public void setStatePages(Map<Integer, AnswerState> statePages) {
        this.statePages = statePages;
        notifyDataSetChanged();
    }
}