package com.toptoche.searchablespinnerlibrary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchableListDialog extends DialogFragment implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String ITEMS = "items";
    private ArrayAdapter listAdapter;
    private ListView _listViewItems;
    private SearchableItem _searchableItem;
    private OnSearchTextChanged _onSearchTextChanged;
    private SearchView _searchView;
    private String _strTitle;
    private String _strPositiveButtonText;
    private static List<Boolean> enabledList;
    public List<Boolean> originalEnabled;
    private DialogInterface.OnClickListener _onClickListener;
    private List<String> itemsList;

    public SearchableListDialog() {

    }

    public static SearchableListDialog newInstance(List items, List enabled) {
        SearchableListDialog multiSelectExpandableFragment = new
                SearchableListDialog();
        Bundle args = new Bundle();
        args.putSerializable(ITEMS, (Serializable) items);
        try {
            enabledList = (List<Boolean>) enabled;
        } catch (Exception e) {
            e.printStackTrace();
        }

        multiSelectExpandableFragment.setArguments(args);

        return multiSelectExpandableFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        originalEnabled = new ArrayList<>(enabledList);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        enabledList = originalEnabled;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Getting the layout inflater to inflate the view in an alert dialog.
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        // Crash on orientation change #7
        // Change Start
        // Description: As the instance was re initializing to null on rotating the device,
        // getting the instance from the saved instance
        if (null != savedInstanceState) {
            _searchableItem = (SearchableItem) savedInstanceState.getSerializable("item");
        }
        // Change End

        View rootView = inflater.inflate(R.layout.searchable_list_dialog, null);
        setData(rootView);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(rootView);

        String strPositiveButton = _strPositiveButtonText == null ? "CLOSE" : _strPositiveButtonText;
        alertDialog.setPositiveButton(strPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enabledList = originalEnabled;
            }
        });

        String strTitle = _strTitle == null ? "Select Item" : _strTitle;
        alertDialog.setTitle(strTitle);

        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);
        return dialog;
    }

    // Crash on orientation change #7
    // Change Start
    // Description: Saving the instance of searchable item instance.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("item", _searchableItem);
        super.onSaveInstanceState(outState);
    }
    // Change End

    public void setTitle(String strTitle) {
        _strTitle = strTitle;
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _strPositiveButtonText = strPositiveButtonText;
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        _strPositiveButtonText = strPositiveButtonText;
        _onClickListener = onClickListener;
    }

    public void setOnSearchableItemClickListener(SearchableItem searchableItem) {
        this._searchableItem = searchableItem;
    }

    public void setOnSearchTextChangedListener(OnSearchTextChanged onSearchTextChanged) {
        this._onSearchTextChanged = onSearchTextChanged;
    }

    private void setData(View rootView) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context
                .SEARCH_SERVICE);

        _searchView = (SearchView) rootView.findViewById(R.id.search);
        _searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName
                ()));
        _searchView.setIconifiedByDefault(false);
        _searchView.setOnQueryTextListener(this);
        _searchView.setOnCloseListener(this);
        _searchView.clearFocus();
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(_searchView.getWindowToken(), 0);


        List items = (List) getArguments().getSerializable(ITEMS);
        itemsList = new ArrayList<String>(items);

        _listViewItems = (ListView) rootView.findViewById(R.id.listItems);

        listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, itemsList) {

            private ArrayList<String> originalList = new ArrayList<>(itemsList);

            private ArrayList<String> titlesList = new ArrayList<>(itemsList);
            private ItemFilter filter;

            @Override
            public Filter getFilter() {
                if (filter == null) {
                    filter = new ItemFilter();
                }
                return filter;
            }

            @Override
            public boolean isEnabled(int position) {
                if (enabledList != null && position < enabledList.size()) {
                    return enabledList.get(position);
                }
                return true;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if (!isEnabled(position)) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }


            class ItemFilter extends Filter {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {


                    FilterResults result = new FilterResults();
                    if (constraint != null && constraint.toString().length() > 0) {
                        constraint = constraint.toString().toLowerCase();
                        ArrayList<String> filteredItems = new ArrayList<>();
                        ArrayList<Boolean> filteredEnabled = new ArrayList<>();

                        for (int i = 0, l = originalList.size(); i < l; i++) {
                            String element = originalList.get(i);
                            if (element.toLowerCase().contains(constraint)) {
                                filteredItems.add(element);
                                filteredEnabled.add(originalEnabled.get(i));
                            }
                        }
                        enabledList = filteredEnabled;
                        result.count = filteredItems.size();
                        result.values = filteredItems;
                    } else {
                        synchronized (this) {
                            enabledList = originalEnabled;
                            result.values = originalList;
                            result.count = originalList.size();
                        }
                    }
                    return result;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {

                    itemsList = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                    clear();
                    for (int i = 0, l = itemsList.size(); i < l; i++)
                        add(itemsList.get(i));
                    notifyDataSetInvalidated();
//                    enabledList = originalEnabled;
                }
            }
        };

        _listViewItems.setAdapter(listAdapter);

        _listViewItems.setTextFilterEnabled(true);

        _listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _searchableItem.onSearchableItemClicked(listAdapter.getItem(position), position);
                enabledList = originalEnabled;
                getDialog().dismiss();
            }
        });
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        _searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            ((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(null);
        } else {
            ((ArrayAdapter) _listViewItems.getAdapter()).getFilter().filter(s);
        }
        if (null != _onSearchTextChanged) {
            _onSearchTextChanged.onSearchTextChanged(s);
        }
        return true;
    }

    public interface SearchableItem<T> extends Serializable {
        void onSearchableItemClicked(T item, int position);
    }

    public interface OnSearchTextChanged {
        void onSearchTextChanged(String strText);
    }
}
