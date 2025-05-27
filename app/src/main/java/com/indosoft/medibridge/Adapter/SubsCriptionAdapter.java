package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Activities.SubscribeActivity;
import com.indosoft.medibridge.Model.PlansResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class SubsCriptionAdapter extends RecyclerView.Adapter<SubsCriptionAdapter.ViewHolder> {

    Context context;
    ArrayList<PlansResponse>list;

    public SubsCriptionAdapter(Context context, ArrayList<PlansResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubsCriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.plans_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubsCriptionAdapter.ViewHolder holder, int position) {
        PlansResponse response = list.get(position);
        holder.name.setText(response.getSubscriptionName());
        holder.charges.setText("\u20B9"+response.getSubscriptionCharges());


        holder.cardView.setOnClickListener(v -> {
            int amount = Integer.parseInt(response.getSubscriptionCharges());

            if (amount == 0) {
              //  ((SubscribeActivity) context).activateFreePlan();
                Toast.makeText(context, "Trail Period Activated!", Toast.LENGTH_SHORT).show();
            } else {
                // Open payment gateway for paid plans
                Intent intent = new Intent(context, SubscribeActivity.class);
                intent.putExtra("amount", amount);
                intent.putExtra("planId", response.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<PlansResponse> updatedList) {
        this.list = new ArrayList<>(updatedList);
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,charges;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_subsName);
            charges = itemView.findViewById(R.id.txt_subsCharges);
            cardView = itemView.findViewById(R.id.cardView_plans);
        }
    }
}
