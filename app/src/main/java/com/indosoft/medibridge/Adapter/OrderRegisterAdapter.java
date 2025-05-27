package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Activities.ExpireActivity;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class OrderRegisterAdapter extends RecyclerView.Adapter<OrderRegisterAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderRegisterResponse> list;

    public OrderRegisterAdapter(Context context, ArrayList<OrderRegisterResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_register,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderRegisterResponse response = list.get(position);
        holder.medicine.setText(response.getProductName());
        holder.unitName.setText(response.getUnitName());
        holder.unitQty.setText(response.getOrderQty());
        holder.stockist.setText(response.getDealerName());
        holder.delivary.setText(response.getDeliveryDay());
        holder.status.setText(response.getOrderStatus());
        holder.time.setText(response.getAddtime());
        holder.serial.setText(String.valueOf(position+1)+".");
        holder.unlisted.setText(response.getUnlistedMedicines());

        if ("UNLISTED MEDICINES".equals(response.getProductName())){
            holder.unlisted.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
        }else {
            holder.unlisted.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.VISIBLE);
        }

        int textColor;
        switch (response.getOrderStatus()) {
            case "Pending":
                textColor = context.getResources().getColor(R.color.yellow);
                break;
            case "Received":
                textColor = context.getResources().getColor(R.color.green);
                break;
            case "Not Received":
                textColor = context.getResources().getColor(R.color.red);
                break;
            default:
                textColor = context.getResources().getColor(R.color.grey);
                break;
        }
        holder.status.setTextColor(textColor);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<OrderRegisterResponse> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicine,unitName,unitQty,stockist,delivary,status ,serial,time,unlisted,expire;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicine = itemView.findViewById(R.id.txt_orderMedicineNm);
            unitName = itemView.findViewById(R.id.txt_orderUnitName);
            unitQty = itemView.findViewById(R.id.txt_orderUnitNumber);
            stockist = itemView.findViewById(R.id.txt_orderStockitName);
            delivary = itemView.findViewById(R.id.txt_orderDeliDay);
            status = itemView.findViewById(R.id.txt_orderStt);
            serial = itemView.findViewById(R.id.txt_orderSerial);
            time = itemView.findViewById(R.id.txt_orderDot);
            linearLayout = itemView.findViewById(R.id.linear_orderLayout);
            unlisted = itemView.findViewById(R.id.txt_orderUnlisted);
           // expire = itemView.findViewById(R.id.txt_expire);
        }
    }
}
