package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.view.ExtendedEditText;
import pro.quizer.quizerexit.view.resizeble.edittext.ResizableEditText;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    final Context mContext;
    final ResizableEditText mEditText;
    private Calendar mCalendar = Calendar.getInstance();

    AbstractViewHolder(@NonNull View itemView) {
        super(itemView);

        mEditText = itemView.findViewById(R.id.answer_edit_text);
        mContext = itemView.getContext();
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(mContext, d,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(mContext, t,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE), true)
                .show();
    }

    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime(final boolean pIsDate) {
        SimpleDateFormat dateFormat;

        if (pIsDate) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("HH:mm");
        }

        dateFormat.setTimeZone(mCalendar.getTimeZone());
        mEditText.setText(dateFormat.format(mCalendar.getTime()));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            setInitialDateTime(false);
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime(true);
        }
    };

    public abstract void onBind(final ElementModel pAnswer, final int pPosition);
}
