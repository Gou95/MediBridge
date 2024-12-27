package com.indosoft.GoodBooks.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.indosoft.GoodBooks.Model.CardListResponse;
import com.indosoft.GoodBooks.R;

import java.util.ArrayList;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    Context context;

    ArrayList<CardListResponse> list;

    public CardListAdapter(Context context, ArrayList<CardListResponse> list) {
        this.context = context;
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
        CardListResponse output = list.get(position);

        holder.name.setText(output.getName());
        holder.id.setText(output.getId());

        // Log to see if data is correctly binding
        Log.d("CardListAdapter", "onBindViewHolder: " + output.getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id,name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.txt_id);
            name = itemView.findViewById(R.id.txt_name);
        }
    }



}
