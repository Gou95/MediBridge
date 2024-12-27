package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Listener.CartListener;
import com.indosoft.mediBridge.Listener.StateListener;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatesRepository {
    public static StatesRepository repository;

    private final MutableLiveData<List<IndiaStateResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static StatesRepository getInstance() {
        if (repository == null) {
            repository = new StatesRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public StatesRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<IndiaStateResponse>> getStattesData(Context context, StateListener listener) {
        Call<List<IndiaStateResponse>> call = apiInterface.stateList();
        call.enqueue(new Callback<List<IndiaStateResponse>>() {
            @Override
            public void onResponse(Call<List<IndiaStateResponse>> call, Response<List<IndiaStateResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<IndiaStateResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
