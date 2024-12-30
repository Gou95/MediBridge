package com.indosoft.mediBridge.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.indosoft.mediBridge.Activities.CartActivity;
import com.indosoft.mediBridge.Adapter.DealerListAdapter;
import com.indosoft.mediBridge.Adapter.DropDownAdapter;
import com.indosoft.mediBridge.Body.AddtoCartBody;
import com.indosoft.mediBridge.Listener.DealerListener;
import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.MedicineListResponse;
import com.indosoft.mediBridge.Model.UnitResponse;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.Session.AppSession;
import com.indosoft.mediBridge.Session.Constants;
import com.indosoft.mediBridge.ViewModel.AddtoCartViewModel;
import com.indosoft.mediBridge.ViewModel.DealerViewModel;
import com.indosoft.mediBridge.ViewModel.MedicineViewModel;
import com.indosoft.mediBridge.ViewModel.UnitViewModel;
import com.indosoft.mediBridge.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class HomeFragment extends Fragment {

    private ImageSlider imageSlider;
    FragmentHomeBinding binding;
    private List<String> staticList;

    ArrayList<DealersResponse> dealerList = new ArrayList<>();
    ArrayList<UnitResponse> unitList = new ArrayList<>();
    ArrayList<MedicineListResponse> itemList = new ArrayList<>();

    DealerViewModel dealerViewModel;
    UnitViewModel unitViewModel;
    MedicineViewModel medicineViewModel;
    AddtoCartViewModel cartViewModel;

    String selectDealerId;
    String selectUnitId;
    String retailerId;
    private HashMap<String, String> productMap = new HashMap<>();
    private HashMap<String, String> dealerMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentHomeBinding.inflate(inflater, container, false);

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        medicineViewModel.init(getContext());

      medicineViewModel.getMedicineData();
        imageSlider = binding.imageSlider;

        onAttachObservers();

        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.b1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.b2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.b3, ScaleTypes.FIT));
        imageSlider.setImageList(imageList, com.denzcoskun.imageslider.constants.ScaleTypes.CENTER_CROP);

        // Set the slide animation type
        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT);

        String retailerName = AppSession.getInstance(getContext()).getString(Constants.RELAILER_NAME);
