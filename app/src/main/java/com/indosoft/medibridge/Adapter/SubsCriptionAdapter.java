package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.indosoft.medibridge.Activities.SubscribeActivity;
import com.indosoft.medibridge.Model.PlansResponse;
import com.indosoft.medibridge.R;

import java.util.ArrayList;

public class SubsCriptionAdapter extends RecyclerView.Adapter<SubsCriptionAdapter.ViewHolder> {

    Context context;
    ArrayList<PlansResponse>list;

    public SubsCriptionAdapter(Context context, ArrayList<PlansResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubsCriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.plans_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PlansResponse response = list.get(position);

        int months = response.getMonths();
        int monthlyPrice = response.getMonthlyCharge();
        int totalAmount = months * monthlyPrice;
        int validityDays = months * 30;

        /* ================= TITLE ================= */

        // 🔒 VERY IMPORTANT: reset TextView style
        holder.name.setTypeface(Typeface.DEFAULT);
        holder.name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        String monthLabel = (months == 1) ? "Month" : "Months";

        String titleText =
                months + " " + monthLabel + " Plan(" + validityDays + " days) \n";

        SpannableString title = new SpannableString(titleText);

// 🔵 + 🟠 COLOR (Month + Plan)
        int monthEnd = (months + " " + monthLabel).length();
        title.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue_light)),
                0,
                monthEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        int planStart = monthEnd + 1;
        int planEnd = planStart + "Plan".length();
        title.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.orange_dark)),
                planStart,
                planEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

// 🔥 BOLD ONLY: "X Month(s) Plan"
        int boldEnd = (months + " " + monthLabel + " Plan").length();
        title.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                boldEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

// ⚫ "(30 days)" → NORMAL
        int daysStart = titleText.indexOf("(");
        title.setSpan(
                new StyleSpan(Typeface.NORMAL),
                daysStart,
                titleText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        holder.name.setText(title);



        /* ================= DESCRIPTION ================= */

        // 🔒 Reset description TextView style
        holder.charges.setTypeface(Typeface.DEFAULT);
        holder.name.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);


        SpannableStringBuilder desc = new SpannableStringBuilder();


        int validityLabelStart = desc.length();
        desc.append("Validity : ");
        int validityLabelEnd = desc.length();
        desc.setSpan(new StyleSpan(Typeface.BOLD), validityLabelStart, validityLabelEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int validityDaysStart = desc.length();
        desc.append(validityDays + " Days\n");

        int validityDaysEnd = desc.length();
        desc.setSpan(new StyleSpan(Typeface.BOLD), validityDaysStart, validityDaysEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        desc.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.orange_dark)),
                validityDaysStart, validityDaysEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        int amtStart = desc.length();
        desc.append("₹ " + totalAmount + " Only ");
        int amtEnd = desc.length();
        desc.setSpan(new StyleSpan(Typeface.BOLD), amtStart, amtEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int gstStart = desc.length();
        desc.append("(Including GST)\n");
        int gstEnd = desc.length();
        desc.setSpan(new StyleSpan(Typeface.NORMAL), gstStart, gstEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        desc.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue_light)),
                gstStart, gstEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Validity label → NORMAL


        // Days → NORMAL + ORANGE


        // 🟣 Effective cost line → NORMAL
        int effStart = desc.length();
        desc.append("Plans effective cost is" +" "+
                monthlyPrice + " per Month \n");
        int effEnd = desc.length();
        desc.setSpan(new StyleSpan(Typeface.NORMAL), effStart, effEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        desc.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.purple_700)),
                effStart, effEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        holder.charges.setText(desc);
        holder.charges.setLineSpacing(1.4f, 1.4f);

        holder.duration.setVisibility(View.GONE);

        holder.btn.setOnClickListener(v -> {
            Intent intent = new Intent(context, SubscribeActivity.class);
            intent.putExtra("amount", totalAmount);
            intent.putExtra("planId", response.getId());
            intent.putExtra("months", months);
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<PlansResponse> updatedList) {
        this.list = new ArrayList<>(updatedList);
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,charges,duration;
        MaterialButton btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_subsName);
            charges = itemView.findViewById(R.id.txt_subsCharges);
            duration = itemView.findViewById(R.id.txtPlanDuration);
            btn = itemView.findViewById(R.id.btnSubscribe);
        }
    }
}
