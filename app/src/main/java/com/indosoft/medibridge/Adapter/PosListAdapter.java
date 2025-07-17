package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.indosoft.medibridge.Body.PosUpdateBody;
import com.indosoft.medibridge.Model.GetPosProductResponse;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PosListAdapter extends RecyclerView.Adapter<PosListAdapter.ViewHolder> {
    Context context;
    ArrayList<GetPosProductResponse> list;
    ArrayList<UnitResponse> unitList;
    String selectUnitId;
    SignUpViewModel sign;
    UnitViewModel unitViewModel;


    OnPosUpdatedListener updateListener;

    public PosListAdapter(Context context, ArrayList<GetPosProductResponse> list, SignUpViewModel sign, OnPosUpdatedListener updateListener) {
        this.context = context;
        this.list = list;
        this.sign = sign;
        this.updateListener = updateListener;
    }

    @NonNull
    @Override
    public PosListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pos_list,parent,false);
        return new PosListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosListAdapter.ViewHolder holder, int position) {
        GetPosProductResponse response = list.get(position);
        holder.medicine.setText(response.getProductName());
        holder.unitName.setText(response.getUnitName());
        holder.qty.setText(response.getQty());
        holder.amount.setText(response.getAmount());
        holder.linearLayout.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(context);
            View popupView = inflater.inflate(R.layout.pos_list_popup, null);

            unitViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UnitViewModel.class);
            unitViewModel.init(context);
            unitViewModel.getUnits();

            HashMap<String, String> unitNameToIdMap = new HashMap<>();
            unitList = new ArrayList<>();

            // Bind views
            TextView txtMediName = popupView.findViewById(R.id.txt_posMediName);
            TextView txtCompanyName = popupView.findViewById(R.id.txt_posCompanyName);
            TextView txtQty = popupView.findViewById(R.id.txt_number);
            TextView txtUnit = popupView.findViewById(R.id.txt_posUnitName);
            EditText edtAmount = popupView.findViewById(R.id.edt_posAmount);
            ImageView btnAdd = popupView.findViewById(R.id.img_add);
            ImageView btnSub = popupView.findViewById(R.id.img_sub);
            ImageView btnClose = popupView.findViewById(R.id.img_cancle);
            CardView btnAddCart = popupView.findViewById(R.id.btn_addCart);
            Spinner unitSpn = popupView.findViewById(R.id.spin_unit);

            // Set existing data
            txtMediName.setText(response.getProductName());
            txtCompanyName.setText("Company Name"); // or from response
            txtQty.setText(response.getQty());
            txtUnit.setText(response.getUnitName());
            edtAmount.setText(response.getAmount());

            // Show spinner when unit TextView clicked
            txtUnit.setOnClickListener(v12 -> {
                txtUnit.setVisibility(View.GONE);
                unitSpn.setVisibility(View.VISIBLE);
                unitSpn.performClick();
            });

            // Observe unit list
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

                        if (name.equalsIgnoreCase(response.getUnitName())) {
                            selectedIndex = i;
                        }
                    }

                    ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, unitNames);
                    unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    unitSpn.setAdapter(unitAdapter);
                    unitSpn.setSelection(selectedIndex);

                    String defaultUnitName = unitNames.get(selectedIndex);
                    selectUnitId = unitNameToIdMap.get(defaultUnitName);

                    unitSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedUnitName = unitNames.get(position);
                            txtUnit.setText(selectedUnitName);
                            selectUnitId = unitNameToIdMap.get(selectedUnitName);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });

                }
            });


            final float[] quantity = {Float.parseFloat(response.getQty())};
            btnAdd.setOnClickListener(v13 -> {
                quantity[0]++;
                txtQty.setText(String.valueOf(quantity[0]));
            });
            btnSub.setOnClickListener(v14 -> {
                if (quantity[0] > 1) {
                    quantity[0]--;
                    txtQty.setText(String.valueOf(quantity[0]));
                }
            });
            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                    .setView(popupView)
                    .setCancelable(false)
                    .create();

            dialog.show();
            // Add to cart button (calls PUT API)
            btnAddCart.setOnClickListener(v15 -> {
                String updatedQty = txtQty.getText().toString();
                String updatedAmount = edtAmount.getText().toString();
                String posId = response.getId();
                String unitId = selectUnitId;// required for PUT

                if (updatedQty.isEmpty() || updatedAmount.isEmpty()) {
                    Toast.makeText(context, "Please fill Qty & Amount", Toast.LENGTH_SHORT).show();
                    return;
                } else if (unitId == null || unitId.isEmpty()) {
                    Toast.makeText(context, "Please select a unit", Toast.LENGTH_SHORT).show();
                    return;
                }

                PosUpdateBody body = new PosUpdateBody();
                body.setProductId(response.getProductId());
                body.setQty(updatedQty);
                body.setAmount(updatedAmount);
                body.setUnitId(unitId); // ✅ use selected unitId from spinner

                sign.posUpdate(posId, body);

                sign.getLiveData().observe((LifecycleOwner) context, signUpResponse -> {
                    if (signUpResponse != null) {
                        Toast.makeText(context, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            });

            // Close popup
            btnClose.setOnClickListener(v16 -> dialog.dismiss());

            // Show popup

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

//    public void updateList(ArrayList<OrderRegisterResponse> filteredList) {
//        this.list = filteredList;
//        notifyDataSetChanged();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicine,unitName,qty,amount;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicine = itemView.findViewById(R.id.txt_posProductName);
            unitName = itemView.findViewById(R.id.txt_posUnitNM);
            qty = itemView.findViewById(R.id.txt_posQty);
            amount = itemView.findViewById(R.id.txt_posAmount);
            linearLayout = itemView.findViewById(R.id.pos_linear);


        }
    }
    public interface OnPosUpdatedListener {
        void onPosUpdated();
    }
}
