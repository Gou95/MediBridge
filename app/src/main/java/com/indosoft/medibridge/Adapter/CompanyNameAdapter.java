package com.indosoft.medibridgestockist.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.medibridgestockist.Model.CompanyResponse;
import com.indosoft.medibridgestockist.R;

import java.util.ArrayList;

public class CompanyNameAdapter extends RecyclerView.Adapter<CompanyNameAdapter.ViewHolder> {

    Context context;
    ArrayList<CompanyResponse> list;

    public CompanyNameAdapter(Context context, ArrayList<CompanyResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CompanyNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.company_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyNameAdapter.ViewHolder holder, int position) {
        CompanyResponse response = list.get(position);
        holder.companyName.setText(response.getCompanyName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.txt_companyName);
        }
    }
}
