package com.indosoft.medibridge.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Model.CompanyResponse;
import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.ViewModel.CompanyViewModel;

import java.util.ArrayList;

public class StockistListAdapter extends RecyclerView.Adapter<StockistListAdapter.ViewHolder> {
    Context context;
    ArrayList<StockistListResponse> list;
    ArrayList<CompanyResponse> companyResponseArrayList = new ArrayList<>();
    CompanyViewModel companyViewModel;
    CompanyNameAdapter adapter;

    public StockistListAdapter(Context context, ArrayList<StockistListResponse> list, CompanyViewModel companyViewModel) {
        this.context = context;
        this.list = list;
        this.companyViewModel = companyViewModel;
    }


    @NonNull
    @Override
    public StockistListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stockist_list,parent,false);
        return new ViewHolder(view);
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public void onBindViewHolder(@NonNull StockistListAdapter.ViewHolder holder, int position) {
        StockistListResponse response = list.get(position);
        holder.name.setText(response.getDealerName());
        holder.mobile.setText(response.getDealerPhone());
       // holder.gst.setText(response.getDealerGst());
        holder.address.setText(response.getDealerAddress());
        holder.serial.setText(String.valueOf(position)+":");


        holder.show.setOnClickListener(v -> {
            companyResponseArrayList.clear(); // clear old list
            companyViewModel.getCompany(response.getDealerId());

            View popupView = LayoutInflater.from(context).inflate(R.layout.show_company_list, null);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setView(popupView);
            RecyclerView recyclerView = popupView.findViewById(R.id.recyclerView);
            Button close = popupView.findViewById(R.id.btn_closed);

            adapter = new CompanyNameAdapter(context, companyResponseArrayList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            android.app.AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Add this line
            dialog.show();


            close.setOnClickListener(v1 -> dialog.dismiss());

            // Observe result once
            companyViewModel.getLiveData().observe((LifecycleOwner) context, companyResponses -> {
                if (companyResponses != null) {
                    companyResponseArrayList.clear();
                    companyResponseArrayList.addAll(companyResponses);
                    adapter.notifyDataSetChanged();
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,mobile,gst,address,serial,show;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_stockistNm);
            mobile = itemView.findViewById(R.id.txt_stockitMob);
         //   gst = itemView.findViewById(R.id.txt_stockistGstNo);
            address = itemView.findViewById(R.id.txt_stockistAdd);
            serial = itemView.findViewById(R.id.txt_stockistSerialNo);
            show = itemView.findViewById(R.id.txt_showCompany);
        }
    }
}
