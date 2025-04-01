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


import com.indosoft.medibridge.Activities.SeeAllOrderDetailsActivity;
import com.indosoft.medibridge.Model.OrderListResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private Context context;
    ArrayList<OrderListResponse> orderList ;


    public OrderListAdapter(Context context, ArrayList<OrderListResponse> orderList) {
        this.context = context;
        this.orderList = orderList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.order_list_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderListResponse response = orderList.get(position);
        holder.order_status.setText(response.getOrderStatus());
       // holder.address.setText("#"+response.geta());
        holder.dateTime.setText(response.getAddtime());
        holder.items.setText(response.getTotalmeds());
        holder.dealer.setText(response.getDealerName());



        switch (response.getOrderStatus()) {
            case "Pending":
                holder.background.setCardBackgroundColor(context.getResources().getColor(R.color.yellow));
                break;
            case "Approved":
                holder.background.setCardBackgroundColor(context.getResources().getColor(R.color.orange));
                break;
            case "Delivered":
                holder.background.setCardBackgroundColor(context.getResources().getColor(R.color.green));
                break;
            case "Rejected":
                holder.background.setCardBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            default:
                holder.background.setCardBackgroundColor(context.getResources().getColor(R.color.grey));
                break;
        }

        holder.seeDetails.setOnClickListener(v -> {
            String retailerId = response.getRetailerId();
            String dealerId = response.getDealerId();
            String orderNo = response.getOrderNo();
            String dot = response.getAddtime();
            String status =response.getOrderStatus();
            String name =response.getDealerName();

           Intent intent = new Intent(context, SeeAllOrderDetailsActivity.class);
            intent.putExtra("retailerId", retailerId);
            intent.putExtra("dealerId", dealerId);
            intent.putExtra("orderNo", orderNo);
            intent.putExtra("orderStatus", status);
            intent.putExtra("dot", dot);
            intent.putExtra("name", name);
            context.startActivity(intent);
        });


    }




    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateList(ArrayList<OrderListResponse> filteredList) {
        this.orderList = filteredList;
        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView order_status,address,dateTime,items,seeDetails,dealer;
        CardView background;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_status = itemView.findViewById(R.id.txt_status);
            address = itemView.findViewById(R.id.txt_address);
            dateTime = itemView.findViewById(R.id.txt_dateOftime);
            items = itemView.findViewById(R.id.txt_items);
            seeDetails = itemView.findViewById(R.id.txt_allDetails);
            background = itemView.findViewById(R.id.card_background);
            dealer = itemView.findViewById(R.id.txt_dealer);



        }
    }
}
