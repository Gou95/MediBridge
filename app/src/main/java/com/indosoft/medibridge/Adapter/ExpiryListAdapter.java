package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Model.ExpiryListResponse;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class ExpiryListAdapter extends RecyclerView.Adapter<ExpiryListAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderDetailsResponse> list;

    public ExpiryListAdapter(Context context, ArrayList<OrderDetailsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ExpiryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expiry_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpiryListAdapter.ViewHolder holder, int position) {
        OrderDetailsResponse response = list.get(position);
        holder.product.setText(response.getProductName());
        holder.batch.setText(response.getBatchNo());
        holder.dealer.setText(response.getDealerName());
        holder.stock.setText(response.getOrderQty());
        holder.time.setText(response.getExpiryMonth());
        holder.serial.setText(String.valueOf(position +1)+":");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<OrderDetailsResponse> filterList) {
        this.list = filterList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product,batch,dealer,stock,time,serial;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.txt_medi);
            batch = itemView.findViewById(R.id.txt_batchNo);
            dealer = itemView.findViewById(R.id.txt_stockistN);
            stock = itemView.findViewById(R.id.txt_stock);
            time = itemView.findViewById(R.id.txt_date);
            serial = itemView.findViewById(R.id.txt_serialNo);
        }
    }
}
