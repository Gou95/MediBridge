package com.indosoft.MediBridges.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.MediBridges.Activities.SeeAllDetailsActivity;
import com.indosoft.MediBridges.Model.OrderListResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.OrderDetailsViewModel;

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
    public OrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.order_list_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, int position) {
        OrderListResponse response = orderList.get(position);
        holder.order_status.setText(response.getOrderStatus());
        holder.orderNum.setText("#"+response.getOrderNo());
        holder.dateTime.setText(response.getAddtime());
        holder.items.setText(response.getTotalItems());

        holder.seeDetails.setOnClickListener(v -> {
            String retailerId = AppSession.getInstance(context).getValue(Constants.RELAILER_ID);
            String orderNo = response.getOrderNo();
            String dot = response.getAddtime();

            Intent intent = new Intent(context, SeeAllDetailsActivity.class);
            intent.putExtra("retailerId", retailerId);
            intent.putExtra("orderNo", orderNo);
            intent.putExtra("dot", dot);
            context.startActivity(intent);
        });

    }




    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView order_status,orderNum,dateTime,items,seeDetails;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_status = itemView.findViewById(R.id.txt_status);
            orderNum = itemView.findViewById(R.id.txt_orderNo);
            dateTime = itemView.findViewById(R.id.txt_dateOftime);
            items = itemView.findViewById(R.id.txt_items);
            seeDetails = itemView.findViewById(R.id.txt_allDetails);

        }
    }
}
