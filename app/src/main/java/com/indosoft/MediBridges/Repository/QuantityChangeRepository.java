package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Listener.QuantityChangeListener;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Model.QuantityChangeResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuantityChangeRepository {

    public static QuantityChangeRepository repository;

    private final MutableLiveData<QuantityChangeResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static QuantityChangeRepository getInstance() {
        if (repository == null) {
            repository = new QuantityChangeRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public QuantityChangeRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<QuantityChangeResponse> getQtyChangeData(Context context, String cart_id, String product_id,String qty, QuantityChangeListener listener) {
        Call<QuantityChangeResponse> call = apiInterface.qtyChange(cart_id,product_id,qty);
        call.enqueue(new Callback<QuantityChangeResponse>() {
            @Override
            public void onResponse(Call<QuantityChangeResponse> call, Response<QuantityChangeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<QuantityChangeResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
