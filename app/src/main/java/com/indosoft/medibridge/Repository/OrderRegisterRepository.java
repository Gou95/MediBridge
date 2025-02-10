package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.OrderRegisterListener;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRegisterRepository {
    public static OrderRegisterRepository repository;

    private final MutableLiveData<List<OrderRegisterResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static OrderRegisterRepository getInstance() {
        if (repository == null) {
            repository = new OrderRegisterRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public OrderRegisterRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<OrderRegisterResponse>> getRegisterList(Context context, String retailer_id, OrderRegisterListener listener) {
        Call<List<OrderRegisterResponse>> call = apiInterface.orderRegister(retailer_id);
        call.enqueue(new Callback<List<OrderRegisterResponse>>() {
            @Override
            public void onResponse(Call<List<OrderRegisterResponse>> call, Response<List<OrderRegisterResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderRegisterResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
