package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Body.SignUpBody;
import com.indosoft.mediBridge.Listener.CityListener;
import com.indosoft.mediBridge.Listener.SignUpListener;
import com.indosoft.mediBridge.Model.SignUpResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpRepository {
    public static SignUpRepository repository;

    private final MutableLiveData<SignUpResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static SignUpRepository getInstance() {
        if (repository == null) {
            repository = new SignUpRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public SignUpRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<SignUpResponse> getSignUpData(Context context,SignUpBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.getRegisterRes(body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
