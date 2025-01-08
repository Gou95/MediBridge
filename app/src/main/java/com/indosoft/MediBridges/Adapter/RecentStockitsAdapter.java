package com.indosoft.MediBridges.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.MediBridges.Activities.CartActivity;
import com.indosoft.MediBridges.Activities.SeeAllDetailsActivity;
import com.indosoft.MediBridges.Model.RecentStockitsResponse;
import com.indosoft.MediBridges.R;

import java.util.ArrayList;

public class RecentStockitsAdapter extends RecyclerView.Adapter<RecentStockitsAdapter.ViewHolder> {

    Context context;
    ArrayList<RecentStockitsResponse> list;

    public RecentStockitsAdapter(Context context, ArrayList<RecentStockitsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecentStockitsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_stockits_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentStockitsAdapter.ViewHolder holder, int position) {

        RecentStockitsResponse response = list.get(position);
        String dealerName = response.getDealerName();

        if (dealerName != null && dealerName.length() > 20) {

            String[] nameParts = dealerName.split(" ", 2);
            if (nameParts.length > 1) {
                holder.dealerName.setText(nameParts[0] + "\n" + nameParts[1]);
            } else {
                holder.dealerName.setText(dealerName);
            }
        } else {
            // If the name is short, display it on one line
            holder.dealerName.setText(dealerName);
        }

        holder.stockitsList.setOnClickListener(v -> {
            String name = response.getDealerName();
            String dealerId = response.getDealerId();
            Intent intent = new Intent(context, CartActivity.class);
            intent.putExtra("dealerName", name);
            intent.putExtra("dealerId", dealerId);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dealerName;
        LinearLayout stockitsList;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dealerName = itemView.findViewById(R.id.txt_recentStockits);
            stockitsList = itemView.findViewById(R.id.linear_stockits);
        }
    }
}
