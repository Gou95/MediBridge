package com.indosoft.MediBridges.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.MediBridges.Model.GetUrgentCartResponse;
import com.indosoft.MediBridges.Model.UrgentCartResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.ViewModel.DeleteCartViewModel;
import com.indosoft.MediBridges.ViewModel.UrgentDeleteViewModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class UrgentCartAdapter extends RecyclerView.Adapter<UrgentCartAdapter.ViewHolder> {
    Context context;

    ArrayList<GetUrgentCartResponse> list;
    UrgentDeleteViewModel viewModel;
    private final OnQuantityChangeListener onQuantityChangeListener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged(int newTotalQuantity);
    }

    public UrgentCartAdapter(Context context, ArrayList<GetUrgentCartResponse> list, OnQuantityChangeListener onQuantityChangeListener) {
        this.context = context;
        this.list = list;
        this.onQuantityChangeListener = onQuantityChangeListener;
    }


    @NonNull
    @Override
    public UrgentCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.urgent_cart_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UrgentCartAdapter.ViewHolder holder, int position) {
        GetUrgentCartResponse response = list.get(position);
         holder.medicineName.setText(response.getProductName());
         holder.stockitsName.setText(response.getDealerName());
         holder.unitName.setText(response.getUnitName());
         holder.quantity.setText(response.getQty());

        AtomicInteger number = new AtomicInteger((int) Float.parseFloat(response.getQty()));

        updateVisibility(holder, number.get());


        holder.add.setOnClickListener(v -> {
            number.getAndIncrement();
            response.setQty(String.valueOf(number.get())); // Update qty in the response object
            holder.quantity.setText(String.valueOf(number.get())); // Update the displayed value
            updateVisibility(holder, number.get()); // Update the visibility based on the new quantity
            notifyItemChanged(position);

            if (onQuantityChangeListener != null) {
                onQuantityChangeListener.onQuantityChanged(calculateTotalQuantity());
            }
        });


        holder.sub.setOnClickListener(v -> {
            if (number.get() > 1) {
                number.getAndDecrement();
                response.setQty(String.valueOf(number.get()));
                holder.quantity.setText(String.valueOf(number.get()));
                updateVisibility(holder, number.get());
                notifyItemChanged(position);

                if (onQuantityChangeListener != null) {
                    onQuantityChangeListener.onQuantityChanged(calculateTotalQuantity());
                }
            }
        });


        holder.delete.setOnClickListener(v -> {
            String cartId = response.getCartId();
            deleteCartItem(cartId, position);

        });
    }


    private void deleteCartItem(String cartId, int position) {
        if (viewModel == null) {
            viewModel = new UrgentDeleteViewModel();
            viewModel.init(context);
        }

        viewModel.deleteUrgentCart(cartId);

        viewModel.getLiveData().observe((LifecycleOwner) context, response -> {
            if (response != null) {
                if (position >= 0 && position < list.size()) { // Check if the position is valid
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete item: Invalid position", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Failed to delete item: " + response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsFailed().observe((LifecycleOwner) context, error -> {
            if (error != null) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateVisibility(UrgentCartAdapter.ViewHolder holder, int quantity) {
        if (quantity == 1) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.sub.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.GONE);
            holder.sub.setVisibility(View.VISIBLE);
        }
    }
    private int calculateTotalQuantity() {
        int total = 0;
        for (GetUrgentCartResponse item : list) {
            total += (int) Float.parseFloat(item.getQty()); // Parse as float and cast to int
        }
        return total;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView medicineName,stockitsName,unitName,quantity;
        ImageView delete,sub,add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicineName = itemView.findViewById(R.id.txt_removeMedicineNm);
            stockitsName = itemView.findViewById(R.id.txt_removestockitName);
            unitName = itemView.findViewById(R.id.txt_removeUnit);
            quantity = itemView.findViewById(R.id.txt_removeNumber);
            delete = itemView.findViewById(R.id.img_delete);
            sub = itemView.findViewById(R.id.img_sub);
            add = itemView.findViewById(R.id.img_add);

        }
    }
}
