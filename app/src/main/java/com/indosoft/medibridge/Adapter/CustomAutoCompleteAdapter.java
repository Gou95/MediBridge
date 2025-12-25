package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomAutoCompleteAdapter extends ArrayAdapter<String> {

    private final List<String> items;
    private final List<String> filteredItems;
    private final Context context;

    public CustomAutoCompleteAdapter(Context context, int resource, List<String> items) {
        super(context, resource, new ArrayList<>(items));
        this.context = context;
        this.items = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return filteredItems.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                filteredItems.clear();

                if (constraint != null) {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String item : items) {
                        if (item.toLowerCase().contains(filterPattern)) {
                            filteredItems.add(item);
                        }
                    }
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return (String) resultValue;
            }
        };
    }
}
