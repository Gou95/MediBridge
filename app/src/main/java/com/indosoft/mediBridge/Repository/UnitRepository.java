package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Body.SignUpBody;
import com.indosoft.mediBridge.Listener.SignUpListener;
import com.indosoft.mediBridge.Listener.StateListener;
import com.indosoft.mediBridge.Listener.UnitListener;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.SignUpResponse;
import com.indosoft.mediBridge.Model.UnitResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnitRepository {
    public static UnitRepository repository;

    private final MutableLiveData<List<UnitResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static UnitRepository getInstance() {
        if (repository == null) {
            repository = new UnitRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public UnitRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<UnitResponse>> getUnitData(Context context, UnitListener listener) {
        Call<List<UnitResponse>> call = apiInterface.unitList();
        call.enqueue(new Callback<List<UnitResponse>>() {
            @Override
            public void onResponse(Call<List<UnitResponse>> call, Response<List<UnitResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UnitResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
