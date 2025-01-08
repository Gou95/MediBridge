package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.MedicineListListener;
import com.indosoft.MediBridges.Listener.OrderListListener;
import com.indosoft.MediBridges.Model.MedicineListResponse;
import com.indosoft.MediBridges.Model.OrderListResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListRepository {

    public static OrderListRepository repository;

    private final MutableLiveData<List<OrderListResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static OrderListRepository getInstance() {
        if (repository == null) {
            repository = new OrderListRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public OrderListRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<OrderListResponse>> getOrderListData(Context context,String retailer_id, OrderListListener listener) {
        Call<List<OrderListResponse>> call = apiInterface.orderList(retailer_id);
        call.enqueue(new Callback<List<OrderListResponse>>() {
            @Override
            public void onResponse(Call<List<OrderListResponse>> call, Response<List<OrderListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderListResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }


}
