package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Model.RecievedOrderResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class ProductRecivedAdapter extends RecyclerView.Adapter<ProductRecivedAdapter.ViewHolder> {
    Context context;
    ArrayList<RecievedOrderResponse> list;

    public ProductRecivedAdapter(Context context, ArrayList<RecievedOrderResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductRecivedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recieved_product_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecivedAdapter.ViewHolder holder, int position) {
        RecievedOrderResponse response = list.get(position);
        holder.medicine.setText(response.getProductName());
        holder.unit.setText(response.getUnitName());
        holder.qty.setText(response.getOrderQty());
        holder.stockist.setText(response.getDealerName());
        holder.delivery.setText(response.getDeliveryDay());
        holder.status.setText(response.getOrderStatus());
        holder.datetime.setText(response.getAddtime());
        holder.serial.setText(String.valueOf(position+1)+".");
        holder.unlisted.setText(response.getUnlistedMedicines());

        if ("UNLISTED MEDICINES".equals(response.getProductName())){
            holder.unlisted.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
        }else {
            holder.unlisted.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.VISIBLE);
        }

    }
    public void updateList(ArrayList<RecievedOrderResponse> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial,medicine,datetime,unit,qty,unlisted,stockist,delivery,status;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.txt_recievedSerial);
            medicine = itemView.findViewById(R.id.txt_recievedMedicineNm);
            datetime = itemView.findViewById(R.id.txt_recievedDot);
            unit = itemView.findViewById(R.id.txt_recievedUnitName);
            qty = itemView.findViewById(R.id.txt_recievedUnitNumber);
            unlisted = itemView.findViewById(R.id.txt_recievedUnlisted);
            stockist = itemView.findViewById(R.id.txt_recievedStockitName);
            delivery = itemView.findViewById(R.id.txt_recievedDeliDay);
            status = itemView.findViewById(R.id.txt_recievedStatus);
            linearLayout = itemView.findViewById(R.id.linear_recievedLayout);

        }
    }
}
