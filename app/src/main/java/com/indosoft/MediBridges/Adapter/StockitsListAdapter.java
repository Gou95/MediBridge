package com.indosoft.MediBridges.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.MediBridges.Model.StockitsResponse;
import com.indosoft.MediBridges.R;

import java.util.ArrayList;

public class StockitsListAdapter extends RecyclerView.Adapter<StockitsListAdapter.ViewHolder> {

    Context context;
    ArrayList<StockitsResponse> list;

    public StockitsListAdapter(Context context, ArrayList<StockitsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StockitsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stockits_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockitsListAdapter.ViewHolder holder, int position) {
        StockitsResponse response = list.get(position);
        holder.mediName.setText(response.getProductName());
        holder.unitNm.setText(response.getUnitName());
        holder.quantity.setText(response.getOrderQty());
        holder.orderNo.setText(response.getOrderNo());
        holder.delivery.setText(response.getDeliveryDay());
        holder.time.setText(response.getAddtime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mediName,unitNm,quantity,orderNo,time,delivery;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mediName = itemView.findViewById(R.id.txt_mediName);
            unitNm = itemView.findViewById(R.id.txt_uniName);
            quantity = itemView.findViewById(R.id.txt_unitNo);
            orderNo = itemView.findViewById(R.id.txt_orderNo);
            delivery = itemView.findViewById(R.id.txt_deliveryDay);
            time = itemView.findViewById(R.id.txt_dateTm);
        }
    }
}
