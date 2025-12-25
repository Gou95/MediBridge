package com.indosoft.medibridge.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.indosoft.medibridge.Adapter.CashMemoAdapter;
import com.indosoft.medibridge.Adapter.PosListAdapter;
import com.indosoft.medibridge.Adapter.SearchAdapter;
import com.indosoft.medibridge.Body.AddBillBody;
import com.indosoft.medibridge.Body.CashMemoAddBody;
import com.indosoft.medibridge.Body.PosAddBody;
import com.indosoft.medibridge.Model.CashMemoListResponse;
import com.indosoft.medibridge.Model.CashMemoPdfResponse;
import com.indosoft.medibridge.Model.GetPosProductResponse;
import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CashMemoListViewModel;
import com.indosoft.medibridge.ViewModel.CashMemoPdfViewModel;
import com.indosoft.medibridge.ViewModel.MedicineViewModel;
import com.indosoft.medibridge.ViewModel.PosProductViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;
import com.indosoft.medibridge.databinding.ActivityCashMemoBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CashMemoActivity extends AppCompatActivity {
    ActivityCashMemoBinding binding;
    MedicineViewModel medicineViewModel;
    UnitViewModel unitViewModel;
    SignUpViewModel sign;
    CashMemoListViewModel cashMemoListViewModel;
    CashMemoPdfViewModel cashMemoPdfViewModel;
    CashMemoAdapter adapter;
    ArrayList<UnitResponse> unitList = new ArrayList<>();
    ArrayList<CashMemoPdfResponse> pdfList = new ArrayList<>();
    ArrayList<CashMemoListResponse> list = new ArrayList<>();
    ArrayList<MedicineListResponse.Datum> itemList = new ArrayList<>();
  //  private HashMap<String, Integer> productMap = new HashMap<>();
  HashMap<String, MedicineListResponse.Datum> productMap = new HashMap<>();
    // ProductId -> Rate
    private HashMap<String, String> productRateMap = new HashMap<>();
    private HashMap<String, String> unitNameToIdMap = new HashMap<>();
   SearchAdapter searchAdapter;
    private ArrayList<String> fullProductNameList = new ArrayList<>();
    String retailerId;
    String selectUnitId;
    private boolean isMedicineLoaded = false;
    private String currentQuery = "";
    private boolean isReceiverRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCashMemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        medicineViewModel.init(this);
        medicineViewModel.searchMedicine("");
        cashMemoListViewModel = new ViewModelProvider(this).get(CashMemoListViewModel.class);
        cashMemoListViewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        cashMemoPdfViewModel = new ViewModelProvider(this).get(CashMemoPdfViewModel.class);
        cashMemoPdfViewModel.init(this);
         retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        cashMemoListViewModel.getMedicine(retailerId);
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);
        adapter = new CashMemoAdapter(this,list,sign,()->{
            cashMemoListViewModel.getMedicine(retailerId);
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initClicks();
        onAttachObservers();
        binding.edtDocterNm.setText("Dr. ");
        binding.edtDocterNm.setSelection(binding.edtDocterNm.getText().length());

        searchAdapter = new SearchAdapter(item -> {

            binding.etSearch.setText("");
            binding.rvSearch.setVisibility(View.GONE);

            // ✅ Product ID
            AppSession.getInstance(this)
                    .setValue(Constants.PRODUCT_ID, String.valueOf(item.getProductId()));

            // ✅ Unit ID & Name (IMPORTANT)
            AppSession.getInstance(this)
                    .setValue(Constants.UNIT_ID, String.valueOf(item.getUnitId()));

            AppSession.getInstance(this)
                    .setValue(Constants.UNIT_NAME, item.getUnitName());

            showPopup(item.getProductName(), item.getSupplierName());
        });


        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearch.setAdapter(searchAdapter);

    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        medicineViewModel.getLiveData().observe(this, list -> {

            // ✅ Agar user ne search hi nahi ki
            if (currentQuery == null || currentQuery.isEmpty()) {
                binding.rvSearch.setVisibility(View.GONE);
                return;
            }

            if (list == null || list.isEmpty()) {
                binding.rvSearch.setVisibility(View.GONE);
                return;
            }

            searchAdapter.submitList(list);
            binding.rvSearch.setVisibility(View.VISIBLE);
        });
        cashMemoListViewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            list.clear();
            if (responses != null) {
                String selectedDate = binding.edtDate.getText().toString();  // yyyy-MM-dd
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat onlyDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                for (CashMemoListResponse item : responses) {
                    try {
                        if (item.getAddtime() != null) {
                            Date apiDate = apiFormat.parse(item.getAddtime()); // yyyy-MM-dd HH:mm:ss
                            String itemDate = onlyDateFormat.format(apiDate);  // yyyy-MM-dd

                            if (itemDate.equals(selectedDate)) {
                                list.add(item);
                            }
                            else {
                            }
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                adapter.notifyDataSetChanged();
                calculateRawTotalAmount();
            }else {
            }
        });
        cashMemoPdfViewModel.getLiveData().observe(this, responses -> {
            if (responses != null && !responses.isEmpty()) {
                pdfList.clear();
                pdfList.addAll(responses);
                generateMedicalBillPdf(this);

                for (CashMemoPdfResponse response : responses){
                    AppSession.getInstance(this).setValue(Constants.SALE_ID,response.getSaleId());
                }
            } else {
                Toast.makeText(this, "No bill data found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initClicks() {

        binding.etSearch.addTextChangedListener(new TextWatcher() {

            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

                currentQuery = s.toString().trim();   // ✅ IMPORTANT

                if (currentQuery.isEmpty()) {
                    binding.rvSearch.setVisibility(View.GONE);
                    searchAdapter.submitList(new ArrayList<>());
                    return;
                }

                runnable = () -> medicineViewModel.searchMedicine(currentQuery);
                handler.postDelayed(runnable, 300);
            }
        });
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.edtDate.setOnClickListener(v -> {
            openCalendarDialog();
        });

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(cal.getTime());
        binding.edtDate.setText(formattedDate);
        binding.btnPdf.setOnClickListener(v -> {
          cashMemoPdfViewModel.getPdf(AppSession.getInstance(this).getValue(Constants.SALE_ID)); // <-- Trigger API call
        });
        binding.btnReset.setOnClickListener(v -> {
            binding.edtPatient.setText("");
            binding.edtDocterNm.setText("");
            list.clear();
            adapter.notifyDataSetChanged();

        });
        binding.btnAddBill.setOnClickListener(v -> {
            String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
            String date = binding.edtDate.getText().toString().trim();
            String doctor = binding.edtDocterNm.getText().toString().trim();
            String patient = binding.edtPatient.getText().toString().trim();
            String billAmt = String.format(Locale.getDefault(), "%.2f", calculateRawTotalAmount());

            if (date.isEmpty() || doctor.isEmpty() || patient.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            } else {
                AddBillBody body = new AddBillBody();
                body.setBillDate(date);
                body.setDoctorName(doctor);
                body.setPatientName(patient);
                body.setBillAmount(billAmt);
                sign.addBill(retailerId, body);

                sign.getLiveData().observe(this,signUpResponse -> {
                    if (signUpResponse!=null){
                        AppSession.getInstance(this).setValue(Constants.SALE_ID,signUpResponse.getMessage());
                        binding.edtDate.setText("");
                        binding.edtPatient.setText("");
                        binding.edtDocterNm.setText("");
                    } });
            }

        });

        binding.imgVoice.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US"); // ya "hi-IN" agar Hindi chahiye
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak medicine name...");
            try {
                startActivityForResult(intent, 1001);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.edtDocterNm.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String input = s.toString();

                // Agar user Dr. hata de to wapas laga do
                if (!input.startsWith("Dr. ")) {
                    input = input.replace("Dr.", "").replace("dr.", "").trim();
                    input = capitalizeFirstLetter(input);
                    input = "Dr. " + input;
                } else {
                    // Sirf naam ka first letter capital rakho
                    String namePart = input.replace("Dr. ", "").trim();
                    namePart = capitalizeFirstLetter(namePart);
                    input = "Dr. " + namePart;
                }

                binding.edtDocterNm.setText(input);
                binding.edtDocterNm.setSelection(input.length()); // cursor end pe

                isEditing = false;
            }
        });

        binding.edtPatient.addTextChangedListener(new TextWatcher() {
            private boolean isEditing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String input = s.toString().trim();
                String formatted = capitalizeFirstLetter(input);

                binding.edtPatient.setText(formatted);
                binding.edtPatient.setSelection(formatted.length());

                isEditing = false;
            }
        });


    }


    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private double calculateRawTotalAmount() {
        double totalAmount = 0.0;
        for (CashMemoListResponse item : list) {
            try {
                if (item.getAmount() != null && !item.getAmount().isEmpty()) {
                    totalAmount += Double.parseDouble(item.getAmount());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
         binding.txtTotalCashMemo.setText("Total Cash memo"+" \n"+"₹ " + String.format(Locale.getDefault(), "%.2f", totalAmount));
        return totalAmount;
    }
    private void openCalendarDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View calendarView = inflater.inflate(R.layout.custom_calendar, null);

        CalendarView calendar = calendarView.findViewById(R.id.calendarView);
        Button btnClose = calendarView.findViewById(R.id.btnClose);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(calendarView)
                .create();
        dialog.show();

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(cal.getTime());
            binding.edtDate.setText(formattedDate);
            dialog.dismiss();

        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }
    private void showPopup(String selectedProductName, String supplierName) {
        unitViewModel = new ViewModelProvider(this).get(UnitViewModel.class);
        unitViewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        unitViewModel.getUnits();
        String savedUnitId = AppSession.getInstance(this).getValue(Constants.UNIT_ID);

        String savedUnitName = AppSession.getInstance(this).getValue(Constants.UNIT_NAME);
        View popupView = LayoutInflater.from(this).inflate(R.layout.cash_memo_details, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtItemDetails = popupView.findViewById(R.id.txt_posMediName);
        txtItemDetails.setText(selectedProductName);
        TextView companyNm = popupView.findViewById(R.id.txt_posCompanyName);
        companyNm.setText(supplierName);
        TextView txtNumber = popupView.findViewById(R.id.txt_number);
        TextView txtUnitName = popupView.findViewById(R.id.txt_posUnitName);
        ImageView imgSub = popupView.findViewById(R.id.img_sub);
        ImageView imgAdd = popupView.findViewById(R.id.img_add);
        ImageView imgCancel = popupView.findViewById(R.id.img_cancle);
        CardView addCart = popupView.findViewById(R.id.btn_addCart);
        Spinner unitSpn = popupView.findViewById(R.id.spin_unit);
        EditText amount = popupView.findViewById(R.id.edt_cashAmount);
        EditText batchNo = popupView.findViewById(R.id.edt_cashBatch);
        EditText expiry = popupView.findViewById(R.id.edt_cashExpiry);
        EditText rate = popupView.findViewById(R.id.edt_cashRate);
        String productId = AppSession.getInstance(this).getValue(Constants.PRODUCT_ID);
        if (productRateMap.containsKey(productId)) {
            rate.setText(productRateMap.get(productId));
        }

        unitViewModel.getLiveData().observe(this, unitResponses -> {

            if (unitResponses == null || unitResponses.isEmpty()) return;

            unitList.clear();
            unitList.addAll(unitResponses);

            List<String> unitNames = new ArrayList<>();
            unitNameToIdMap.clear();

            for (UnitResponse unit : unitList) {
                unitNames.add(unit.getUnitName());
                unitNameToIdMap.put(unit.getUnitName(), unit.getUnitId());
            }

            ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    unitNames
            );
            unitAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);

            unitSpn.setAdapter(unitAdapter);

            // ✅ AUTO SELECT UNIT FROM SESSION
            if (savedUnitId != null) {
                for (int i = 0; i < unitList.size(); i++) {
                    if (unitList.get(i).getUnitId().equals(savedUnitId)) {
                        unitSpn.setSelection(i);
                        txtUnitName.setText(savedUnitName);
                        selectUnitId = savedUnitId;
                        break;
                    }
                }
                unitSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectUnitId = unitList.get(position).getUnitId();
                        txtUnitName.setText(unitList.get(position).getUnitName());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
                txtUnitName.setOnClickListener(v -> {
                    txtUnitName.setVisibility(View.GONE);
                    unitSpn.setVisibility(View.VISIBLE);
                    unitSpn.performClick();
                });
            }
        });
        sign.getLiveData().observe(this,signUpResponse -> {
            if (signUpResponse !=null){
                cashMemoListViewModel.getMedicine(retailerId);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }            }
        });
        expiry.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.calendar_view, null);
            builder1.setView(dialogView);

            final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
            int daySpinnerId = getResources().getIdentifier("android:id/day", null, null);
            if (daySpinnerId != 0) {
                View daySpinner = datePicker.findViewById(daySpinnerId);
                if (daySpinner != null) {
                    daySpinner.setVisibility(View.GONE);
                }
            }
            TextView btnCancel = dialogView.findViewById(R.id.txt_cancel);
            TextView btnOk = dialogView.findViewById(R.id.txt_ok);
            final AlertDialog dialog1 = builder1.create(); // 👈 Second popup
            btnCancel.setOnClickListener(v2 -> dialog1.dismiss());
            btnOk.setOnClickListener(v2 -> {
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                String selectedMonthYear = month + "/" + year;
                expiry.setText(selectedMonthYear);
                dialog1.dismiss(); // ✅ Dismiss only the second popup
            });

            dialog1.show(); // ✅ Show the inner popup
        });

        addCart.setOnClickListener(v -> {
            String product_id = AppSession.getInstance(this).getValue(Constants.PRODUCT_ID);
            String quantity = txtNumber.getText().toString();
            String amt = amount.getText().toString();
            String expiry_date = expiry.getText().toString();
            String batch = batchNo.getText().toString();
            String unitId = selectUnitId;

            if (product_id == null || product_id.isEmpty()) {
                Toast.makeText(this, "Please select a product", Toast.LENGTH_SHORT).show();
                return;
            }
            if (unitId == null || unitId.isEmpty()) {
                Toast.makeText(this, "Please select a unit", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity == null || quantity.isEmpty() || Integer.parseInt(quantity) <= 0) {
                Toast.makeText(this, "Please select a valid quantity greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
            if (expiry_date == null || expiry_date.isEmpty()) {
                Toast.makeText(this, "enter expire date", Toast.LENGTH_SHORT).show();
                return;
            }
            if (batch == null || batch.isEmpty() ) {
                Toast.makeText(this, "enter batch no", Toast.LENGTH_SHORT).show();
                return;
            }
            String rateValue = rate.getText().toString().trim();

            if (rateValue.isEmpty()) {
                Toast.makeText(this, "Please enter rate", Toast.LENGTH_SHORT).show();
                return;
            }
            productRateMap.put(product_id, rateValue);
            CashMemoAddBody body = new CashMemoAddBody();
            body.setProductId(product_id);
            body.setUnitId(unitId);
            body.setQty(quantity);
            body.setAmount(amt);
            body.setBatchNo(batch);
            body.setExpiryDate(expiry_date);
            body.setRate(rateValue);   // ✅ this must not be null or empty

            Log.d("POS_BODY", new Gson().toJson(body));  // ✅ check output

            sign.cashMemoAdd(retailerId, body);

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
        Runnable calculateAmount = () -> {
            try {
                int qty = Integer.parseInt(txtNumber.getText().toString());
                double rateVal = Double.parseDouble(rate.getText().toString());
                double total = qty * rateVal;
                amount.setText(String.valueOf(total));
            } catch (Exception e) {
                amount.setText("0");
            }
        };
        imgSub.setOnClickListener(v -> {
            if (number.get() > 1) {
                number.getAndDecrement();
                txtNumber.setText(String.valueOf(number.get()));
                calculateAmount.run();
            }
        });
        imgAdd.setOnClickListener(v -> {
            number.getAndIncrement();
            txtNumber.setText(String.valueOf(number.get()));
            calculateAmount.run();
        });
        rate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                calculateAmount.run(); // update when rate changes
            }
        });

        calculateAmount.run();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0) {
                String spokenText = result.get(0); // user jo bola


            }
        }
    }
    private void generateMedicalBillPdf(Context context) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 850, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // ==== Outer Border ====
        int margin = 20;
        int boxLeft = margin;
        int boxTop = margin;
        int boxRight = pageInfo.getPageWidth() - margin;
        int boxBottom = pageInfo.getPageHeight() - margin;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(boxLeft, boxTop, boxRight, boxBottom, paint);
        paint.setStyle(Paint.Style.FILL);

        // ==== Header Section ====
        CashMemoPdfResponse firstItem = pdfList.get(0);
        String billNo = firstItem.getBillNo();
        String billDate = firstItem.getBillDate();
        String patientName = firstItem.getPatientName();
        String doctorName = firstItem.getDoctorName();

        int headerY = boxTop + 30;

