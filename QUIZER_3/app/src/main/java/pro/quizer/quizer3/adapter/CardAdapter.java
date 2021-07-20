package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import androidx.cardview.widget.CardView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.CardItem;
import pro.quizer.quizer3.utils.UiUtils;

import static pro.quizer.quizer3.model.OptionsOpenType.NUMBER;

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
                String openType = Objects.requireNonNull(getItem(position)).getOpen();
                String data = Objects.requireNonNull(getItem(position)).getData();
                String hint = Objects.requireNonNull(getItem(position)).getHint();
                boolean open = !openType.equals("checkbox");

                CardView cont = holder.findViewById(R.id.cont_card);
                TextView textView = holder.findViewById(R.id.text1);
                TextView cardInput = holder.findViewById(R.id.card_input);
                ImageView checker = holder.findViewById(R.id.checker);

                if (data != null && data.length() > 0) {
                    cardInput.setTextColor(mContext.getResources().getColor(R.color.brand_color_dark));
//                    cardInput.setText(data);
                    UiUtils.setTextOrHide(cardInput, data);
                } else if (hint != null && hint.length() > 0) {
                    cardInput.setTextColor(mContext.getResources().getColor(R.color.gray));
                    cardInput.setText(hint);
                } else {
                    cardInput.setTextColor(mContext.getResources().getColor(R.color.gray));
                    cardInput.setText(R.string.enter_answer);
                }
                cardInput.setVisibility(open ? View.VISIBLE : View.GONE);

                if (isMulti) {
                    checker.setImageResource(checked ? R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
                } else {
                    checker.setImageResource(checked ? R.drawable.radio_button_checked : R.drawable.radio_button_unchecked);
                }
                checker.setVisibility(View.VISIBLE);

                UiUtils.setTextOrHide(textView, text);

                cont.setOnClickListener(v -> {
                    if ((open && !getItem(position).isChecked()) || (open && getItem(position).isAutoCkecker())) {
                        switch (getItem(position).getOpen()) {
                            case "text":
                            case "number":
                                showInputDialog(cardInput, position);
                                break;
                            case "date":
                                setDate(cardInput, position);
                                break;
                            case "time":
                                setTime(cardInput, position);
                                break;
                        }
                    } else {
                        checkItem(position);
                    }
                });
            }
        }

        return holder;
    }

    private void checkItem(int position) {
        if (mItems.get(position).isChecked() && !isMulti) {
            return;
        }
        if (mItems.get(position).isAutoCkecker()) return;
        mItems.get(position).setChecked(!mItems.get(position).isChecked());
        if (!isMulti || mItems.get(position).isUnChecker()) {
            for (int i = 0; i < mItems.size(); i++) {
                if (i != position) {
                    mItems.get(i).setChecked(false);
                }
            }
        }
        if (isMulti) {
            for (int i = 0; i < mItems.size(); i++) {
                if (i != position && mItems.get(i).isUnChecker()) {
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

    private void showInputDialog(final TextView pEditText, int position) {
        MainActivity mActivity = (MainActivity) mContext;
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mActivity);
        final View mView = layoutInflaterAndroid.inflate(mActivity.isAutoZoom() ? R.layout.dialog_input_answer_auto : R.layout.dialog_input_answer, null);
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        dialog.setView(mView);

        final EditText mEditText = mView.findViewById(R.id.input_answer);
        final View mNextBtn = mView.findViewById(R.id.view_ok);

        if (mItems.get(position).getOpen().equals(NUMBER)) {
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        String hint = mItems.get(position).getHint();
        String answer = mItems.get(position).getData();
        if (answer != null && answer.length() > 0) {
            mEditText.setText(mItems.get(position).getData());
        } else {
            if (hint != null && hint.length() > 0) {
                mEditText.setHint(hint);
            } else {
                mEditText.setHint("Введите ответ");
            }
        }

        mEditText.setFocusable(true);
        mEditText.requestFocus();
        mActivity.showKeyboard();

        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.create();

        mNextBtn.setOnClickListener(v -> {
            mItems.get(position).setData(mEditText.getText().toString());
            pEditText.setText(mEditText.getText().toString());
            checkItem(position);

            if (mActivity != null && !mActivity.isFinishing()) {
                mActivity.hideKeyboardFrom(mEditText);
                alertDialog.dismiss();
            }
        });

        if (mActivity != null && !mActivity.isFinishing()) {
            alertDialog.show();
        }
    }

    private Calendar mCalendar = Calendar.getInstance();

    public void setDate(final TextView pEditText, int position) {
        MainActivity mActivity = (MainActivity) mContext;
        if (!mActivity.isFinishing()) {
            new DatePickerDialog(mActivity, (view, year, monthOfYear, dayOfMonth) -> {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime(pEditText, true, position);
            },
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    public void setTime(final TextView pEditText, int position) {
        MainActivity mActivity = (MainActivity) mContext;
        if (!mActivity.isFinishing()) {
            new TimePickerDialog(mActivity, (view, hourOfDay, minute) -> {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                setInitialDateTime(pEditText, false, position);
            },
                    mCalendar.get(Calendar.HOUR_OF_DAY),
                    mCalendar.get(Calendar.MINUTE), true)
                    .show();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime(final TextView mTextView, final boolean pIsDate, int position) {
        SimpleDateFormat dateFormat;

        if (pIsDate) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("HH:mm");
        }

        dateFormat.setTimeZone(mCalendar.getTimeZone());
        mTextView.setText(dateFormat.format(mCalendar.getTime()));
        mItems.get(position).setData(dateFormat.format(mCalendar.getTime()));
        checkItem(position);
    }
}
