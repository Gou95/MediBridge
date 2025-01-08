package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Body.SignUpBody;
import com.indosoft.MediBridges.Listener.DeliveryDayListener;
import com.indosoft.MediBridges.Listener.SignUpListener;
import com.indosoft.MediBridges.Model.DeliveryDayResponse;
import com.indosoft.MediBridges.Model.SignUpResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryDayRepository {

    public static DeliveryDayRepository repository;

    private final MutableLiveData<DeliveryDayResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static DeliveryDayRepository getInstance() {
        if (repository == null) {
            repository = new DeliveryDayRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public DeliveryDayRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<DeliveryDayResponse> updateDay(Context context, String cart_id,String delivery_day, DeliveryDayListener listener) {
        Call<DeliveryDayResponse> call = apiInterface.deliveryDay(cart_id,delivery_day);
        call.enqueue(new Callback<DeliveryDayResponse>() {
            @Override
            public void onResponse(Call<DeliveryDayResponse> call, Response<DeliveryDayResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<DeliveryDayResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
