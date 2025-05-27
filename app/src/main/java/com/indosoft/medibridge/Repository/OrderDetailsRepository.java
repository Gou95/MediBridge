package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.OrderDetailListerner;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsRepository {

    public static OrderDetailsRepository repository;

    private final MutableLiveData<List<OrderDetailsResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static OrderDetailsRepository getInstance() {
        if (repository == null) {
            repository = new OrderDetailsRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public OrderDetailsRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<OrderDetailsResponse>> orderDetailsData(Context context, String retailer_id, OrderDetailListerner listener) {
        Call<List<OrderDetailsResponse>> call = apiInterface.getOrderDetails(retailer_id);
        call.enqueue(new Callback<List<OrderDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<OrderDetailsResponse>> call, Response<List<OrderDetailsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<OrderDetailsResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
    public MutableLiveData<List<OrderDetailsResponse>> stockistDetailsData(Context context, String retailer_id,String order_no,String dealer_id,String order_status, OrderDetailListerner listener) {
        Call<List<OrderDetailsResponse>> call = apiInterface.stockisOrderDetails(retailer_id,order_no,dealer_id,order_status);
        call.enqueue(new Callback<List<OrderDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<OrderDetailsResponse>> call, Response<List<OrderDetailsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<OrderDetailsResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
