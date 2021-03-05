package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.state.SelectItem;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;

public class PageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private final List<ElementItemR> questions;
    private Map<Integer, List<AnswerState>> pageAnswersStates;
    //    private final String mType;
    private final MainActivity mActivity;

    public PageAdapter(final Context context, List<ElementItemR> questions, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
//        this.mType = type;
        this.questions = questions;
        this.onAnswerClickListener = onAnswerClickListener;
        this.pageAnswersStates = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            List<AnswerState> answersStates = new ArrayList<>();
            List<ElementItemR> answers = questions.get(i).getElements();
            for (int k = 0; k < questions.get(i).getElements().size(); k++) {
                answersStates.add(new AnswerState(answers.get(k).getRelative_id(), false, ""));
            }
            pageAnswersStates.put(questions.get(i).getRelative_id(), answersStates);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        Log.d("T-L.PageAdapter", "onCreateViewHolder TYPE: " + type);
        switch (type) {
            case 1:
                View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_select_avia, viewGroup, false);
                return new PageSelectViewHolder(view1, onAnswerClickListener);
            case 2:
                View view2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_imagepage_avia, viewGroup, false);
                return new PagerViewHolder(view2, onAnswerClickListener);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 1:
                PageSelectViewHolder pageSelectViewHolder = (PageSelectViewHolder) holder;
                pageSelectViewHolder.bind(questions.get(position), position);
                break;
            case 2:
                PagerViewHolder pagerViewHolder = (PagerViewHolder) holder;
                pagerViewHolder.bind(questions.get(position), position);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (questions.get(position).getSubtype()) {
            case ElementSubtype.SELECT:
                return 1;
            case ElementSubtype.LIST:
            case ElementSubtype.PAGEVIEW:
                return 2;
            default:
                return 0;
        }

    }

    public interface OnAnswerClickListener {
        void onAnswerClick(int relativeId, boolean enabled, String answer);
    }

    public Map<Integer, List<AnswerState>> getAnswers() {
        return pageAnswersStates;
    }

    public void setAnswers(Map<Integer, List<AnswerState>> pageAnswersStates) {
        this.pageAnswersStates = pageAnswersStates;
        Log.d("T-L.PageAdapter", "ADAPTER setAnswers: " + pageAnswersStates.size() + " elements");
        notifyDataSetChanged();
    }

    public Map<Integer, AnswerState> convertStatesListToMap(List<AnswerState> list) {
        Map<Integer, AnswerState> map = new HashMap<>();
        for (AnswerState state : list) {
            map.put(state.getRelative_id(), state);
        }
        return map;
    }

    public class PageSelectViewHolder extends RecyclerView.ViewHolder {

        TextView questionText;
        TextView answerText;
        TextView answerSelectText;
        View cont;

        OnAnswerClickListener onItemClickListener;

        public PageSelectViewHolder(@NonNull View itemView, OnAnswerClickListener onItemClickListener) {
            super(itemView);

            cont = itemView.findViewById(R.id.cont_select_holder);
            questionText = itemView.findViewById(R.id.select_answer_title);
            answerText = itemView.findViewById(R.id.selected_answers);
            answerSelectText = itemView.findViewById(R.id.answer_select_text);
            questionText.setTypeface(Fonts.getFuturaPtBook());
            answerText.setTypeface(Fonts.getFuturaPtBook());
            answerSelectText.setTypeface(Fonts.getFuturaPtBook());

            this.onItemClickListener = onItemClickListener;
        }

        public void bind(final ElementItemR item, int position) {
            questionText.setText(item.getElementOptionsR().getTitle());
            StringBuilder answerTextBuilder = null;
            List<AnswerState> answerStates = pageAnswersStates.get(item.getRelative_id());
            List<ElementItemR> answersList = item.getElements();

            for (int i = 0; i < answerStates.size(); i++) {
                if (answerStates.get(i).isChecked()) {
                    if (answerTextBuilder == null) {
                        answerTextBuilder = new StringBuilder();
                        answerText.setVisibility(View.VISIBLE);
                    } else {
                        answerTextBuilder.append("; ");
                    }
                    answerTextBuilder.append(answersList.get(i).getElementOptionsR().getTitle());
                }
            }
            if (answerTextBuilder != null) {
                answerText.setText(answerTextBuilder.toString());
            }

            cont.setOnClickListener(v -> showSelectDialog(item, answerStates));
        }

        @SuppressLint("RestrictedApi")
        public void showSelectDialog(final ElementItemR item, final List<AnswerState> answerStates) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
            View layoutView = mActivity.getLayoutInflater().inflate(R.layout.dialog_select, null);
            Button mOkBtn = layoutView.findViewById(R.id.select_ok_button);
            RecyclerView recyclerView = layoutView.findViewById(R.id.select_recyclerview);
            TextView questionTitle = layoutView.findViewById(R.id.select_title);
            questionTitle.setText(item.getElementOptionsR().getTitle());
            EditText selectInput = layoutView.findViewById(R.id.select_input);

            questionTitle.setTypeface(Fonts.getFuturaPtBook());
            selectInput.setTypeface(Fonts.getFuturaPtBook());
            mOkBtn.setTypeface(Fonts.getFuturaPtBook());

            List<ElementItemR> elementItems = item.getElements();
            List<SelectItem> selectItems = new ArrayList<>();
            for (int i = 0; i < elementItems.size(); i++) {
                selectItems.add(new SelectItem(elementItems.get(i).getElementOptionsR().getTitle(), answerStates.get(i).isChecked(), answerStates.get(i).isEnabled()));
            }
            SelectAdapter selectAdapter = new SelectAdapter(selectItems, item.getElementOptionsR().isPolyanswer(), (answers) -> {
                Log.d("T-L.PageAdapter", ">>>>>>>>>> ON SELECT: ");
                for (int i = 0; i < answerStates.size(); i++) {
                    boolean found = false;
                    for (SelectItem selectItem : answers) {
                        if (elementItems.get(i).getElementOptionsR().getTitle().equals(selectItem.getTitle())) {
                            answerStates.get(i).setChecked(selectItem.isChecked());
                            found = true;
                        }
                    }

                    if (!found && !item.getElementOptionsR().isPolyanswer())
                        answerStates.get(i).setChecked(false);

                }
                pageAnswersStates.put(item.getRelative_id(), answerStates);
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            recyclerView.setAdapter(selectAdapter);

            selectInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (StringUtils.isNotNullOrEmpty(s.toString())) {
                        filterItem(s.toString(), selectAdapter, selectItems);
                    } else {
                        selectAdapter.setAnswers(selectItems);
                    }
                }
            });

            dialogBuilder.setView(layoutView, 10, 40, 10, 10);
            AlertDialog selectDialog = dialogBuilder.create();
            selectDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            selectDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
            selectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            selectDialog.setCancelable(false);

            mOkBtn.setOnClickListener(v -> {
                mActivity.getMainDao().insertCrashLog(new CrashLogs(DateUtils.getCurrentTimeMillis(), selectAdapter.getLog(), true));
                selectDialog.dismiss();
                notifyDataSetChanged();
                onItemClickListener.onAnswerClick(item.getRelative_id(), true, null);
            });

            if (mActivity != null && !mActivity.isFinishing()) selectDialog.show();
        }

        public void filterItem(String text, SelectAdapter selectAdapter, List<SelectItem> selectItems) {
            List<SelectItem> newSelectItems = new ArrayList<>();
            for (SelectItem item : selectItems) {
                if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    newSelectItems.add(item);
                }
            }
            selectAdapter.setAnswers(newSelectItems);
        }

    }

    public class PagerViewHolder extends RecyclerView.ViewHolder {

        ViewPager viewPager;
        ViewPagerAdapter adapter;
        ImageButton btnPrev;
        ImageButton btnNext;
        Map<Integer, AnswerState> pagerAnswerStates;

        OnAnswerClickListener onItemClickListener;

        public PagerViewHolder(@NonNull View itemView, OnAnswerClickListener onItemClickListener) {
            super(itemView);

            viewPager = itemView.findViewById(R.id.viewpager);
            btnPrev = itemView.findViewById(R.id.btn_prev);
            btnNext = itemView.findViewById(R.id.btn_next);

            this.onItemClickListener = onItemClickListener;
        }

        public void bind(final ElementItemR item, int position) {

            pagerAnswerStates = convertStatesListToMap(pageAnswersStates.get(item.getRelative_id()));
            List<ElementItemR> answersList = item.getElements();

            adapter = new ViewPagerAdapter(mActivity, answersList, pagerAnswerStates, item.getElementOptionsR().isPolyanswer(), (relativeId, enabled, answer) -> {
                setStates();
                onAnswerClickListener.onAnswerClick(relativeId, false, null);
            });
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);

            if (adapter.getCount() == 1) {
                btnPrev.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.INVISIBLE);
            } else {
                btnPrev.setOnClickListener(v -> viewPager.setCurrentItem(getPrevPosition()));
                btnNext.setOnClickListener(v -> viewPager.setCurrentItem(getNextPosition()));
            }
        }

        public void setStates() {
            pagerAnswerStates = adapter.getStatePages();
        }

        public int getNextPosition() {
            return viewPager.getCurrentItem() == adapter.getCount() - 1 ? 0 : viewPager.getCurrentItem() + 1;
        }

        public int getPrevPosition() {
            return viewPager.getCurrentItem() == 0 ? adapter.getCount() - 1 : viewPager.getCurrentItem() - 1;
        }

    }


}
