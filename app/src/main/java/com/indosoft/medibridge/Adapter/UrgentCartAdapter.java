package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Activities.DashBoardActivity;
import com.indosoft.medibridge.Model.GetUrgentCartResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.ViewModel.QuantityChangeViewModel;
import com.indosoft.medibridge.ViewModel.UrgentDeleteViewModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class UrgentCartAdapter extends RecyclerView.Adapter<UrgentCartAdapter.ViewHolder> {
    Context context;
    ArrayList<GetUrgentCartResponse> list;
    UrgentDeleteViewModel viewModel;
    QuantityChangeViewModel quantityChangeViewModel;
    public UrgentCartAdapter(Context context, ArrayList<GetUrgentCartResponse> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.urgent_cart_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetUrgentCartResponse response = list.get(position);
         holder.medicineName.setText(response.getProductName());
         holder.stockitsName.setText(response.getDealerName());
         holder.unitName.setText(response.getUnitName());
         holder.quantity.setText(response.getQty());

        if ("1680".equals(response.getProductId())) {
            holder.layout.setVisibility(View.GONE);
            holder.unlisted.setVisibility(View.VISIBLE);
            holder.unlisted.setText(response.getUnlistedMedicines() != null ? response.getUnlistedMedicines() : "N/A");
        } else {
            holder.layout.setVisibility(View.VISIBLE);
            holder.unlisted.setVisibility(View.GONE);
            holder.unitName.setText(response.getUnitName() != null ? response.getUnitName() : "N/A");
        }

        AtomicInteger number = new AtomicInteger(tryParseFloat(response.getQty(), 1));
        String cartId = response.getCartId();
        String productId = response.getProductId();
        updateVisibility(holder, number.get());


        holder.add.setOnClickListener(v -> {
            int newQty = number.incrementAndGet();
            updateQuantity(cartId, productId, String.valueOf(newQty), holder, number, position);
        });

        holder.sub.setOnClickListener(v -> {
            if (number.get() > 1) {
                int newQty = number.decrementAndGet();
                updateQuantity(cartId, productId, String.valueOf(newQty), holder, number, position);
            }
        });

        holder.delete.setOnClickListener(v -> deleteCartItem(cartId, position));
    }
    private int tryParseFloat(String value, int defaultValue) {
        try {
            return value != null ? (int) Float.parseFloat(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void updateQuantity(String cartId, String productId, String newQty, ViewHolder holder, AtomicInteger number, int position) {
        if (quantityChangeViewModel == null) {
            quantityChangeViewModel = new QuantityChangeViewModel();
            quantityChangeViewModel.init(context);
        }

        quantityChangeViewModel.updateQuantityChange(cartId, productId, newQty);
        quantityChangeViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
            if (response != null) {
                list.get(position).setQty(newQty);
                holder.quantity.setText(newQty);
                updateVisibility(holder, number.get());



            } else {
                number.set(Integer.parseInt(list.get(position).getQty()));
                Toast.makeText(context, "Failed to update quantity.", Toast.LENGTH_SHORT).show();
            }
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

                    if (context instanceof DashBoardActivity) {
                        DashBoardActivity dashboard = (DashBoardActivity) context;
                        int urgentCount = dashboard.getUrgentBadgeCount() - 1;
                        dashboard.updateUrgentBadge(urgentCount);

                        if (list.isEmpty()) {
                            dashboard.updateUrgentBadge(0);
                        }
                    }

                }

            } else {
                //Toast.makeText(context, "Failed to delete item: " + response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsFailed().observe((LifecycleOwner) context, error -> {
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

        TextView medicineName,stockitsName,unitName,quantity,unlisted;
        ImageView delete,sub,add;
        LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicineName = itemView.findViewById(R.id.txt_removeMedicineNm);
            stockitsName = itemView.findViewById(R.id.txt_removestockitName);
            unitName = itemView.findViewById(R.id.txt_removeUnit);
            quantity = itemView.findViewById(R.id.txt_removeNumber);
            delete = itemView.findViewById(R.id.img_delete);
            sub = itemView.findViewById(R.id.img_sub);
            add = itemView.findViewById(R.id.img_add);
            layout = itemView.findViewById(R.id.linear_urgentUnit);
            unlisted = itemView.findViewById(R.id.unlistedMedicine);

        }
    }
}
