package com.indosoft.mediBridge.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.mediBridge.Activities.SignUpActivity;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.ShowCartResponse;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.ViewModel.DeleteCartViewModel;
import com.indosoft.mediBridge.ViewModel.UrgentCartViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {
    Context context;
    UrgentCartViewModel cartViewModel;
    ArrayList<ShowCartResponse> list;
    DeleteCartViewModel deleteCartViewModel;

    public CardListAdapter(Context context, UrgentCartViewModel cartViewModel, ArrayList<ShowCartResponse> list) {
        this.context = context;
        this.cartViewModel = cartViewModel;
        this.list = list;

    }

    @NonNull
    @Override
    public CardListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardListAdapter.ViewHolder holder, int position) {
        ShowCartResponse output = list.get(position);

        holder.medicinename.setText(output.getProductName());
        holder.dealerName.setText(output.getDealerName());
        holder.unit.setText(output.getUnitName());
        holder.number.setText(output.getQty());

        holder.urgent.setOnClickListener(v -> {
            String cartId = output.getCartId();
            moveToUrgentCart(cartId, position);
        });
        AtomicInteger number = new AtomicInteger((int) Float.parseFloat(output.getQty()));

        updateVisibility(holder, number.get());


        holder.add.setOnClickListener(v -> {
            number.getAndIncrement();
            output.setQty(String.valueOf(number.get())); // Update qty in the response object
            holder.number.setText(String.valueOf(number.get())); // Update the displayed value
            updateVisibility(holder, number.get()); // Update the visibility based on the new quantity
            notifyItemChanged(position); // Notify adapter to refresh the UI
        });

        // Handle decrement button click (sub)
        holder.sub.setOnClickListener(v -> {
            if (number.get() > 1) {
                number.getAndDecrement();
                output.setQty(String.valueOf(number.get()));
                holder.number.setText(String.valueOf(number.get()));
                updateVisibility(holder, number.get());
                notifyItemChanged(position);
            }
        });

        // Handle delete button click
        holder.delete.setOnClickListener(v -> {
            String cartId = output.getCartId();
            deleteCartItem(cartId, position);

        });


        Log.d("CardListAdapter", "Binding Data - Medicine: " + output.getProductName() +
                ", Dealer: " + output.getDealerName() +
                ", Unit: " + output.getUnitName() +
                ", Qty: " + output.getQty());

    }

    private void deleteCartItem(String cartId, int position) {
        if (deleteCartViewModel == null) {
            deleteCartViewModel = new DeleteCartViewModel();
            deleteCartViewModel.init(context);
        }

        deleteCartViewModel.deleteCartData(cartId);

        deleteCartViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
            if (response != null) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                // Item deletion failure
                Toast.makeText(context, "Failed to delete item: " + response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        deleteCartViewModel.getIsFailed().observe((LifecycleOwner) context, error -> {
            if (error != null) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateVisibility(ViewHolder holder, int quantity) {
        if (quantity == 1) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.sub.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.GONE);
            holder.sub.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView medicinename,dealerName,unit,number;
        RadioButton urgent;
        ImageView delete,sub,add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicinename = itemView.findViewById(R.id.txt_medicine);
            dealerName = itemView.findViewById(R.id.txt_stockitName);
            unit = itemView.findViewById(R.id.txt_unit);
            number = itemView.findViewById(R.id.txt_number);
            urgent = itemView.findViewById(R.id.radio_urgent);
            delete = itemView.findViewById(R.id.img_delete);
            sub = itemView.findViewById(R.id.img_sub);
            add = itemView.findViewById(R.id.img_add);
        }
    }

    private void moveToUrgentCart(String cartId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation")
                .setMessage("Do you want to move this item to the urgent cart?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    cartViewModel.getUrgentCart(cartId);
                    cartViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
                        if (response != null && "success".equalsIgnoreCase(response.getMessage())) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, list.size());
                            Toast.makeText(context, "Moved to urgent cart successfully!", Toast.LENGTH_SHORT).show();
                            // Trigger a refresh for the remaining cart items
                         //   refreshCart(cartId);
                        } else {
                            Toast.makeText(context, "Failed to move item to urgent cart.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    cartViewModel.getIsFailed().observe((LifecycleOwner) context, error -> {
                        if (error != null) {
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

//    private void refreshCart(String cartId) {
//
//        cartViewModel.getUrgentCart(cartId);
//        cartViewModel.getLiveData().observe((LifecycleOwner) context,response -> {
//            if (response != null ) {
//                list.clear();
//                list.addAll((Collection<? extends ShowCartResponse>) response);
//                notifyDataSetChanged();
//                Toast.makeText(context, "Cart refreshed!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
