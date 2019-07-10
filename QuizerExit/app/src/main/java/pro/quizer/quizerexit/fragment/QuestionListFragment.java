package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.AbstractQuestionAdapter;
import pro.quizer.quizerexit.adapter.QuestionListAdapter;
import pro.quizer.quizerexit.model.ElementSubtype;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class QuestionListFragment extends AbstractQuestionFragment {


    public static Fragment newInstance(final boolean pIsFromDialog, final boolean pIsVisibleButton, final UserModel user, @NonNull final ElementModel pElement, final NavigationCallback pCallback, final HashMap<Integer, ElementModel> pMap) {
        final Fragment fragment = new QuestionListFragment();

        fragment.setArguments(getBundle(pIsFromDialog, pIsVisibleButton, pElement, pCallback));

        return fragment;
    }

    private AbstractQuestionAdapter mAdapter;
    RecyclerView mRecyclerView;
    EditText mSearchEditText;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View pView, @Nullable final Bundle savedInstanceState) {
        mRecyclerView = pView.findViewById(R.id.question_recycler_view);
        mSearchEditText = pView.findViewById(R.id.search_edittext_alert);

        super.onViewCreated(pView, savedInstanceState);

        handleButtonsVisibility();

        final ElementModel elementModel = getElementModel();

        if (mSearchEditText != null) {
            mSearchEditText.setVisibility(elementModel.getOptions().isSearch() ? View.VISIBLE :View.GONE);
            mSearchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mAdapter != null) {
                        mAdapter.sortByString(s.toString());
                    }
                }
            });

            mSearchEditText.setText("");
        }
    }

    @Override
    protected boolean isFromDialog() {
        return mIsFromDialog;
    }

    @Override
    protected boolean isButtonVisible() {
        return getArguments().getBoolean(BUNDLE_IS_BUTTON_VISIBLE);
    }

    @Override
    IAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    void createAdapter(final HashMap<Integer, ElementModel> pMap, final ElementModel pCurrentElement, List<ElementModel> subElements, int minAnswers, int maxAnswers, Runnable refreshRecyclerViewRunnable) {
        mAdapter = new QuestionListAdapter(pMap, pCurrentElement, (BaseActivity) getContext(), subElements, maxAnswers, minAnswers, mUser);

        if (ElementSubtype.SCALE.equals(pCurrentElement.getSubtype())) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), pCurrentElement.getElements().size()));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mRecyclerView.setLayoutParams(layoutParams);
        } else if (pCurrentElement.getOptions().isMedia()) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }

        mRecyclerView.setItemViewCacheSize(0);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    void updateAdapter() {
        mAdapter.setIsUpdateActionPerformed(true);
//        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
        UiUtils.hideKeyboard(getContext(), getView());
    }
}