//        binding.txtRetailerName.setText("Welcome To"+"/"+retailerName != null ? retailerName : "Default Retailer");
        binding.txtRetailerName.setText("Welcome" + retailerName);

        String user_id = AppSession.getInstance(getContext()).getString(Constants.RELAILER_ID);

        Log.d("nnnn", "onCreateView: "+retailerName);
        Log.d("userid", "onCreateView: "+user_id);
        initCliks();


        return binding.getRoot();
    }

    private void onAttachObservers() {

        medicineViewModel.getLiveData().observe(getViewLifecycleOwner(), medicineListResponses -> {
            if (medicineListResponses != null && !medicineListResponses.isEmpty()) {
                List<String> productNameList = new ArrayList<>();
                productMap.clear(); // Clear the map to avoid duplicates

                for (MedicineListResponse response : medicineListResponses) {
                    if (response != null && response.getProductId() != null) {
                        productNameList.add(response.getProductName());
                        productMap.put(response.getProductName(), response.getProductId());
                    }
                }

                // Create and set the custom adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_items,productNameList);
                binding.autoSearch.setAdapter(adapter);

            } else {
                Log.e("HomeFragment", "Medicine data is null or empty");
            }
        });



    }


    private void initCliks() {

        binding.autoSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProductName = parent.getItemAtPosition(position).toString();
            String selectedProductId = productMap.get(selectedProductName);

            if (selectedProductId != null) {
                AppSession.getInstance(getContext()).putString(Constants.PRODUCT_ID, selectedProductId);
            }
            showPopup(selectedProductName);
            binding.autoSearch.setText("");
        });

    }

    @SuppressLint("MissingInflatedId")
    private void showPopup(String selectedProductName) {
        // Initialize the ViewModels
        unitViewModel = new ViewModelProvider(this).get(UnitViewModel.class);
        unitViewModel.init(requireContext());  // Use requireContext() instead of getContext()
        dealerViewModel = new ViewModelProvider(this).get(DealerViewModel.class);
        dealerViewModel.init(requireContext());
        cartViewModel = new ViewModelProvider(this).get(AddtoCartViewModel.class);
        cartViewModel.init(requireContext());

        // Get units and dealers data
        unitViewModel.getUnits();
        dealerViewModel.getDealerData();

        // Inflate the popup layout
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.search_list_layout, null);

        TextView txtItemDetails = popupView.findViewById(R.id.txt_medicineName);
        txtItemDetails.setText(selectedProductName);

        TextView txtNumber = popupView.findViewById(R.id.txt_number);

        ImageView imgSub = popupView.findViewById(R.id.img_sub);
        ImageView imgAdd = popupView.findViewById(R.id.img_add);
        ImageView imgCancel = popupView.findViewById(R.id.img_cancle);
        CardView addCart = popupView.findViewById(R.id.btn_addCart);
        Spinner unitSpn = popupView.findViewById(R.id.spinner_unit);
        AutoCompleteTextView dealerName = popupView.findViewById(R.id.auto_dealerName);

        // Observe unit data and set the adapter for units spinner
        unitViewModel.getLiveData().observe(getViewLifecycleOwner(), unitResponses -> {
            if (unitResponses != null && !unitResponses.isEmpty()) {
                unitList.clear();
                unitList.addAll(unitResponses);

                List<String> unitNames = new ArrayList<>();
                for (UnitResponse unit : unitList) {
                    unitNames.add(unit.getUnitName());
                }

                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, unitNames);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unitSpn.setAdapter(unitAdapter);

                // Set the default unit for the selected product
                String selectedUnitName = getUnitForProduct(selectedProductName);
                int unitPosition = unitNames.indexOf(selectedUnitName);
                if (unitPosition >= 0) {
                    unitSpn.setSelection(unitPosition);
                }

                unitSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectUnitId = unitList.get(position).getUnitId();
                      //  AppSession.getInstance(getContext()).putString(Constants.UNIT_ID,selectUnitId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });




        dealerViewModel.getLiveData().observe(getViewLifecycleOwner(), dealersResponses -> {
            if (dealersResponses != null && !dealersResponses.isEmpty()) {
                List<String> dealerNameList = new ArrayList<>();
                dealerMap.clear(); // Clear the map to avoid duplicates

                for (DealersResponse response : dealersResponses) {
                    if (response != null && response.getDealerId() != null) {
                        dealerNameList.add(response.getDealerName());
                        dealerMap.put(response.getDealerName(), response.getDealerId());
                    }
                }

                // Set up the adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),R.layout.dealer_items, dealerNameList);
                dealerName.setAdapter(adapter);

                dealerName.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedDealerName = parent.getItemAtPosition(position).toString();
                    selectDealerId = dealerMap.get(selectedDealerName);
                    if (selectDealerId != null) {
                        AppSession.getInstance(requireContext()).putString(Constants.DEALER_ID, selectDealerId);
                    }
                });
            }
        });

        addCart.setOnClickListener(v -> {
            // Get the values from the session or UI elements
            String product_id = AppSession.getInstance(requireContext()).getString(Constants.PRODUCT_ID);
            String quantity = txtNumber.getText().toString();
            String dealerId = selectDealerId;  // Replace this with how you retrieve the dealer ID
            String unitId = selectUnitId;      // Replace this with how you retrieve the unit ID


            if (product_id == null || product_id.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a product", Toast.LENGTH_SHORT).show();
                return;  // Exit the method if the product is missing
            }

            if (dealerId == null || dealerId.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a dealer", Toast.LENGTH_SHORT).show();
                return;
            }

            if (unitId == null || unitId.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a unit", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantity == null || quantity.isEmpty() || Integer.parseInt(quantity) <= 0) {
                Toast.makeText(requireContext(), "Please select a valid quantity greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // If all fields are valid, proceed with creating the request body
            AddtoCartBody body = new AddtoCartBody();
            body.setDealerId(dealerId);
            body.setUnit(unitId);
            body.setProductId(product_id);
            body.setQty(quantity);
            body.setRetailerId(AppSession.getInstance(getContext()).getString(Constants.RELAILER_ID));

            // Call the ViewModel to add to cart
            cartViewModel.getAddToCardData(body);

            // Observe the response from ViewModel
            cartViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
                if (response != null) {
                    Toast.makeText(requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    // Handle failure if the response is null
                    Toast.makeText(requireContext(), "Failed to add to cart. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });



        AtomicInteger number = new AtomicInteger();
        try {
            number.set(Integer.parseInt(txtNumber.getText().toString()));
        } catch (NumberFormatException e) {
            txtNumber.setText("1");
        }

        imgSub.setOnClickListener(v -> {
            if (number.get() > 0) {
                number.getAndDecrement();
                txtNumber.setText(String.valueOf(number.get()));
            }
        });

        imgAdd.setOnClickListener(v -> {
            number.getAndIncrement();
            txtNumber.setText(String.valueOf(number.get()));
        });

        // Create and show the popup dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        // Handle cancel button click
        imgCancel.setOnClickListener(v -> dialog.dismiss());

        // Configure the dialog window
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        dialog.show();
    }




    private String getUnitForProduct(String productName) {
        for (MedicineListResponse medicine : itemList) {
            if (medicine.getProductName().equals(productName)) {

                return (String) medicine.getUnitName();
            }
        }
        return "";
    }






}