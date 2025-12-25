package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridge.Activities.CashMemoListActivity;
import com.indosoft.medibridge.Model.CashMemodetailsResponse;
import com.indosoft.medibridge.Model.PosDetailsResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class CashMemoDetailsAdapter extends RecyclerView.Adapter<CashMemoDetailsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CashMemodetailsResponse> list;

    public CashMemoDetailsAdapter(Context context, ArrayList<CashMemodetailsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CashMemoDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cashmemo_details_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CashMemoDetailsAdapter.ViewHolder holder, int position) {
        CashMemodetailsResponse response = list.get(position);
        holder.billNo.setText(response.getBillNo());
        holder.doctor.setText(response.getDoctorName());
        holder.patient.setText(response.getPatientName());
        holder.date.setText(response.getBillDate());
        holder.items.setText(response.getTotalitems());
        holder.amounts.setText((CharSequence) response.getBillAmount());

        String bill_no = response.getBillNo();
        String date = response.getBillDate();
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CashMemoListActivity.class);
            intent.putExtra("bill_no",bill_no);
            intent.putExtra("date",date);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<CashMemodetailsResponse> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView billNo, doctor,patient,date,items,amounts;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            billNo = itemView.findViewById(R.id.txt_billNo);
            doctor = itemView.findViewById(R.id.txt_doctorName);
            patient = itemView.findViewById(R.id.txt_patientName);
            date = itemView.findViewById(R.id.txt_billDate);
            items = itemView.findViewById(R.id.txt_totalItems);
            amounts = itemView.findViewById(R.id.txt_billAmt);
            cardView = itemView.findViewById(R.id.pos_cardview);

        }
    }
}
