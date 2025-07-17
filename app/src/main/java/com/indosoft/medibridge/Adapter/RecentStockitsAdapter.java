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


import com.indosoft.medibridge.Activities.RecentStockistActivity;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentStockitsAdapter extends RecyclerView.Adapter<RecentStockitsAdapter.ViewHolder> {

    Context context;
    ArrayList<RecentStockitsResponse> list;

    public RecentStockitsAdapter(Context context, ArrayList<RecentStockitsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_stockits_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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
            Intent intent = new Intent(context, RecentStockistActivity.class);
            intent.putExtra("dealerName", name);
            intent.putExtra("dealerId", dealerId);
            context.startActivity(intent);
        });

        int nightModeFlags = context.getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            holder.userImage.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.userImage.setColorFilter(android.graphics.Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
        }


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dealerName;
        CircleImageView userImage;
        LinearLayout stockitsList;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dealerName = itemView.findViewById(R.id.txt_recentStockits);
            stockitsList = itemView.findViewById(R.id.linear_stockits);
            userImage = itemView.findViewById(R.id.img_image);

        }
    }
}
