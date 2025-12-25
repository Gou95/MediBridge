package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Model.CashMemoListResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.Listener.CashMemoListListener;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashMemoListRepository {
    public static CashMemoListRepository repository;

    private final MutableLiveData<List<CashMemoListResponse>> mutableLiveData = new MutableLiveData<>();

    // Singleton pattern
    public static CashMemoListRepository getInstance() {
        if (repository == null) {
            repository = new CashMemoListRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CashMemoListRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<CashMemoListResponse>> getMedicine(Context context,String retailer_id, CashMemoListListener listener) {
        Call<List<CashMemoListResponse>> call = apiInterface.getMedicine(retailer_id);
        call.enqueue(new Callback<List<CashMemoListResponse>>() {
            @Override
            public void onResponse(Call<List<CashMemoListResponse>> call, Response<List<CashMemoListResponse>> response) {
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
            public void onFailure(Call<List<CashMemoListResponse>> call, Throwable t) {
                // Handle failure (e.g., network issue)
                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
