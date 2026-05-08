package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.indosoft.medibridge.Activities.ExpireActivity;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Model.SignUpResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;

import java.util.ArrayList;

public class AllOrderDetailAdapter extends RecyclerView.Adapter<AllOrderDetailAdapter.ViewHolder> {

    Context context;
    ArrayList<OrderDetailsResponse> list;
    SignUpViewModel sign;

    public AllOrderDetailAdapter(Context context, ArrayList<OrderDetailsResponse> list, SignUpViewModel sign) {
        this.context = context;
        this.list = list;
        this.sign = sign;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_order_details,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailsResponse response = list.get(position);
        String name;

        if (response.getProductName() != null && !response.getProductName().isEmpty()) {
            name = response.getProductName();
        } else if (response.getUnlistedMedicines() != null && !response.getUnlistedMedicines().isEmpty()) {
            name = response.getUnlistedMedicines();
        } else {
            name = "N/A";
        }
        holder.productName.setText(name);

        holder.unitNM.setText(response.getUnitName()+":");
        holder.unitNUmber.setText(response.getOrderQty() );
        holder.deliveryDay.setText(response.getDeliveryDay());
        holder.status.setText(response.getOrderStatus());
        holder.unListed.setText(response.getUnlistedMedicines());
        holder.serialNumber.setText(String.valueOf(position+1)+".");

//        if ("unlisted".equals(response.getProductName())){
//            holder.layout.setVisibility(View.GONE);
//            holder.unListed.setVisibility(View.VISIBLE);
//        }else {
//            holder.layout.setVisibility(View.VISIBLE);
//            holder.unListed.setVisibility(View.GONE);
//        }

        boolean isPed = response.isPed();
        boolean isNotPed = response.isNotPed();

        holder.imgTick.setVisibility(isPed ? View.VISIBLE : View.GONE);
        holder.imgNotPedStatus.setVisibility(isNotPed ? View.VISIBLE : View.GONE);
        holder.expiry.setVisibility(isPed ? View.VISIBLE : View.GONE);
        holder.boxPed.setOnClickListener(v -> {
            boolean newState = !response.isPed();
            response.setPed(newState);
            response.setNotPed(false);
            notifyItemChanged(position);

            if (newState) {
                // If imgTick is now visible, make API call
                String status = "Received";
                String orderItem = response.getOrderItemsId();
                sign.orderStatus(orderItem, status);
                sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                    if (signUpResponse != null) {
                        // Toast.makeText(context,signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        response.setOrderStatus(status);
                       notifyItemChanged(position);
                    }
                });
            }
        });

        holder.boxNotPed.setOnClickListener(v -> {
            boolean newState = !response.isNotPed();
            response.setNotPed(newState);
            response.setPed(false);
            notifyItemChanged(position);

            if (newState) {
                // If imgNotPedStatus is now visible, make API call
                String status = "Not Received";
                String orderItem = response.getOrderItemsId();
                sign.orderStatus(orderItem, status);
                sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                    if (signUpResponse != null) {
                       // Toast.makeText(context,signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        response.setOrderStatus(status);
                        notifyItemChanged(position);
                    }
                });
            }
        });

//


        String productName = response.getProductName();
        String qty = response.getOrderQty();
        String stockist = response.getDealerName();
        String productId = response.getProductId();
        String stockistId = response.getDealerId();
        String unlisted = response.getUnlistedMedicines();
        holder.expiry.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExpireActivity.class);
            intent.putExtra("productName",productName);
            intent.putExtra("qty",qty);
            intent.putExtra("stockist",stockist);
            intent.putExtra("productId",productId);
            intent.putExtra("stockistId",stockistId);
            intent.putExtra("unlisted",unlisted);

            context.startActivity(intent);
        });

      //  updateButtonVisibility(holder, response.getOrderStatus());

        int textColor;
        switch (response.getOrderStatus()) {
            case "Pending":
                textColor = context.getResources().getColor(R.color.yellow);
                break;
            case "Received":
                textColor = context.getResources().getColor(R.color.green);
                break;
            case "Not Received":
                textColor = context.getResources().getColor(R.color.red);
                break;
            default:
                textColor = context.getResources().getColor(R.color.grey);
                break;
        }
        holder.status.setTextColor(textColor);
        holder.button.setOnClickListener(v -> {
            int position1 = holder.getAdapterPosition(); // Store the position before any operation
            if (position1 != RecyclerView.NO_POSITION) {
                int orderItemsId = Integer.parseInt(response.getOrderItemsId());
                sign.deleteOrder(String.valueOf(orderItemsId));  // Call the delete method from the ViewModel
                sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                    if (signUpResponse != null && "Medicine deleted successfully".equals(signUpResponse.getMessage())) {
                        if (position1 < list.size()) {
                            list.remove(position1);
                            notifyItemRemoved(position1);
                            notifyItemRangeChanged(position1, list.size());
                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });



    }

    private void updateButtonVisibility(ViewHolder holder, String status) {
        if ("Pending".equals(status)) {
            holder.button.setVisibility(View.VISIBLE);
        } else {
            holder.button.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName,stockitsNm,unitNM,unitNUmber,deliveryDay,status,serialNumber,unListed,expiry;
        LinearLayout layout;
        FrameLayout boxPed, boxNotPed;
        ImageView imgTick, imgNotPedStatus;
        MaterialButton button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.txt_MedicineNm);
            unitNM = itemView.findViewById(R.id.txt_unitName);
            unitNUmber = itemView.findViewById(R.id.txt_unitNumber);
            deliveryDay = itemView.findViewById(R.id.txt_deliDay);
            status = itemView.findViewById(R.id.txt_orderStatus);
            serialNumber = itemView.findViewById(R.id.txt_serialNumber);
            unListed = itemView.findViewById(R.id.unlistedMedicine);
            layout = itemView.findViewById(R.id.linear_details);
            button = itemView.findViewById(R.id.btn_cancelItem);
            boxPed = itemView.findViewById(R.id.boxPed);
            boxNotPed = itemView.findViewById(R.id.boxNotPed);
            imgTick = itemView.findViewById(R.id.imgTick);
            imgNotPedStatus = itemView.findViewById(R.id.imgNotPedStatus);
            expiry = itemView.findViewById(R.id.txt_expiry);
        }
    }
}
