package com.indosoft.MediBridges.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.MediBridges.Activities.CartActivity;
import com.indosoft.MediBridges.Activities.SignUpActivity;
import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Model.ShowCartResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.DeleteCartViewModel;
import com.indosoft.MediBridges.ViewModel.DeliveryDayViewModel;
import com.indosoft.MediBridges.ViewModel.QuantityChangeViewModel;
import com.indosoft.MediBridges.ViewModel.ShowCartViewModel;
import com.indosoft.MediBridges.ViewModel.UrgentCartViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {
    Context context;
    UrgentCartViewModel cartViewModel;
    ArrayList<ShowCartResponse> list;
    DeleteCartViewModel deleteCartViewModel;
    DeliveryDayViewModel dayViewModel;
    QuantityChangeViewModel quantityChangeViewModel;

    public CardListAdapter(Context context, UrgentCartViewModel cartViewModel, DeliveryDayViewModel dayViewModel, ArrayList<ShowCartResponse> list) {
        this.context = context;
        this.cartViewModel = cartViewModel;
        this.dayViewModel = dayViewModel;  // Initialize the dayViewModel here
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


        AtomicInteger number = new AtomicInteger((int) Float.parseFloat(output.getQty()));
        String cartId = output.getCartId();
        String productId = output.getProductId();
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

        holder.urgent.setOnClickListener(v -> {
            holder.urgent.setEnabled(false); // Disable the urgent button to prevent multiple clicks
            moveToUrgentCart(cartId, Collections.singletonList(position)); // Pass the ViewHolder to re-enable the button after action
        });


        holder.today.setOnClickListener(v -> {
            if (dayViewModel != null) {
                String today = "Today";
                holder.today.setEnabled(false);
                dayViewModel.deliveryDayData(cartId, today);
                dayViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
                    if (response != null) {
                        Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update delivery day to Today", Toast.LENGTH_SHORT).show();
                    }
                    holder.today.setEnabled(true);
                });
            }
        });

        holder.tomorrow.setOnClickListener(v -> {
            if (dayViewModel != null) {
                holder.tomorrow.setEnabled(false);
                String tomorrow = "Tomorrow";
                dayViewModel.deliveryDayData(cartId, tomorrow);
                dayViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
                    if (response != null) {
                        Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update delivery day to Tomorrow", Toast.LENGTH_SHORT).show();
                    }
                    holder.tomorrow.setEnabled(true);
                });
            }
        });


    }

    private void updateQuantity(String cartId, String productId, String qty, ViewHolder holder, AtomicInteger number, int position) {
        if (quantityChangeViewModel == null) {
            quantityChangeViewModel = new QuantityChangeViewModel();
            quantityChangeViewModel.init(context);
        }

        quantityChangeViewModel.updateQuantityChange(cartId, productId, qty);
        quantityChangeViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
            if (response != null) {
                list.get(position).setQty(qty);
                holder.number.setText(qty);
                updateVisibility(holder, number.get());
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                number.getAndSet(Integer.parseInt(list.get(position).getQty()));
                Toast.makeText(context, "Failed to update quantity.", Toast.LENGTH_SHORT).show();
            }
        });
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
              //  refreshCart();

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

    private void moveToUrgentCart(String cartId, List<Integer> positions) {
        if (positions != null && !positions.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmation")
                    .setMessage("Do you want to move the selected items to the urgent cart?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Assuming cartViewModel.getUrgentCart() is updating the live data
                        cartViewModel.getUrgentCart(cartId);
                        cartViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
                            if (response != null) {
                                // Iterate through all the positions and remove each one if valid
                                for (int position : positions) {
                                    if (position >= 0 && position < list.size()) {  // Ensure index is valid
                                        list.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, list.size());
                                    } else {
                                        Log.e("CardListAdapter", "Invalid index: " + position);
                                    }
                                }
                                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to move items to urgent cart.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        } else {
            Log.e("CardListAdapter", "No items selected or invalid positions.");
            Toast.makeText(context, "Error: No items selected or invalid positions.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView medicinename,dealerName,unit,number;
        RadioButton urgent,today,tomorrow;
        ImageView delete,sub,add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicinename = itemView.findViewById(R.id.txt_medicine);
            dealerName = itemView.findViewById(R.id.txt_stockitName);
            unit = itemView.findViewById(R.id.txt_unit);
            number = itemView.findViewById(R.id.txt_number);
            urgent = itemView.findViewById(R.id.radio_urgent);
            today = itemView.findViewById(R.id.radio_today);
            tomorrow = itemView.findViewById(R.id.radio_tomorrow);
            delete = itemView.findViewById(R.id.img_delete);
            sub = itemView.findViewById(R.id.img_sub);
            add = itemView.findViewById(R.id.img_add);
        }
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
