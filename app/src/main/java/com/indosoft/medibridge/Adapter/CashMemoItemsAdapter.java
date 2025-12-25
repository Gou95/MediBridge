package com.indosoft.medibridge.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.indosoft.medibridge.Body.CashMemoAddBody;
import com.indosoft.medibridge.Model.CashMemoItemDetailsResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CashMemoItemsAdapter extends RecyclerView.Adapter<CashMemoItemsAdapter.ViewHolder> {

    Context context;
    ArrayList<CashMemoItemDetailsResponse> list;
    ArrayList<UnitResponse> unitList;
    UnitViewModel unitViewModel;
    SignUpViewModel sign;
    String selectUnitId;
    private OnMedicineUpdatedListener listener;

    public CashMemoItemsAdapter(Context context, ArrayList<CashMemoItemDetailsResponse> list, SignUpViewModel sign, OnMedicineUpdatedListener listener) {
        this.context = context;
        this.list = list;
        this.sign = sign;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CashMemoItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cash_memo_list, parent, false);
        return new CashMemoItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CashMemoItemsAdapter.ViewHolder holder, int position) {
        CashMemoItemDetailsResponse response = list.get(position);

        holder.product.setText(response.getProductName());
        holder.unit.setText(response.getUnitName());
        holder.expiry.setText(response.getExpiryDate());
        holder.qty.setText(response.getQty());
        holder.batch.setText(response.getBatchNo());
        holder.amount.setText(response.getAmount());
        holder.rate.setText(response.getRate());


        holder.delete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Do you want to delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    sign.deleteItem(response.getId());
                    sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                        if (signUpResponse != null) {
                            holder.delete.setVisibility(View.GONE);
                            if (listener != null) listener.onMedicineUpdated();

                        }

                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show());


        holder.layout.setOnClickListener(v -> {
            View popupView = LayoutInflater.from(context).inflate(R.layout.cash_memo_details, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(popupView);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            unitViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UnitViewModel.class);
            unitViewModel.init(context);
            unitViewModel.getUnits();

            HashMap<String, String> unitNameToIdMap = new HashMap<>();
            unitList = new ArrayList<>();

            TextView txtItemDetails = popupView.findViewById(R.id.txt_posMediName);
            TextView companyNm = popupView.findViewById(R.id.txt_posCompanyName);
            TextView txtNumber = popupView.findViewById(R.id.txt_number);
            TextView txtUnitName = popupView.findViewById(R.id.txt_posUnitName);
            ImageView imgSub = popupView.findViewById(R.id.img_sub);
            ImageView imgAdd = popupView.findViewById(R.id.img_add);
            ImageView imgCancel = popupView.findViewById(R.id.img_cancle);
            CardView addCart = popupView.findViewById(R.id.btn_addCart);
            Spinner unitSpn = popupView.findViewById(R.id.spin_unit);
            EditText amount = popupView.findViewById(R.id.edt_cashAmount);
            EditText batchNo = popupView.findViewById(R.id.edt_cashBatch);
            EditText expiry = popupView.findViewById(R.id.edt_cashExpiry);
            EditText rate = popupView.findViewById(R.id.edt_cashRate);

            txtItemDetails.setText(response.getProductName());
            companyNm.setText("company name");
            txtUnitName.setText(response.getUnitName());
            txtNumber.setText(response.getQty());
            batchNo.setText(response.getBatchNo());
            expiry.setText(response.getExpiryDate());
            amount.setText(response.getAmount());
            rate.setText(response.getRate());

            txtUnitName.setOnClickListener(v12 -> {
                txtUnitName.setVisibility(View.GONE);
                unitSpn.setVisibility(View.VISIBLE);
                unitSpn.performClick();
            });

            // 🔄 LOAD UNIT LIST
            unitViewModel.getLiveData().observe((LifecycleOwner) context, unitResponses -> {
                if (unitResponses != null && !unitResponses.isEmpty()) {
                    unitList.clear();
                    unitList.addAll(unitResponses);
                    List<String> unitNames = new ArrayList<>();
                    int selectedIndex = 0;
                    for (int i = 0; i < unitList.size(); i++) {
                        String name = unitList.get(i).getUnitName();
                        unitNames.add(name);
                        unitNameToIdMap.put(name, unitList.get(i).getUnitId());
                        if (name.equalsIgnoreCase(response.getUnitName())) selectedIndex = i;
                    }
                    ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, unitNames);
                    unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    unitSpn.setAdapter(unitAdapter);
                    unitSpn.setSelection(selectedIndex);
                    selectUnitId = unitNameToIdMap.get(unitNames.get(selectedIndex));
                    unitSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedName = unitNames.get(position);
                            txtUnitName.setText(selectedName);
                            selectUnitId = unitNameToIdMap.get(selectedName);
                        }
                        @Override public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            });

            // 📅 EXPIRY PICKER
            expiry.setOnClickListener(view -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.calendar_view, null);
                builder1.setView(dialogView);
                final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
                int daySpinnerId = context.getResources().getIdentifier("android:id/day", null, null);
                if (daySpinnerId != 0) {
                    View daySpinner = datePicker.findViewById(daySpinnerId);
                    if (daySpinner != null) daySpinner.setVisibility(View.GONE);
                }
                TextView btnCancel = dialogView.findViewById(R.id.txt_cancel);
                TextView btnOk = dialogView.findViewById(R.id.txt_ok);
                final AlertDialog dialog1 = builder1.create();
                btnCancel.setOnClickListener(v2 -> dialog1.dismiss());
                btnOk.setOnClickListener(v2 -> {
                    int month = datePicker.getMonth() + 1;
                    int year = datePicker.getYear();
                    expiry.setText(month + "/" + year);
                    dialog1.dismiss();
                });
                dialog1.show();
            });

            // 🧮 QUANTITY LOGIC
            int qtyVal = 1;
            try { qtyVal = (int) Double.parseDouble(response.getQty()); } catch (NumberFormatException e) {}
            AtomicInteger number = new AtomicInteger(qtyVal);
            txtNumber.setText(String.valueOf(qtyVal));
            Runnable calculateAmount = () -> {
                try {
                    int qty = Integer.parseInt(txtNumber.getText().toString());
                    double rateVal = Double.parseDouble(rate.getText().toString());
                    double total = qty * rateVal;
                    amount.setText(String.valueOf(total));
                } catch (Exception e) { amount.setText("0"); }
            };
            imgSub.setOnClickListener(view -> {
                if (number.get() > 1) {
                    number.getAndDecrement();
                    txtNumber.setText(String.valueOf(number.get()));
                    calculateAmount.run();
                }
            });
            imgAdd.setOnClickListener(view -> {
                number.getAndIncrement();
                txtNumber.setText(String.valueOf(number.get()));
                calculateAmount.run();
            });
            rate.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) { calculateAmount.run(); }
            });
            calculateAmount.run();

            // ❌ CANCEL
            imgCancel.setOnClickListener(v1 -> dialog.dismiss());

            // ✅ ADD CART (UPDATE)
            addCart.setOnClickListener(view -> {
                String retailerId = AppSession.getInstance(context).getValue(Constants.RELAILER_ID);
                String product_id = response.getProductId();
                String quantity = txtNumber.getText().toString();
                String expiry_date = expiry.getText().toString();
                String batch = batchNo.getText().toString();
                String unitId = selectUnitId;
                String rat = rate.getText().toString();
                String amt = amount.getText().toString();
                String id = response.getId();

                if (batch.isEmpty() || expiry_date.isEmpty() || quantity.isEmpty() || rat.isEmpty() || amt.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                CashMemoAddBody body = new CashMemoAddBody();
                body.setProductId(product_id);
                body.setUnitId(unitId);
                body.setQty(quantity);
                body.setAmount(amt);
                body.setBatchNo(batch);
                body.setExpiryDate(expiry_date);
                body.setRate(rat);

                Log.d("PUT_BODY", new Gson().toJson(body));

                sign.updateItem(retailerId, id, body);
                sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                    if (signUpResponse != null) {
                        Toast.makeText(context, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if (listener != null) listener.onMedicineUpdated();
                    } else {
                        Toast.makeText(context, "Failed to update medicine", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            dialog.show();
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.TOP);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(layoutParams);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product, unit, expiry, qty, batch, amount, rate;
        LinearLayout layout;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.txt_cashMemoProductName);
            unit = itemView.findViewById(R.id.txt_cashMemoUnitNM);
            expiry = itemView.findViewById(R.id.txt_cashMemoExpiry);
            qty = itemView.findViewById(R.id.txt_cashMemoQty);
            batch = itemView.findViewById(R.id.txt_cashMemoBatch);
            amount = itemView.findViewById(R.id.txt_cashMemoAmount);
            layout = itemView.findViewById(R.id.cash_linear);
            rate = itemView.findViewById(R.id.txt_cashMemoRate);
            delete = itemView.findViewById(R.id.img_delete);
        }
    }

    public interface OnMedicineUpdatedListener {
        void onMedicineUpdated();
    }
}