// Cash Memo (Center)
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTextSize(22);
        titlePaint.setFakeBoldText(true);
        canvas.drawText("Cash Memo", pageInfo.getPageWidth() / 2, headerY, titlePaint);

// DL Number (Left)
        Paint headerLeftPaint = new Paint();
        headerLeftPaint.setTextSize(14);
        headerLeftPaint.setFakeBoldText(false);
        headerLeftPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("DL No: " + AppSession.getInstance(this).getValue(Constants.RETAILER_DL),
                boxLeft + 10, headerY, headerLeftPaint);

// GST Number (Right)
        Paint headerRightPaint = new Paint();
        headerRightPaint.setTextSize(14);
        headerRightPaint.setFakeBoldText(false);
        headerRightPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("GST No: " + AppSession.getInstance(this).getValue(Constants.RETAILER_GST),
                boxRight - 10, headerY, headerRightPaint);

// Retailer Name (Next Line - Center)
        titlePaint.setTextSize(18);
        titlePaint.setFakeBoldText(true);
        canvas.drawText(AppSession.getInstance(this).getValue(Constants.RELAILER_NAME),
                pageInfo.getPageWidth() / 2, boxTop + 55, titlePaint);

        paint.setTextSize(12);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(AppSession.getInstance(this).getValue(Constants.PERMANENTADDRESS),
                pageInfo.getPageWidth() / 2, boxTop + 75, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("No: " + billNo, boxLeft + 10, boxTop + 95, paint);
        canvas.drawText("Date: " + billDate, boxRight - 160, boxTop + 95, paint);
        canvas.drawText("Patient Name: " + patientName, boxLeft + 10, boxTop + 115, paint);
        canvas.drawText("Doctor Name: " + doctorName, boxRight - 160, boxTop + 115, paint);

        // ==== Table Header ====
        int tableLeft = boxLeft + 10;
        int tableTop = boxTop + 150;

        int[] colWidths = {40, 120, 80, 80, 60, 80, 80};
        String[] headers = {"S.No.", "Particulars", "Batch No", "Expiry", "Qty", "Rate", "Amount"};

        int tableRight = tableLeft;
        for (int width : colWidths) tableRight += width;

        int headerHeight = 30;
        int y = tableTop;

        // Header Row Box
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(tableLeft, y, tableRight, y + headerHeight, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(12);
        paint.setFakeBoldText(true);

        int x = tableLeft;
        for (int i = 0; i < headers.length; i++) {
            canvas.drawText(headers[i], x + 5, y + 20, paint);
            x += colWidths[i];
            canvas.drawLine(x, y, x, y + headerHeight, paint); // vertical lines
        }
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1);
        borderPaint.setColor(Color.BLACK);

        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);
        textPaint.setFakeBoldText(false);
        // ==== Table Rows ====
        paint.setFakeBoldText(false);
        y += headerHeight;
        double grandTotal = 0.0;

        for (int i = 0; i < pdfList.size(); i++) {
            CashMemoPdfResponse item = pdfList.get(i);

            String sno = String.valueOf(i + 1);
            String name = item.getProductName();
            String batch = item.getBatchNo();
            String expiry = item.getExpiryDate();
            String qty = item.getQty();
            String rate = item.getRate() == null ? "0" : item.getRate().toString();
            String amt = item.getAmount() == null ? "0" : item.getAmount();

            try {
                grandTotal += Double.parseDouble(amt);
            } catch (Exception ignored) {}


            TextPaint tp = new TextPaint();
            tp.setTextSize(12);
            tp.setColor(Color.BLACK);

            int particularsColWidth = colWidths[1] - 10; // width of "Particulars" column
            StaticLayout staticLayout = new StaticLayout(
                    name,
                    tp,
                    particularsColWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
            );

            int nameHeight = staticLayout.getHeight();
            int rowHeight = Math.max(25, nameHeight + 10); // dynamic row height

            // Draw row border
            canvas.drawRect(tableLeft, y, tableRight, y + rowHeight, borderPaint);

            int colX = tableLeft;
            canvas.drawText(sno, colX + 5, y + 18, textPaint);
            colX += colWidths[0];

            // Draw product name multiline
            canvas.save();
            canvas.translate(colX + 5, y + 5);
            staticLayout.draw(canvas);
            canvas.restore();
            colX += colWidths[1];

            // Other columns
            canvas.drawText(batch, colX + 5, y + 18, textPaint);
            colX += colWidths[2];

            canvas.drawText(expiry, colX + 5, y + 18, textPaint);
            colX += colWidths[3];

            canvas.drawText(qty, colX + 5, y + 18, textPaint);
            colX += colWidths[4];

            canvas.drawText(rate, colX + 5, y + 18, textPaint); // <-- NEW
            colX += colWidths[5];

            canvas.drawText(amt, colX + 5, y + 18, textPaint);

            // Update Y for next row
            y += rowHeight;
        }

        // ==== Total Row ====
        int totalRowHeight = 30;
        canvas.drawRect(tableLeft, y, tableRight, y + totalRowHeight, borderPaint);

        textPaint.setFakeBoldText(true);
        canvas.drawText("Total:", tableRight - 120, y + 20, textPaint);
        canvas.drawText("₹" + String.format("%.2f", grandTotal), tableRight - 60, y + 20, textPaint);

        y += totalRowHeight + 40;

        paint.setFakeBoldText(false);
        paint.setTextSize(12);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Terms & Conditions", tableLeft, y, paint);

        // ==== Signature ====
        paint.setFakeBoldText(false);
        canvas.drawText("Signature", tableRight - 100, y, paint);

        // ==== End Page ====
        pdfDocument.finishPage(page);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "CashMemo_" + timeStamp + ".pdf";
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPdf(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        pdfDocument.close();
    }
    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_LONG).show();
        }
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
              //  hideNoConnectionView();
                reloadData();
            } else {
//                showNoConnectionView();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, filter);
            isReceiverRegistered = true;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(networkReceiver);
            isReceiverRegistered = false;
        }
    }
    private void reloadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 5000);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    protected void onStart() {
        super.onStart();

    }
}