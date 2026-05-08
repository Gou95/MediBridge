package com.indosoft.medibridge.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> {

    public interface OnItemClick {
        void onClick(MedicineListResponse.Datum item);
    }

    private final List<MedicineListResponse.Datum> list = new ArrayList<>();
    private final OnItemClick listener;

    public SearchAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void submitList(List<MedicineListResponse.Datum> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int p) {
        MedicineListResponse.Datum item = list.get(p);
        h.name.setText(item.getProductName());

        h.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return Math.min(list.size(), 100); // 🔥 ONLY SHOW FIRST 50
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, company;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.txt_product_name);

        }
    }
}
