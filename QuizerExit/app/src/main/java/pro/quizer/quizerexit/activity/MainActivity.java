package pro.quizer.quizerexit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pro.quizer.quizerexit.R;

public class MainActivity extends BaseActivity {

    public void onSingleSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra(RecyclerViewActivity.BUNDLE_MAX_ANSWERS, 1);
        intent.putExtra(RecyclerViewActivity.BUNDLE_MIN_ANSWERS, 1);
        startActivity(intent);
    }

    public void onMaxSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra(RecyclerViewActivity.BUNDLE_MAX_ANSWERS, 3);
        startActivity(intent);
    }

    public void onMinSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra(RecyclerViewActivity.BUNDLE_MIN_ANSWERS, 3);
        startActivity(intent);
    }

    public void onMinAndMaxSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra(RecyclerViewActivity.BUNDLE_MAX_ANSWERS, 4);
        intent.putExtra(RecyclerViewActivity.BUNDLE_MIN_ANSWERS, 2);
        startActivity(intent);
    }

    public void onMultiSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}