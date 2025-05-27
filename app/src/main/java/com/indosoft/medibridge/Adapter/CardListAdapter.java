package com.indosoft.medibridge.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;


import com.indosoft.medibridge.Activities.DashBoardActivity;
import com.indosoft.medibridge.Model.GetUrgentCartResponse;
import com.indosoft.medibridge.Model.ShowCartResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.DeleteCartViewModel;
import com.indosoft.medibridge.ViewModel.DeliveryDayViewModel;
import com.indosoft.medibridge.ViewModel.QuantityChangeViewModel;
import com.indosoft.medibridge.ViewModel.ShowCartViewModel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {
    Context context;
    ArrayList<ShowCartResponse> list;
    DeleteCartViewModel deleteCartViewModel;
    DeliveryDayViewModel dayViewModel;
    ShowCartViewModel showCartViewModel;
    QuantityChangeViewModel quantityChangeViewModel;
    OnUrgentMovedListener urgentMovedListener;
    public CardListAdapter(Context context, DeliveryDayViewModel dayViewModel, ArrayList<ShowCartResponse> list, ShowCartViewModel showCartViewModel, OnUrgentMovedListener listener) {
        this.context = context;
        this.dayViewModel = dayViewModel;
        this.list = list;
        this.showCartViewModel = showCartViewModel;
        this.urgentMovedListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_list_layout, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShowCartResponse output = list.get(position);
        holder.medicinename.setText(output.getProductName());
        holder.dealerName.setText(output.getDealerName());
        holder.unit.setText(output.getUnitName());
        holder.number.setText(output.getQty());
        holder.unlisted.setText(output.getUnlistedMedicines());
        Log.d("AdapterDebug", "Position: " + position + " | Product ID: " + output.getProductId() + " | Product Name: " + output.getProductName());

        if ("1680".equals(output.getProductId())) {
            holder.linearLayout.setVisibility(View.GONE);
            holder.unlisted.setVisibility(View.VISIBLE);
            holder.unlisted.setText(output.getUnlistedMedicines() != null ? output.getUnlistedMedicines() : "N/A");
        } else {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.unlisted.setVisibility(View.GONE);
            holder.unit.setText(output.getUnitName() != null ? output.getUnitName() : "N/A");
        }
        AtomicInteger number = new AtomicInteger(tryParseFloat(output.getQty(), 1));
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
        holder.delete.setOnClickListener(v -> {
            deleteCartItem(cartId, position);
        });
        holder.urgent.setOnClickListener(v -> {
            handleDeliveryDayUpdate(holder, cartId, "Urgent");
            GetUrgentCartResponse newItem = new GetUrgentCartResponse();
            newItem.setQty("1");
            newItem.setProductName(output.getProductName());
        });

        holder.today.setOnClickListener(v -> {
            holder.today.setEnabled(false);
            String today = "Today";
            dayViewModel.deliveryDayData(cartId,today);
            dayViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
                if (response != null){
                   // Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
                holder.today.setEnabled(true);
            });
        });
        holder.tomorrow.setOnClickListener(v -> {
            holder.tomorrow.setEnabled(false);
            String tomorrow = "Tomorrow";
            dayViewModel.deliveryDayData(cartId,tomorrow);
            dayViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
                if (response != null){
                   // Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
           holder.tomorrow.setEnabled(true);
            });
        });
    }
    private int tryParseFloat(String value, int defaultValue) {
        try {
            return value != null ? (int) Float.parseFloat(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    private void handleDeliveryDayUpdate(ViewHolder holder, String cartId, String day) {
        if ("Urgent".equals(day)) {
            if (showCartViewModel == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            String retailerId = AppSession.getInstance(context).getValue(Constants.RELAILER_ID);
            showCartViewModel.getShowPostCartData(retailerId); // This line will work now as showCartViewModel is initialized
            LayoutInflater inflater = LayoutInflater.from(context);
            View popupView = inflater.inflate(R.layout.popup_layout, null);
            TextView productName = popupView.findViewById(R.id.popup_product_name);
            TextView title = popupView.findViewById(R.id.popup_title);
            TextView message = popupView.findViewById(R.id.popup_message);
            TextView yes = popupView.findViewById(R.id.popup_confirm);
            TextView no = popupView.findViewById(R.id.popup_cancel);
            AlertDialog dialog = builder.setView(popupView).setCancelable(false).create();
            title.setText("Confirm Urgent Day");
            message.setText("Do you want to move this product");
            showCartViewModel.getLiveData().observe((LifecycleOwner) context, showCartResponses -> {
                if (showCartResponses != null && !showCartResponses.isEmpty()) {
                    for (ShowCartResponse response : showCartResponses) {
                        productName.setText(response.getProductName());
                    }
                }
            });
            yes.setOnClickListener(v -> {
                holder.urgent.setEnabled(false);
                callDeliveryDayAPI(holder, cartId, day, true);
                int newBadgeCount = Math.max(list.size() - 1, 0);
                AppSession.getInstance(context).setValue(Constants.CART_COUNT, String.valueOf(newBadgeCount));

                if (context instanceof DashBoardActivity) {
                    DashBoardActivity dashboard = (DashBoardActivity) context;
                    int cartCount = dashboard.getCartBadgeCount();
                    cartCount = Math.max(cartCount - 1, 0);
                    dashboard.updateBadgeCounter(cartCount);
                }
                if (context instanceof DashBoardActivity) {
                    DashBoardActivity dashboard = (DashBoardActivity) context;
                    int currentUrgentCount = dashboard.getUrgentBadgeCount();
                    dashboard.updateUrgentBadge(currentUrgentCount + 1);

                }


                if (urgentMovedListener != null) {
                    urgentMovedListener.onUrgentItemMoved();
                }



                for (ShowCartResponse response : list) {
                    if ("1680".equals(response.getProductId()) || "UNLISTED MEDICINES".equalsIgnoreCase(response.getProductName())) {
                        if (context instanceof DashBoardActivity) {
                            DashBoardActivity dashboard = (DashBoardActivity) context;
                            int cartCount = list.size() - 1; // Instead of getCartBadgeCount() - 1
                            dashboard.updateBadgeCounter(cartCount);
                        }
                        break;
                    }
                }


                dialog.dismiss();

            });
            no.setOnClickListener(v -> {
                dialog.dismiss();
            });
            dialog.show();
        } else {
            callDeliveryDayAPI(holder, cartId, day, false);
        }
    }
    private void callDeliveryDayAPI(ViewHolder holder, String cartId, String day, boolean isUrgent) {
        if (dayViewModel != null) {
            holder.today.setEnabled(false);
            holder.tomorrow.setEnabled(false);
            holder.urgent.setEnabled(false);
            dayViewModel.deliveryDayData(cartId, day);
            dayViewModel.getLiveData().observe((LifecycleOwner) context, response -> {
                if (response != null) {
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCartId().equals(cartId)) {
                            list.remove(i);
                            notifyItemRemoved(i);
                            notifyItemRangeChanged(i, list.size());
                            break;
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to update delivery day to " + day, Toast.LENGTH_SHORT).show();
                }
                holder.urgent.setEnabled(true);
            });
        }
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
            if (position >= 0 && position < list.size()) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());

//                if (context instanceof DashBoardActivity) {
//                    DashBoardActivity dashboard = (DashBoardActivity) context;
//                    int cartCount = dashboard.getCartBadgeCount() - 1;
//                    dashboard.updateBadgeCounter(Math.max(cartCount, 0));
//                }
//                if (context instanceof CartActivity) {
//                    ((CartActivity) context).returnToDashboard();
//                }


                if (context instanceof DashBoardActivity) {
                    DashBoardActivity dashboard = (DashBoardActivity) context;
                    int cartCount = dashboard.getCartBadgeCount() - 1;
                    dashboard.updateBadgeCounter(cartCount);

                    if (list.isEmpty()) {
                        dashboard.updateBadgeCounter(0);
                    }
                }

            } else {
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
        TextView medicinename,dealerName,unit,number,unlisted;
        RadioButton urgent,today,tomorrow;
        ImageView delete,sub,add;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicinename = itemView.findViewById(R.id.txt_medicine);
            dealerName = itemView.findViewById(R.id.txt_stockitName);
            unit = itemView.findViewById(R.id.txt_unitcartName);
            number = itemView.findViewById(R.id.txt_number);
            urgent = itemView.findViewById(R.id.radio_urgent);
            today = itemView.findViewById(R.id.radio_today);
            tomorrow = itemView.findViewById(R.id.radio_tomorrow);
            delete = itemView.findViewById(R.id.img_delete);
            sub = itemView.findViewById(R.id.img_sub);
            add = itemView.findViewById(R.id.img_add);
            linearLayout = itemView.findViewById(R.id.linearUnit);
            unlisted = itemView.findViewById(R.id.unlistedMedicine);
        }
    }
    public interface OnUrgentMovedListener {
        void onUrgentItemMoved();
    }

}
