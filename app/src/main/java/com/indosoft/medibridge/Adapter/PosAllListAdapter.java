package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Model.PosAllListResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class PosAllListAdapter extends RecyclerView.Adapter<PosAllListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PosAllListResponse> list;

    public PosAllListAdapter(Context context, ArrayList<PosAllListResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PosAllListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_by_details,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosAllListAdapter.ViewHolder holder, int position) {
        PosAllListResponse response = list.get(position);

        if ("unlisted".equals(response.getSource())){
            holder.product.setText(response.getProductName());
        }else {
            holder.product.setText(response.getProductName());
        }

        holder.unitName.setText(response.getUnitName());
        holder.qty.setText(response.getQty());
        holder.amount.setText(response.getAmount());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product,unitName,qty,amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.txt_dateByProductName);
            unitName = itemView.findViewById(R.id.txt_dateByPosUnitNM);
            qty = itemView.findViewById(R.id.txt_dateByPosQty);
            amount = itemView.findViewById(R.id.txt_dateByPosAmount);
           // linearLayout = itemView.findViewById(R.id.pos_linear);
        }
    }
}
