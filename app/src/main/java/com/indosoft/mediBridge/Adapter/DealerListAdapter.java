package com.indosoft.mediBridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.indosoft.mediBridge.R;

import java.util.List;

public class DealerListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> productNameList;

    public DealerListAdapter(@NonNull Context context, int resource, List<String> productNameList) {
        super(context, resource, productNameList);
        this.context = context;
        this.productNameList = productNameList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the layout for the dropdown item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dropdown_items, parent, false);
        }

        // Get the current product name
        String productName = productNameList.get(position);

        // Find the TextView by its ID
      //  TextView itemTextView = convertView.findViewById(R.id.itemTextView);

        // Set the product name in the TextView
     //   itemTextView.setText(productName);

        return convertView;
    }
}
