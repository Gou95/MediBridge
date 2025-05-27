package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class StockistListAdapter extends RecyclerView.Adapter<StockistListAdapter.ViewHolder> {
    Context context;
    ArrayList<StockistListResponse> list;

    public StockistListAdapter(Context context, ArrayList<StockistListResponse> list) {
        this.context = context;
        this.list = (list != null) ? list : new ArrayList<>();
    }


    @NonNull
    @Override
    public StockistListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stockist_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockistListAdapter.ViewHolder holder, int position) {
        StockistListResponse response = list.get(position);
        holder.name.setText(response.getDealerName());
        holder.mobile.setText(response.getDealerPhone());
        holder.gst.setText(response.getDealerGst());
        holder.address.setText(response.getDealerAddress());
        holder.serial.setText(String.valueOf(position)+":");
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,mobile,gst,address,serial;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_stockistNm);
            mobile = itemView.findViewById(R.id.txt_stockitMob);
            gst = itemView.findViewById(R.id.txt_stockistGstNo);
            address = itemView.findViewById(R.id.txt_stockistAdd);
            serial = itemView.findViewById(R.id.txt_stockistSerialNo);
        }
    }
}
