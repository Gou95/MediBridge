package com.indosoft.mediBridge.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.ShowCartResponse;
import com.indosoft.mediBridge.R;

import java.util.ArrayList;
import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    Context context;

    ArrayList<ShowCartResponse> list;

    public CardListAdapter(Context context, ArrayList<ShowCartResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CardListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardListAdapter.ViewHolder holder, int position) {
        ShowCartResponse output = list.get(position);

        holder.medicinename.setText(output.getProductName());
        holder.dealerName.setText(output.getDealerName());
        holder.unit.setText(output.getUnit());
        holder.number.setText(output.getQty());

        Log.d("CardListAdapter", "Binding Data - Medicine: " + output.getProductName() +
                ", Dealer: " + output.getDealerName() +
                ", Unit: " + output.getUnit() +
                ", Qty: " + output.getQty());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView medicinename,dealerName,unit,number;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicinename = itemView.findViewById(R.id.txt_medicine);
            dealerName = itemView.findViewById(R.id.txt_stockitName);
            unit = itemView.findViewById(R.id.txt_unit);
            number = itemView.findViewById(R.id.txt_number);
        }
    }



}
