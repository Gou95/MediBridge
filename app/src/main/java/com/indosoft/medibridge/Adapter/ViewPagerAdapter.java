package com.indosoft.medibridge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.indosoft.medibridge.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    // Titles and images for each slide
    private String[] titles = {
            "Welcome to Medibro, a platform that facilitates communication, order management, and supply chain management for retailers, distributors, and super stockists of medical products.",
            "Introducing Medibro, the comprehensive solution for managing and tracking medical orders, products, and inventory.",
            "Designed specifically for retailers, suppliers, and super stockists, our app allows seamless order placement, tracking, and execution for medical products, medicines, surgicals, and other medical equipment."
    };

    private int[] imageResources = {
            R.drawable.slides_one,
            R.drawable.slides_two,
            R.drawable.slides_three
    };

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return titles.length; // Ensure this matches the length of imageResources
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        TextView titleText = view.findViewById(R.id.textPage);
        ImageView imageView = view.findViewById(R.id.imageView);  // Reference to the ImageView


        if (position < titles.length && position < imageResources.length) {
            titleText.setText(titles[position]);
            imageView.setImageResource(imageResources[position]);  // Set the image for this slide
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
