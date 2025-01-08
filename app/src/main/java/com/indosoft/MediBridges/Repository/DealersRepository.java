package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.CityListener;
import com.indosoft.MediBridges.Listener.DealerListener;
import com.indosoft.MediBridges.Model.DealersResponse;
import com.indosoft.MediBridges.Model.StateCityResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DealersRepository {
    public static DealersRepository repository;

    private final MutableLiveData<List<DealersResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static DealersRepository getInstance() {
        if (repository == null) {
            repository = new DealersRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public DealersRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<DealersResponse>> getDealersData(Context context, DealerListener listener) {
        Call<List<DealersResponse>> call = apiInterface.dealersList();
        call.enqueue(new Callback<List<DealersResponse>>() {
            @Override
            public void onResponse(Call<List<DealersResponse>> call, Response<List<DealersResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DealersResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
