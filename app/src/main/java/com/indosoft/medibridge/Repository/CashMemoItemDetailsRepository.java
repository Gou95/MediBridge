package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.CashMemoDetailsListener;
import com.indosoft.medibridge.Listener.CashMemoItemDetailsListener;
import com.indosoft.medibridge.Model.CashMemoItemDetailsResponse;
import com.indosoft.medibridge.Model.CashMemodetailsResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashMemoItemDetailsRepository {
    public static CashMemoItemDetailsRepository repository;

    private final MutableLiveData<List<CashMemoItemDetailsResponse>> mutableLiveData = new MutableLiveData<>();

    // Singleton pattern
    public static CashMemoItemDetailsRepository getInstance() {
        if (repository == null) {
            repository = new CashMemoItemDetailsRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CashMemoItemDetailsRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<CashMemoItemDetailsResponse>> getItemsList(Context context, String retailer_id,String bill_no, CashMemoItemDetailsListener listener) {
        Call<List<CashMemoItemDetailsResponse>> call = apiInterface.getItemsList(retailer_id,bill_no);
        call.enqueue(new Callback<List<CashMemoItemDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<CashMemoItemDetailsResponse>> call, Response<List<CashMemoItemDetailsResponse>> response) {
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
            public void onFailure(Call<List<CashMemoItemDetailsResponse>> call, Throwable t) {
                // Handle failure (e.g., network issue)
                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }
}
