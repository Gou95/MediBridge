package com.indosoft.MediBridges.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.MediBridges.Model.OrderDetailsResponse;
import com.indosoft.MediBridges.R;

import java.util.ArrayList;

public class AllOrderDetailAdapter extends RecyclerView.Adapter<AllOrderDetailAdapter.ViewHolder> {

    Context context;
    ArrayList<OrderDetailsResponse> list;

    public AllOrderDetailAdapter(Context context, ArrayList<OrderDetailsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AllOrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_order_details,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllOrderDetailAdapter.ViewHolder holder, int position) {
        OrderDetailsResponse response = list.get(position);
        holder.productName.setText(response.getProductName());
        holder.stockitsNm.setText(response.getDealerName());
        holder.unitNM.setText(response.getUnitName()+":");
        holder.unitNUmber.setText(response.getOrderQty() );
        holder.deliveryDay.setText(response.getDeliveryDay());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName,stockitsNm,unitNM,unitNUmber,deliveryDay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.txt_MedicineNm);
            stockitsNm = itemView.findViewById(R.id.txt_stockitName);
            unitNM = itemView.findViewById(R.id.txt_unitName);
            unitNUmber = itemView.findViewById(R.id.txt_unitNumber);
            deliveryDay = itemView.findViewById(R.id.txt_deliDay);
        }
    }
}
