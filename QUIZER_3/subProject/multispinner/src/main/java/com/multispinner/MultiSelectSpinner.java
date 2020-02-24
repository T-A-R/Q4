package com.multispinner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by DELL-PC on 29-06-2016.
 */
public class MultiSelectSpinner extends Spinner implements DialogInterface.OnMultiChoiceClickListener {
    public interface OnMultipleItemsSelectedListener {
        void selectedIndices(List<Integer> indices);

        void selectedStrings(List<String> strings);
    }

    private OnMultipleItemsSelectedListener listener;

    String[] _items = null;
    boolean[] mSelection = null;
    boolean[] mEnabled = null;
    boolean[] mSelectionAtStart = null;
    String _itemsAtStart = null;
    Context c;
    ArrayAdapter<String> simple_adapter;
    private boolean hasNone = false;
    private int uncheckerIndex = -101;
    private TextView item;

    public MultiSelectSpinner(Context context) {
        super(context);
        Log.d("TARLOGS", "ADAPTER 1");
        c = context;
        simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {
            @Override
            public boolean isEnabled(int position) {
                if (!mEnabled[position]) {
                    Log.d("TARLOGS", "ADAPTER 1 isEnabled: false " + position);
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    Log.d("TARLOGS", "ADAPTER 1 isEnabled: true " + position);
                    return true;
                }
            }
        };
        simple_adapter.setDropDownViewResource(R.layout.spinner_selector);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
//        Log.d("TARLOGS", "ADAPTER 2");
        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item) {
            @Override
            public boolean isEnabled(int position) {
//                if (!mEnabled[position]) {
                if (!mEnabled[position]) {
//                    Log.d("TARLOGS", "ADAPTER 2 isEnabled: false " + position);
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    Log.d("TARLOGS", "ADAPTER 2 isEnabled: true " + position);
                    return true;
                }
            }
        };
        simple_adapter.setDropDownViewResource(R.layout.spinner_selector);
        item = (TextView) findViewById(R.id.spinnerText);
        super.setAdapter(simple_adapter);
    }

    public void setListener(OnMultipleItemsSelectedListener listener) {
        this.listener = listener;
    }

    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
        if (mSelection != null && position < mSelection.length) {
            if (hasNone) {
                Log.d("TARLOGS", "??? onClick ENABLED: " + mEnabled[position]);
                if (position == uncheckerIndex && isChecked && mSelection.length > 1) {
                    for (int i = 0; i < mSelection.length; i++) {
                        if (i != uncheckerIndex)
                            mSelection[i] = false;
                        mEnabled[i] = false;
                        ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                    }
                } else if (position == uncheckerIndex && !isChecked && mSelection.length > 1) {
                    for (int i = 0; i < mSelection.length; i++) {
                        mEnabled[i] = true;
                    }
//                    simple_adapter.notifyDataSetChanged();
                } else if (position != uncheckerIndex && mSelection[uncheckerIndex] && isChecked) {
//                    mSelection[uncheckerIndex] = false;
//                    ((AlertDialog) dialog).getListView().setItemChecked(uncheckerIndex, false);
//                    ((AlertDialog) dialog).getListView().setItemChecked(position, false);
                    for (int i = 0; i < mSelection.length; i++) {
                        if (i != uncheckerIndex) {
                            mSelection[i] = false;
                            ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                        }
                    }
                }
            }
            if (mEnabled[position])
                mSelection[position] = isChecked;
//            simple_adapter.notifyDataSetChanged();
//            super.setAdapter(simple_adapter);
            simple_adapter.clear();
            simple_adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
        for (int i = 0; i < mEnabled.length; i++) {
            Log.d("TARLOGS", "ENALED: (" + i + ") " + mEnabled[i]);
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Выберите:");
        builder.setMultiChoiceItems(_items, mSelection, this);
        _itemsAtStart = getSelectedItemsAsString();
//        builder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setSelection(0);
//            }
//        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection.length);
                listener.selectedIndices(getSelectedIndices());
                listener.selectedStrings(getSelectedStrings());
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simple_adapter.clear();
                simple_adapter.add(_itemsAtStart);
                System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart.length);
            }
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
//        super.setAdapter(adapter);
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setAdapter(Context context) {

        c = context;
        simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {
            @Override
            public boolean isEnabled(int position) {
                if (!mEnabled[position]) {
                    Log.d("TARLOGS", "ADAPTER 1 isEnabled: false " + position);
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    Log.d("TARLOGS", "ADAPTER 1 isEnabled: true " + position);
                    return true;
                }
            }
        };
        super.setAdapter(simple_adapter);
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        mEnabled = new boolean[_items.length];
        mSelectionAtStart = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        Arrays.fill(mEnabled, true);
        mSelection[0] = true;
        mSelectionAtStart[0] = true;
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        mEnabled = new boolean[_items.length];
        mSelectionAtStart = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        Arrays.fill(mEnabled, true);
        mSelection[0] = true;
    }

    public void setSelection(String[] selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
//            mEnabled[i] = true;
            mSelectionAtStart[i] = false;
        }
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                    mSelectionAtStart[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                    mSelectionAtStart[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }


    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
            mSelectionAtStart[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndices) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (int index : selectedIndices) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
                mSelectionAtStart[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndices() {
        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    public void hasNoneOption(boolean val, int uncheckerIndex) {
        hasNone = val;
        this.uncheckerIndex = uncheckerIndex;
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {


        return super.performItemClick(view, position, id);
    }
}
