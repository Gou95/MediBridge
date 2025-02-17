package com.indosoft.medibridge.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
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
import com.indosoft.medibridge.Activities.DashBoardActivity;
import com.indosoft.medibridge.Activities.ExpiryListActivity;
import com.indosoft.medibridge.Activities.NotificationActivity;
import com.indosoft.medibridge.Activities.OrderRegisterActivity;
import com.indosoft.medibridge.Activities.ExpiryRegisterActivity;
import com.indosoft.medibridge.Activities.UnlistedStockistActivity;
import com.indosoft.medibridge.Activities.ViewAllStockistActivity;
import com.indosoft.medibridge.Adapter.RecentStockitsAdapter;
import com.indosoft.medibridge.Body.AddtoCartBody;
import com.indosoft.medibridge.Model.CityDealerResponse;
import com.indosoft.medibridge.Model.LastStockitsResponse;
import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.Model.NotificationResponse;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.AddtoCartViewModel;
import com.indosoft.medibridge.ViewModel.CityDealerViewModel;
import com.indosoft.medibridge.ViewModel.LastStockitsViewModel;
import com.indosoft.medibridge.ViewModel.MedicineViewModel;
import com.indosoft.medibridge.ViewModel.NotificationViewModel;
import com.indosoft.medibridge.ViewModel.RecentStockitsViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;
import com.indosoft.medibridge.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private ImageSlider imageSlider;
    ArrayList<UnitResponse> unitList = new ArrayList<>();
    ArrayList<MedicineListResponse> itemList = new ArrayList<>();
    ArrayList<RecentStockitsResponse> recentList = new ArrayList<>();
    UnitViewModel unitViewModel;
    MedicineViewModel medicineViewModel;
    AddtoCartViewModel cartViewModel;
    CityDealerViewModel cityDealerViewModel;
    LastStockitsViewModel lastStockitsViewModel;
    RecentStockitsViewModel recentStockitsViewModel;
    NotificationViewModel notificationViewModel;
    private ArrayList<NotificationResponse> list = new ArrayList<>();
    String selectDealerId;
    String selectUnitId;
    RecentStockitsAdapter adapter;
    private HashMap<String, String> productMap = new HashMap<>();
    private HashMap<String, String> dealerMap = new HashMap<>();
    private HashMap<String, String> unitNameToIdMap = new HashMap<>();
    int notificationCount;
    private boolean isReceiverRegistered = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentHomeBinding.inflate(inflater, container, false);
        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        medicineViewModel.init(getContext());
        recentStockitsViewModel = new ViewModelProvider(this).get(RecentStockitsViewModel.class);
        recentStockitsViewModel.init(getContext());
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.init(getContext());
        cartViewModel = new ViewModelProvider(this).get(AddtoCartViewModel.class);
        cartViewModel.init(requireContext());
        medicineViewModel.getMedicineData();
        imageSlider = binding.imageSlider;
        onAttachObservers();
        startNetworkCheckService();
        setUpImageSlider();
        handleRetailerName();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        initCliks();
        recentStockitsViewModel.recentStockits(AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID));
        adapter = new RecentStockitsAdapter(getContext(), recentList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.horizontalRecyclerView.setLayoutManager(layoutManager);
        binding.horizontalRecyclerView.setAdapter(adapter);


        cartViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                String cartCountStr = AppSession.getInstance(getContext()).getValue(Constants.CART_COUNT);
                int cartCount = (cartCountStr == null || cartCountStr.isEmpty()) ? 0 : Integer.parseInt(cartCountStr);
                cartCount++;
                AppSession.getInstance(getContext()).setValue(Constants.CART_COUNT, String.valueOf(cartCount));
                if (getActivity() instanceof DashBoardActivity) {
                    ((DashBoardActivity) getActivity()).updateCartBadge(cartCount);
                }

            }
        });
        return binding.getRoot();
    }
    private void setUpImageSlider() {
        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.b1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.b2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.b3, ScaleTypes.FIT));
        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP);
        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT);
    }

    private void handleRetailerName() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        if (retailerId != null) {
        } else {
            Log.d("Dashboard", "Retailer ID not available");
        }
        String updatedRetailerName = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_NAME);
        binding.txtRetailerName.setText("Welcome " + (updatedRetailerName != null ? updatedRetailerName : "Default Retailer"));
    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        medicineViewModel.getLiveData().observe(getViewLifecycleOwner(), medicineListResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (medicineListResponses != null && !medicineListResponses.isEmpty()) {
                itemList.clear();
                itemList.addAll(medicineListResponses);
                List<String> productNameList = new ArrayList<>();
                productMap.clear();
                for (MedicineListResponse response : medicineListResponses) {
                    if (response != null && response.getProductId() != null) {
                        productNameList.add(response.getProductName());
                        productMap.put(response.getProductName(), response.getProductId());
                        AppSession.getInstance(getContext()).setValue(Constants.PRODUCT_ID, response.getProductId());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_items, productNameList);
                binding.autoSearch.setAdapter(adapter);

            } else {
                Log.e("HomeFragment", "Medicine data is null or empty");
            }
        });
        recentStockitsViewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null && !responses.isEmpty()) {
                recentList.clear();
                recentList.addAll(responses);
                adapter.notifyDataSetChanged();
            } else {
                Log.d("HomeFragment", "No data or empty response.");
            }
        });
        notificationViewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null && !responses.isEmpty()) {
                notificationCount = responses.size();
                updateNotificationBadge(notificationCount);
                list.clear();
                list.addAll(responses);
            } else {
                updateNotificationBadge(0); // Hide the badge if no notifications
            }
        });
    }
    private void updateNotificationBadge(int notificationCount) {
        TextView notificationBadge = binding.notificationBadge;
        if (notificationCount > 0) {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(String.valueOf(notificationCount));
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }
    private void initCliks() {
        binding.autoSearch.setOnItemClickListener(this::onProductSelected);
        binding.cardViewAll.setOnClickListener(v -> launchActivity(ViewAllStockistActivity.class));
        binding.imgNotification.setOnClickListener(v -> launchActivity(NotificationActivity.class));
        binding.cardOrderRegister.setOnClickListener(v -> launchActivity(OrderRegisterActivity.class));
        binding.cardExpiryRegister.setOnClickListener(v -> launchActivity(ExpiryRegisterActivity.class));
        binding.cardUnlistedStockist.setOnClickListener(v -> launchActivity(UnlistedStockistActivity.class));
        binding.cardExpiryList.setOnClickListener(v -> launchActivity(ExpiryListActivity.class));
    }
    private void onProductSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedProductName = parent.getItemAtPosition(position).toString();
        String selectedProductId = productMap.get(selectedProductName);
        if (selectedProductId != null) {
            AppSession.getInstance(getContext()).setValue(Constants.PRODUCT_ID, selectedProductId);
            String supplierName = getSupplierNameForProduct(selectedProductId);
            showPopup(selectedProductName, supplierName);
            binding.autoSearch.setText("");
        }
    }
    private void launchActivity(Class<?> activityClass) {
        Intent intent = new Intent(getContext(), activityClass);
        startActivity(intent);
    }
    private String getSupplierNameForProduct(String productId) {
        for (MedicineListResponse medicine : itemList) {
            if (medicine.getProductId().equals(productId)) {
                return medicine.getSupplierName();
            }
        }
        return "Supplier not available";
    }
    private String getUnitForProduct(String productName) {
        for (MedicineListResponse medicine : itemList) {
            if (medicine.getProductName().equalsIgnoreCase(productName)) {
                return (String) medicine.getUnitName();
            }
        }
        return null;
    }
    private void showPopup(String selectedProductName, String supplierName) {
        unitViewModel = new ViewModelProvider(this).get(UnitViewModel.class);
        unitViewModel.init(requireContext());

        cityDealerViewModel = new ViewModelProvider(this).get(CityDealerViewModel.class);
        cityDealerViewModel.init(requireContext());
        lastStockitsViewModel = new ViewModelProvider(this).get(LastStockitsViewModel.class);
        lastStockitsViewModel.init(requireContext());
        unitViewModel.getUnits();
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.search_list_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        TextView txtItemDetails = popupView.findViewById(R.id.txt_medicineName);
        txtItemDetails.setText(selectedProductName);
        TextView companyNm = popupView.findViewById(R.id.txt_conpanyName);
        companyNm.setText(supplierName);
        TextView txtNumber = popupView.findViewById(R.id.txt_number);
        TextView txtUnitName = popupView.findViewById(R.id.txt_unitName);
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
                    AppSession.getInstance(getContext()).setValue(Constants.UNIT_ID, unit.getUnitId());
                }
                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, unitNames);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unitSpn.setAdapter(unitAdapter);
                String selectedUnitName = getUnitForProduct(selectedProductName);
                if (selectedUnitName != null) {
                    txtUnitName.setText(selectedUnitName);
                    selectUnitId = unitNameToIdMap.get(selectedUnitName);
                } else {
                    txtUnitName.setText("Unit not available");
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
        String currentProductId = AppSession.getInstance(getContext()).getValue(Constants.PRODUCT_ID);
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        lastStockitsViewModel.lastStockitsData(retailerId, currentProductId);
        lastStockitsViewModel.getLiveData().observe(getViewLifecycleOwner(), lastStockitsResponses -> {
            if (lastStockitsResponses != null) {
                for (LastStockitsResponse response : lastStockitsResponses) {
                    dealerName.setText(response.getDealerName());
                    selectDealerId = response.getDealerId();
                    AppSession.getInstance(getContext()).setValue(Constants.DEALER_ID, selectDealerId);
                }
            }
        });
        cityDealerViewModel.cityDealerData(AppSession.getInstance(getActivity()).getValue(Constants.CITY_ID));
        cityDealerViewModel.getLiveData().observe(getViewLifecycleOwner(), cityDealerResponses -> {
            if (cityDealerResponses != null) {
                List<String> stockitsList = new ArrayList<>();
                dealerMap.clear();
                for (CityDealerResponse response : cityDealerResponses) {
                    if (response != null && response.getDealerId() != null) {
                        stockitsList.add(response.getDealerName());
                        dealerMap.put(response.getDealerName(), response.getDealerId());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dealer_items, stockitsList);
                dealerName.setAdapter(adapter);
                dealerName.setOnClickListener(v -> {
                    dealerName.setText("");
                    if (dealerName.getText().toString().isEmpty()) {
                        selectDealerId = null;
                        AppSession.getInstance(getContext()).setValue(Constants.DEALER_ID, null);
                    }
                });
                dealerName.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedDealerName = parent.getItemAtPosition(position).toString();
                    selectDealerId = dealerMap.get(selectedDealerName);
                    AppSession.getInstance(requireContext()).setValue(Constants.DEALER_ID, selectDealerId);
                    dealerName.setText(selectedDealerName);
                });
            }
        });

        addCart.setOnClickListener(v -> {
            String product_id = AppSession.getInstance(requireContext()).getValue(Constants.PRODUCT_ID);
            String quantity = txtNumber.getText().toString();
            String dealerId = selectDealerId;
            String unitId = selectUnitId;
            if (dealerId == null || dealerId.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a dealer", Toast.LENGTH_SHORT).show();
                return;
            }
            if (product_id == null || product_id.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a product", Toast.LENGTH_SHORT).show();
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
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

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
        imgCancel.setOnClickListener(v -> dialog.dismiss());
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
    private void startNetworkCheckService() {
        Intent serviceIntent = new Intent(getContext(), NetworkCheckService.class);
        requireActivity().startService(serviceIntent);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {

                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        }
        return false;
    }
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                reloadData();
            } else {
            }
        }};
    @Override
    public void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            getActivity().registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            isReceiverRegistered = true;
        }
        binding.notificationBadge.setVisibility(View.GONE);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            getActivity().unregisterReceiver(networkReceiver);  // Unregister receiver only if registered
            isReceiverRegistered = false;
        }
    }private void reloadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 5000);
    }
}