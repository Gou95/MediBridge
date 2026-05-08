package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.R;

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stockits_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockitsResponse response = list.get(position);

        String name;

        if (response.getProductName() != null && !response.getProductName().isEmpty()) {
            name = response.getProductName();
        } else if (response.getUnlistedMedicines() != null && !response.getUnlistedMedicines().isEmpty()) {
            name = response.getUnlistedMedicines();
        } else {
            name = "N/A";
        }
        holder.mediName.setText(name);
        holder.unitNm.setText(response.getUnitName() + ":");
        holder.quantity.setText(response.getOrderQty());
        holder.orderNo.setText(response.getOrderNo());
        holder.delivery.setText(response.getDeliveryDay());
        holder.time.setText(response.getAddtime());
        holder.unlisted.setText(response.getUnlistedMedicines());
        holder.serial.setText(String.valueOf(position+1)+".");
        if ("UNLISTED MEDICINES".equals(response.getProductName())){
            holder.unlisted.setVisibility(View.VISIBLE);
            holder.layout.setVisibility(View.GONE);
        }else {
            holder.unlisted.setVisibility(View.GONE);
            holder.layout.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<StockitsResponse> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mediName,unitNm,quantity,orderNo,time,delivery,serial,unlisted;
        LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mediName = itemView.findViewById(R.id.txt_mediName);
            unitNm = itemView.findViewById(R.id.txt_uniName);
            quantity = itemView.findViewById(R.id.txt_unitNo);
            orderNo = itemView.findViewById(R.id.txt_orderNo);
            delivery = itemView.findViewById(R.id.txt_deliveryDay);
            time = itemView.findViewById(R.id.txt_dateTm);
            serial = itemView.findViewById(R.id.txt_serialNo);
            layout = itemView.findViewById(R.id.linear_layout);
            unlisted = itemView.findViewById(R.id.txt_unlisted);
        }
    }
}
