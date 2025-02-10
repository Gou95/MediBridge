package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.indosoft.medibridge.Activities.RecentStockistActivity;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class ViewStockistAdapter extends RecyclerView.Adapter<ViewStockistAdapter.ViewHolder> {
    Context context;
    ArrayList<RecentStockitsResponse> list;

    public ViewStockistAdapter(Context context, ArrayList<RecentStockitsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_stockist_name,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
RecentStockitsResponse response = list.get(position);
holder.dealername.setText(response.getDealerName());

holder.cardView.setOnClickListener(v -> {
    String dealerId = response.getDealerId();
    String dealerName = response.getDealerName();
    Intent intent = new Intent(context, RecentStockistActivity.class);
    intent.putExtra("dealerName", dealerName);
    intent.putExtra("dealerId", dealerId);
    context.startActivity(intent);
});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<RecentStockitsResponse> filterList) {
        this.list = filterList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dealername;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dealername = itemView.findViewById(R.id.txt_stockistName);
            cardView = itemView.findViewById(R.id.card_stockist);
        }
    }
}
