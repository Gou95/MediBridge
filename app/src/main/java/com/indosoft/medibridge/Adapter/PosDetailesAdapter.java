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

import com.indosoft.medibridge.Activities.PosAllListActivity;
import com.indosoft.medibridge.Model.PosDetailsResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;
import java.util.List;

public class PosDetailesAdapter extends RecyclerView.Adapter<PosDetailesAdapter.ViewHolder> {

    Context context;
    ArrayList<PosDetailsResponse> list;

    public PosDetailesAdapter(Context context, ArrayList<PosDetailsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PosDetailesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pos_details,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosDetailesAdapter.ViewHolder holder, int position) {
        PosDetailsResponse response = list.get(position);
        holder.date.setText(response.getAddDate());
        holder.soleNo.setText(response.getQty());
        holder.amount.setText(response.getAmount());
        String addtime = response.getAddDate();

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PosAllListActivity.class);
            intent.putExtra("addtime",addtime);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<PosDetailsResponse> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,soleNo,amount;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.txt_posDot);
            soleNo = itemView.findViewById(R.id.txt_posNum);
            amount = itemView.findViewById(R.id.txt_posAmt);
            cardView = itemView.findViewById(R.id.pos_cardview);
        }
    }
}
