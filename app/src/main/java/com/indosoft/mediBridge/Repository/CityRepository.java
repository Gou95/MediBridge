package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Listener.CityListener;
import com.indosoft.mediBridge.Listener.StateListener;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityRepository {
    public static CityRepository repository;

    private final MutableLiveData<List<StateCityResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static CityRepository getInstance() {
        if (repository == null) {
            repository = new CityRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CityRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<StateCityResponse>> getCitiesData(Context context,String state_id, CityListener listener) {
        Call<List<StateCityResponse>> call = apiInterface.cityList(state_id);
        call.enqueue(new Callback<List<StateCityResponse>>() {
            @Override
            public void onResponse(Call<List<StateCityResponse>> call, Response<List<StateCityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<StateCityResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
