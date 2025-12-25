package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.CashMemoDetailsListener;
import com.indosoft.medibridge.Listener.CashMemoListListener;
import com.indosoft.medibridge.Model.CashMemoListResponse;
import com.indosoft.medibridge.Model.CashMemodetailsResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashMemoDetailsRepository {
    public static CashMemoDetailsRepository repository;

    private final MutableLiveData<List<CashMemodetailsResponse>> mutableLiveData = new MutableLiveData<>();

    // Singleton pattern
    public static CashMemoDetailsRepository getInstance() {
        if (repository == null) {
            repository = new CashMemoDetailsRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CashMemoDetailsRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<CashMemodetailsResponse>> getCashMemoDetails(Context context, String retailer_id, CashMemoDetailsListener listener) {
        Call<List<CashMemodetailsResponse>> call = apiInterface.getCashMemoDetails(retailer_id);
        call.enqueue(new Callback<List<CashMemodetailsResponse>>() {
            @Override
            public void onResponse(Call<List<CashMemodetailsResponse>> call, Response<List<CashMemodetailsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());
                    mutableLiveData.setValue(response.body());
                } else {
                    // Handle unsuccessful response (e.g., server error)
                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CashMemodetailsResponse>> call, Throwable t) {
                // Handle failure (e.g., network issue)
                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }
}
