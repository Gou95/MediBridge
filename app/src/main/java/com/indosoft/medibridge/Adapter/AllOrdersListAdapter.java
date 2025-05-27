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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.indosoft.medibridge.Activities.ExpireActivity;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;

import java.util.ArrayList;

public class AllOrdersListAdapter extends RecyclerView.Adapter<AllOrdersListAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderDetailsResponse> list;
    SignUpViewModel sign;
    ActivityResultLauncher<Intent> expiryLauncher;
    public AllOrdersListAdapter(Context context, ArrayList<OrderDetailsResponse> list, SignUpViewModel sign, ActivityResultLauncher<Intent> expiryLauncher) {
        this.context = context;
        this.list = list;
        this.sign = sign;
        this.expiryLauncher = expiryLauncher;
    }



    @NonNull
    @Override
    public AllOrdersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_orders_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllOrdersListAdapter.ViewHolder holder, int position) {
        OrderDetailsResponse response = list.get(position);
        holder.medicine.setText(response.getProductName());
        holder.unitName.setText(response.getUnitName());
        holder.unitQty.setText(response.getOrderQty());
        holder.stockist.setText(response.getDealerName());
        holder.delivary.setText(response.getDeliveryDay());
        holder.status.setText(response.getOrderStatus());
        holder.time.setText(response.getAddtime());

        holder.serial.setText(String.valueOf(position+1)+".");
        holder.unlisted.setText(response.getUnlistedMedicines());
        holder.expiryMonth.setText("Expiry Month :"+" "+" "+ response.getExpiryMonth());

        if ("UNLISTED MEDICINES".equals(response.getProductName())){
            holder.unlisted.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
        }else {
            holder.unlisted.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.VISIBLE);
        }

        String orderStatus = response.getOrderStatus();
        if ("Received".equalsIgnoreCase(orderStatus)) {
            holder.imgTick.setVisibility(View.VISIBLE);
            holder.registerExpiry.setVisibility(View.VISIBLE);
            holder.imgNotPedStatus.setVisibility(View.GONE);
        } else if ("Not Received".equalsIgnoreCase(orderStatus)) {
            holder.imgTick.setVisibility(View.GONE);
            holder.registerExpiry.setVisibility(View.GONE);
            holder.imgNotPedStatus.setVisibility(View.VISIBLE);
        } else {
            holder.imgTick.setVisibility(View.GONE);
            holder.registerExpiry.setVisibility(View.GONE);
            holder.imgNotPedStatus.setVisibility(View.GONE);
        }

        // ✅ Set onClick for Ped (Recieved)
        holder.boxPed.setOnClickListener(v -> {
            String status = "Received";
            String orderItem = response.getOrderItemsId();
            sign.orderStatus(orderItem, status);
            sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                if (signUpResponse != null) {
                    response.setOrderStatus(status); // ✅ update model
                    notifyItemChanged(position);     // ✅ refresh view
                }
            });
        });

        // ✅ Set onClick for Not Ped (Not Recieved)
        holder.boxNotPed.setOnClickListener(v -> {
            String status = "Not Received";
            String orderItem = response.getOrderItemsId();
            sign.orderStatus(orderItem, status);
            sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                if (signUpResponse != null) {
                    response.setOrderStatus(status); // ✅ update model
                    notifyItemChanged(position);     // ✅ refresh view
                }
            });
        });
//

        holder.expiry.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExpireActivity.class);
            intent.putExtra("productName", response.getProductName());
            intent.putExtra("qty", response.getOrderQty());
            intent.putExtra("stockist", response.getDealerName());
            intent.putExtra("productId", response.getProductId());
            intent.putExtra("stockistId", response.getDealerId());
            intent.putExtra("orderItemsId", response.getOrderItemsId());
            expiryLauncher.launch(intent);  // ✅ Use launcher instead of direct startActivity
        });


        updateButtonVisibility(holder, response.getOrderStatus());

        int textColor;
        switch (orderStatus) {
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
                sign.deleteOrder(String.valueOf(orderItemsId));

                sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                    if (signUpResponse != null && "Medicine deleted successfully".equals(signUpResponse.getMessage())) {
                        // Check if the position is still valid
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

    public void updateList(ArrayList<OrderDetailsResponse> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicine,unitName,unitQty,stockist,delivary,status ,serial,time,unlisted,expiry,expiryMonth;
        LinearLayout linearLayout,registerExpiry;
        FrameLayout boxPed, boxNotPed;
        ImageView imgTick, imgNotPedStatus;


        MaterialButton button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicine = itemView.findViewById(R.id.txt_all_OrderMedicineNm);
            unitName = itemView.findViewById(R.id.txt_all_OrderUnitName);
            unitQty = itemView.findViewById(R.id.txt_all_OrderUnitNumber);
            stockist = itemView.findViewById(R.id.txt_all_OrderStockitName);
            delivary = itemView.findViewById(R.id.txt_all_orderDeliDay);
            status = itemView.findViewById(R.id.txt_all_OrderStatus);
            serial = itemView.findViewById(R.id.txt_all_OrderSerial);
            time = itemView.findViewById(R.id.txt_all_OrderDot);
            linearLayout = itemView.findViewById(R.id.linear_all_OrderLayout);
            unlisted = itemView.findViewById(R.id.txt_all_OrderUnlisted);
            boxPed = itemView.findViewById(R.id.all_boxPed);
            boxNotPed = itemView.findViewById(R.id.all_boxNotPed);
            imgTick = itemView.findViewById(R.id.all_imgTick);
            imgNotPedStatus = itemView.findViewById(R.id.all_imgNotPedStatus);
            expiry = itemView.findViewById(R.id.txt_all_expiry);
            button = itemView.findViewById(R.id.btn_all_cancelItem);
            expiryMonth = itemView.findViewById(R.id.txt_all_expiryMonth);
            registerExpiry = itemView.findViewById(R.id.linear_all_expiry);
        }
    }
}
