package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.CashMemoPdfListener;
import com.indosoft.medibridge.Model.CashMemoPdfResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashMemoPdfRepository {
    public static CashMemoPdfRepository repository;

    private final MutableLiveData<List<CashMemoPdfResponse>> mutableLiveData = new MutableLiveData<>();

    // Singleton pattern
    public static CashMemoPdfRepository getInstance() {
        if (repository == null) {
            repository = new CashMemoPdfRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CashMemoPdfRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<CashMemoPdfResponse>> getPdf(Context context, String sale_id, CashMemoPdfListener listener) {
        Call<List<CashMemoPdfResponse>> call = apiInterface.generatePdf(sale_id);
        call.enqueue(new Callback<List<CashMemoPdfResponse>>() {
            @Override
            public void onResponse(Call<List<CashMemoPdfResponse>> call, Response<List<CashMemoPdfResponse>> response) {
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
            public void onFailure(Call<List<CashMemoPdfResponse>> call, Throwable t) {
                // Handle failure (e.g., network issue)
                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }
}
