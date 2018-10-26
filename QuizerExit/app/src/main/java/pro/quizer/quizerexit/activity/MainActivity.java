package pro.quizer.quizerexit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pro.quizer.quizerexit.R;

public class MainActivity extends BaseActivity {

    public void onSingleSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra("TAG", "single");
        startActivity(intent);
    }

    public void onMaxSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra("TAG", "single");
        startActivity(intent);
    }

    public void onMinSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra("TAG", "single");
        startActivity(intent);
    }

    public void onMinAndMaxSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra("TAG", "single");
        startActivity(intent);
    }

    public void onMultiSelectionClick(final View view) {
        final Intent intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra("TAG", "single");
        startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}