package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Body.SignUpBody;
import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Listener.SignUpListener;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Model.SignUpResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    public static OrderRepository repository;

    private final MutableLiveData<OrderResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static OrderRepository getInstance() {
        if (repository == null) {
            repository = new OrderRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public OrderRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<OrderResponse> orderResData(Context context, String retailer_id,String order_no, OrderListener listener) {
        Call<OrderResponse> call = apiInterface.orderRes(retailer_id,order_no);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
