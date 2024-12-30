package com.indosoft.mediBridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.mediBridge.Model.GetUrgentCartResponse;
import com.indosoft.mediBridge.Model.UrgentCartResponse;
import com.indosoft.mediBridge.R;

import java.util.ArrayList;
import java.util.List;

public class UrgentCartAdapter extends RecyclerView.Adapter<UrgentCartAdapter.ViewHolder> {
    Context context;

    ArrayList<GetUrgentCartResponse> list;

    public UrgentCartAdapter(Context context, ArrayList<GetUrgentCartResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UrgentCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.urgent_cart_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UrgentCartAdapter.ViewHolder holder, int position) {
        GetUrgentCartResponse response = list.get(position);
         holder.medicineName.setText(response.getProductName());
         holder.stockitsName.setText(response.getDealerName());
         holder.unitName.setText(response.getUnitName());
         holder.quantity.setText(response.getQty());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView medicineName,stockitsName,unitName,quantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicineName = itemView.findViewById(R.id.txt_removeMedicineNm);
            stockitsName = itemView.findViewById(R.id.txt_removestockitName);
            unitName = itemView.findViewById(R.id.txt_removeUnit);
            quantity = itemView.findViewById(R.id.txt_removeNumber);
        }
    }
}
