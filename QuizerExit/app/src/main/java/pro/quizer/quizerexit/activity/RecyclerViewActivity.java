package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.MultiMaxSelectionAdapter;
import pro.quizer.quizerexit.adapter.MultiSelectionAdapter;
import pro.quizer.quizerexit.adapter.SingleSelectionAdapter;
import pro.quizer.quizerexit.model.ItemModel;

public class RecyclerViewActivity extends AppCompatActivity {

    private static final String BUNDLE_MAX_ANSWERS = "bundle_max_count";
    private static final String BUNDLE_MIN_ANSWERS = "bundle_min_count";

    private static final int DEFAULT_MIN_ANSWERS = 1;

    private int mMaxAnswers;
    private int mMinAnswers;

    RecyclerView mRecyclerView;
    Button mSelected;
    SingleSelectionAdapter mAdapterSingle;
    MultiMaxSelectionAdapter mAdapterMultiMax;
    MultiSelectionAdapter mAdapterMulti;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mSelected = findViewById(R.id.selected);
        mSelected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                selectedClick();
            }
        });

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mMaxAnswers = bundle.getInt(BUNDLE_MAX_ANSWERS);
            mMinAnswers = bundle.getInt(BUNDLE_MIN_ANSWERS);
        }

        final List<ItemModel> list = getList();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (tag.equalsIgnoreCase("Single")) {
            mAdapterSingle = new SingleSelectionAdapter(list);
            mRecyclerView.setAdapter(mAdapterSingle);
        } else if (tag.equalsIgnoreCase("max")) {
            mAdapterMultiMax = new MultiMaxSelectionAdapter(this, list);
            mRecyclerView.setAdapter(mAdapterMultiMax);
        } else if (tag.equalsIgnoreCase("multiple")) {
            mAdapterMulti = new MultiSelectionAdapter(list);
            mRecyclerView.setAdapter(mAdapterMulti);
        }
    }

    private List<ItemModel> getList() {
        final List list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            final ItemModel model = new ItemModel();
            model.setName("Case " + i);
            model.setId(i);

            list.add(model);
        }

        return list;
    }

    public void selectedClick() {
        if (tag.equalsIgnoreCase("Single")) {
            if (mAdapterSingle.selectedPosition() != -1) {
                final ItemModel itemModel = mAdapterSingle.getSelectedItem();
                final String cityName = itemModel.getName();
                showToast("Selected City is: " + cityName);
            } else {
                showToast("Please select any city");
            }
        } else if (tag.equalsIgnoreCase("max")) {
            final List<ItemModel> list = mAdapterMultiMax.getSelectedItem();
            if (list.size() > 0) {
                final StringBuilder sb = new StringBuilder();
                for (int index = 0; index < list.size(); index++) {
                    final ItemModel model = list.get(index);
                    sb.append(model.getName() + "\n");
                }
                showToast(sb.toString());
            } else {
                showToast("Please select any city");
            }
        } else if (tag.equalsIgnoreCase("multiple")) {
            final List<ItemModel> list = mAdapterMulti.getSelectedItem();
            if (!list.isEmpty()) {
                final StringBuilder sb = new StringBuilder();
                for (int index = 0; index < list.size(); index++) {
                    final ItemModel model = list.get(index);
                    sb.append(model.getName() + "\n");
                }
                showToast(sb.toString());
            } else {
                showToast("Please select any city");
            }
        }
    }

    private void showToast(final CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}