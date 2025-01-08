package com.indosoft.MediBridges.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.indosoft.MediBridges.Activities.CartActivity;
import com.indosoft.MediBridges.Adapter.DealerListAdapter;
import com.indosoft.MediBridges.Adapter.DropDownAdapter;
import com.indosoft.MediBridges.Adapter.RecentStockitsAdapter;
import com.indosoft.MediBridges.Body.AddtoCartBody;
import com.indosoft.MediBridges.Listener.DealerListener;
import com.indosoft.MediBridges.Model.CityDealerResponse;
import com.indosoft.MediBridges.Model.DealersResponse;
import com.indosoft.MediBridges.Model.IndiaStateResponse;
import com.indosoft.MediBridges.Model.LastOrderResponse;
import com.indosoft.MediBridges.Model.LastStockitsResponse;
import com.indosoft.MediBridges.Model.MedicineListResponse;
import com.indosoft.MediBridges.Model.RecentStockitsResponse;
import com.indosoft.MediBridges.Model.UnitResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.AddtoCartViewModel;
import com.indosoft.MediBridges.ViewModel.CityDealerViewModel;
import com.indosoft.MediBridges.ViewModel.DealerViewModel;
import com.indosoft.MediBridges.ViewModel.LastStockitsViewModel;
import com.indosoft.MediBridges.ViewModel.MedicineViewModel;
import com.indosoft.MediBridges.ViewModel.RecentStockitsViewModel;
import com.indosoft.MediBridges.ViewModel.UnitViewModel;
import com.indosoft.MediBridges.databinding.FragmentHomeBinding;

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
    ArrayList<CityDealerResponse> cityDealerList = new ArrayList<>();
    ArrayList<RecentStockitsResponse> recentList = new ArrayList<>();

    DealerViewModel dealerViewModel;
    UnitViewModel unitViewModel;
    MedicineViewModel medicineViewModel;
    AddtoCartViewModel cartViewModel;
    CityDealerViewModel cityDealerViewModel;
    LastStockitsViewModel lastStockitsViewModel;
    RecentStockitsViewModel recentStockitsViewModel;

    String selectDealerId;
    String selectUnitId;
    String retailerId;
    RecentStockitsAdapter adapter;
    private HashMap<String, String> productMap = new HashMap<>();
    private HashMap<String, String> dealerMap = new HashMap<>();
    private HashMap<String, String> unitNameToIdMap = new HashMap<>();
    private HashMap<String, String> selectDealerToIdMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentHomeBinding.inflate(inflater, container, false);

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        medicineViewModel.init(getContext());
        recentStockitsViewModel = new ViewModelProvider(this).get(RecentStockitsViewModel.class);
        recentStockitsViewModel.init(getContext());

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


        String updatedRetailerName = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_NAME);


        binding.txtRetailerName.setText("Welcome " + (updatedRetailerName != null ? updatedRetailerName : "Default Retailer"));



        initCliks();
        recentStockitsViewModel.recentStockits(AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID));
        adapter = new RecentStockitsAdapter(getContext(),recentList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.horizontalRecyclerView.setLayoutManager(layoutManager);
      binding.horizontalRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    private void onAttachObservers() {

        medicineViewModel.getLiveData().observe(getViewLifecycleOwner(), medicineListResponses -> {
            if (medicineListResponses != null && !medicineListResponses.isEmpty()) {
                itemList.clear();
                itemList.addAll(medicineListResponses);
                List<String> productNameList = new ArrayList<>();
                productMap.clear(); // Clear the map to avoid duplicates

                for (MedicineListResponse response : medicineListResponses) {
                    if (response != null && response.getProductId() != null) {
                        productNameList.add(response.getProductName());
                        productMap.put(response.getProductName(), response.getProductId());
                        AppSession.getInstance(getContext()).setValue(Constants.PRODUCT_ID,response.getProductId());
                    }
                }

                // Create and set the custom adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_items,productNameList);
                binding.autoSearch.setAdapter(adapter);

            } else {
                Log.e("HomeFragment", "Medicine data is null or empty");
            }
        });

        recentStockitsViewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            if (responses != null && !responses.isEmpty()) {
                Log.d("HomeFragment", "Data received: " + responses.size() + " items");
                recentList.clear();
                recentList.addAll(responses);
                adapter.notifyDataSetChanged();
            } else {
                Log.d("HomeFragment", "No data or empty response.");
            }
        });



    }

    private boolean isDealerSelected = false;
    private void initCliks() {
        binding.autoSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProductName = parent.getItemAtPosition(position).toString();
            String selectedProductId = productMap.get(selectedProductName);

            if (selectedProductId != null) {

                AppSession.getInstance(getContext()).setValue(Constants.PRODUCT_ID, selectedProductId);
                isDealerSelected = false; // Reset the dealer selection state
            }

            // Show the popup for selecting unit and dealer
            showPopup(selectedProductName);
            binding.autoSearch.setText(""); // Clear the search text after selection
        });
    }
    private void showPopup(String selectedProductName) {
        // Initialize the ViewModels
        unitViewModel = new ViewModelProvider(this).get(UnitViewModel.class);
        unitViewModel.init(requireContext());  // Use requireContext() instead of getContext()

        cartViewModel = new ViewModelProvider(this).get(AddtoCartViewModel.class);
        cartViewModel.init(requireContext());

        cityDealerViewModel = new ViewModelProvider(this).get(CityDealerViewModel.class);
        cityDealerViewModel.init(requireContext());

        lastStockitsViewModel = new ViewModelProvider(this).get(LastStockitsViewModel.class);
        lastStockitsViewModel.init(requireContext());

        unitViewModel.getUnits();


        cityDealerViewModel.cityDealerData(AppSession.getInstance(getActivity()).getValue(Constants.CITY_ID));

        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.search_list_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        TextView txtItemDetails = popupView.findViewById(R.id.txt_medicineName);
        txtItemDetails.setText(selectedProductName);

        TextView txtNumber = popupView.findViewById(R.id.txt_number);
     TextView txtUnitName = popupView.findViewById(R.id.txt_unitName);
     TextView dealer = popupView.findViewById(R.id.txt_dealer);

        ImageView imgSub = popupView.findViewById(R.id.img_sub);
        ImageView imgAdd = popupView.findViewById(R.id.img_add);
        ImageView imgCancel = popupView.findViewById(R.id.img_cancle);
        CardView addCart = popupView.findViewById(R.id.btn_addCart);
        Spinner unitSpn = popupView.findViewById(R.id.spinner_unit);
        AutoCompleteTextView dealerName = popupView.findViewById(R.id.auto_dealerName);

        unitViewModel.getLiveData().observe(getViewLifecycleOwner(), unitResponses -> {
            if (unitResponses != null && !unitResponses.isEmpty()) {
                unitList.clear();
                unitList.addAll(unitResponses);

                List<String> unitNames = new ArrayList<>();
                unitNameToIdMap.clear();
                for (UnitResponse unit : unitList) {
                    unitNames.add(unit.getUnitName());
                    unitNameToIdMap.put(unit.getUnitName(), unit.getUnitId());
                    AppSession.getInstance(getContext()).setValue(Constants.UNIT_ID,unit.getUnitId());
                }

                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, unitNames);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unitSpn.setAdapter(unitAdapter);

                // Set the default unit for the selected product
                String selectedUnitName = getUnitForProduct(selectedProductName);
                if (selectedUnitName != null) {
                    txtUnitName.setText(selectedUnitName);
                    selectUnitId = unitNameToIdMap.get(selectedUnitName);
                } else {
                    txtUnitName.setText("Unit not available"); // Handle case where no unit is found
                    Log.e("UnitError", "No unit name found for product: " + selectedProductName);
                }


                unitSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectUnitId = unitList.get(position).getUnitId();
                        txtUnitName.setText(selectedUnitName);


                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                txtUnitName.setOnClickListener(v -> {
                    txtUnitName.setVisibility(View.GONE);
                    unitSpn.setVisibility(View.VISIBLE);
                    unitSpn.performClick();
                });
            }
        });

        cityDealerViewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            if (responses != null && !responses.isEmpty()) {
                List<String> stockitsList = new ArrayList<>();
                dealerMap.clear();
                for (CityDealerResponse response : responses) {
                    if (response != null && response.getDealerId() != null) {
                        stockitsList.add(response.getDealerName());
                        dealerMap.put(response.getDealerName(), response.getDealerId());
                        AppSession.getInstance(getContext()).setValue(Constants.DEALER_ID, response.getDealerId());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dealer_items, stockitsList);
                dealerName.setAdapter(adapter);

                String savedDealerId = AppSession.getInstance(getContext()).getValue(Constants.DEALER_ID);
                if (savedDealerId != null && !isDealerSelected) {
                    for (String dealerNameKey : dealerMap.keySet()) {
                        if (dealerMap.get(dealerNameKey).equals(savedDealerId)) {
                            dealerName.setText(dealerNameKey); // Set the previously selected dealer name
                            isDealerSelected = true;
                            break;
                        }
                    }
                }

                dealerName.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedDealerName = parent.getItemAtPosition(position).toString();
                    selectDealerId = dealerMap.get(selectedDealerName);
                    AppSession.getInstance(requireContext()).setValue(Constants.DEALER_ID, selectDealerId);
                    dealer.setText(selectedDealerName);
                });
            }
        });

        String productId = AppSession.getInstance(getContext()).getValue(Constants.PRODUCT_ID);
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

        lastStockitsViewModel.lastStockitsData(retailerId, productId);
        lastStockitsViewModel.getLiveData().observe(getViewLifecycleOwner(), lastStockitsResponses -> {
            if (lastStockitsResponses != null && !lastStockitsResponses.isEmpty()) {
                for (LastStockitsResponse response : lastStockitsResponses) {
                    if (!isDealerSelected) {
                        dealer.setText(response.getDealerName());
                        selectDealerId = response.getDealerId(); // Update dealerId automatically if not selected
                        AppSession.getInstance(getContext()).setValue(Constants.DEALER_ID, selectDealerId);
                    }
                }
            }
        });



        addCart.setOnClickListener(v -> {
            String product_id = AppSession.getInstance(requireContext()).getValue(Constants.PRODUCT_ID);
            String quantity = txtNumber.getText().toString();
            String dealerId = selectDealerId;
            String unitId = selectUnitId;

            dealer.setText(dealerId);

            if (product_id == null || product_id.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a product", Toast.LENGTH_SHORT).show();
                return;
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

            AddtoCartBody body = new AddtoCartBody();
            body.setDealerId(dealerId);
            body.setUnit(unitId);
            body.setProductId(product_id);
            body.setQty(quantity);
            body.setRetailerId(AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID));


            cartViewModel.getAddToCardData(body);

            cartViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
                if (response != null) {
                    Toast.makeText(requireContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
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
            if (number.get() > 1) {
                number.getAndDecrement();
                txtNumber.setText(String.valueOf(number.get()));
            }
        });

        imgAdd.setOnClickListener(v -> {
            number.getAndIncrement();
            txtNumber.setText(String.valueOf(number.get()));
        });

        // Cancel the dialog
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
            if (medicine.getProductName().equalsIgnoreCase(productName)) {
                return (String) medicine.getUnitName(); // Retrieve the correct unit name
            }
        }
        return null;
    }






}