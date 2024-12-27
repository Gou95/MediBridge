package com.indosoft.GoodBooks.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.indosoft.GoodBooks.Activities.CartActivity;
import com.indosoft.GoodBooks.R;
import com.indosoft.GoodBooks.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class HomeFragment extends Fragment {

    private ImageSlider imageSlider;
    FragmentHomeBinding binding;
    private List<String> staticList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentHomeBinding.inflate(inflater, container, false);

        imageSlider = binding.imageSlider;


        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.book, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.book, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.book, ScaleTypes.FIT));
        imageSlider.setImageList(imageList, com.denzcoskun.imageslider.constants.ScaleTypes.CENTER_CROP);

        // Set the slide animation type
        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT);


        staticList = new ArrayList<>();
        staticList.add("Maths Book");
        staticList.add("English Book");
        staticList.add("Hindi Book");
        staticList.add("Science Book");
        staticList.add("Magic Book");
        staticList.add("Jangal Book");

        initCliks();


        return binding.getRoot();
    }

    private void initCliks() {

         binding.edtSearch.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 filterAndDisplayResults(s.toString());
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
    }

    private void filterAndDisplayResults(String query) {
        // Clear any previous results
        binding.resultContainer.removeAllViews();

        // Only display results if the query is not empty
        if (!query.isEmpty()) {
            List<String> filteredList = new ArrayList<>();
            for (String item : staticList) {
                if (item.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            // Show the result container if results are found
            if (!filteredList.isEmpty()) {
                binding.resultContainer.setVisibility(View.VISIBLE);
                for (String item : filteredList) {
                    // Inflate a TextView for each result and add it to the result container
                    TextView resultView = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, binding.resultContainer, false);
                    resultView.setText(item);

                    resultView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopup(item);
                        }
                    });
                    binding.resultContainer.addView(resultView);
                }
            } else {
                binding.resultContainer.setVisibility(View.GONE);
            }
        } else {
            binding.resultContainer.setVisibility(View.GONE);
        }
    }

//    private void showPopup(String item) {
//        // Create a simple AlertDialog to show item details
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Item Details");
//        builder.setMessage("You clicked on: " + item);  // Here, you can customize the message to show more details
//        builder.setPositiveButton("OK", null);  // Close the dialog when the user clicks OK
//
//        // Show the pop-up dialog
//        builder.show();
//    }



    private void showPopup(String item) {

        // Inflate the custom layout for the popup
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.search_list_layout, null);

        // Initialize the custom views in the popup
        TextView txtItemDetails = popupView.findViewById(R.id.txt_medicine);
        txtItemDetails.setText(item);

       TextView txtNumber = popupView.findViewById(R.id.txt_number);
        ImageView imgSub = popupView.findViewById(R.id.img_sub);
        ImageView imgAdd = popupView.findViewById(R.id.img_add);
        ImageView imgCancle = popupView.findViewById(R.id.img_cancle);
         CardView addCart = popupView.findViewById(R.id.btn_addCart);

        // Get initial value of the number from TextView and parse it to an integer
        AtomicInteger number = new AtomicInteger(); // Default value is 0
        try {
            number.set(Integer.parseInt(txtNumber.getText().toString()));
        } catch (NumberFormatException e) {
            // If parsing fails (e.g., empty string or invalid number), set it to 0
            txtNumber.setText("0");
        }

        // Decrease the number when the minus button is clicked
        imgSub.setOnClickListener(v -> {
            if (number.get() > 0) {
                number.getAndDecrement(); // Decrease the value
                txtNumber.setText(String.valueOf(number.get())); // Update the text
            }
        });

        // Increase the number when the add button is clicked
        imgAdd.setOnClickListener(v -> {
            number.getAndIncrement(); // Increase the value
            txtNumber.setText(String.valueOf(number.get())); // Update the text
        });

        addCart.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });

        // Create and set up the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);

        // Create the dialog
        AlertDialog dialog = builder.create();

        imgCancle.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        if (window != null) {
            // Set the gravity to center
            window.setGravity(Gravity.TOP);

            // Optional: Adjust the dialog width and height (if needed)
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;  // Adjust width as needed
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  // Adjust height as needed
            window.setAttributes(layoutParams);
        }

        // Show the dialog
        dialog.show();
    }



}