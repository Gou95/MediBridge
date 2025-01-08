package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.DealerListener;
import com.indosoft.MediBridges.Listener.LoginListener;
import com.indosoft.MediBridges.Model.DealersResponse;
import com.indosoft.MediBridges.Model.LoginResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    public static LoginRepository repository;

    private final MutableLiveData<List<LoginResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static LoginRepository getInstance() {
        if (repository == null) {
            repository = new LoginRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public LoginRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<LoginResponse>> getLoginData(Context context,String retailer_phone,String retailer_password, LoginListener listener) {
        Call<List<LoginResponse>> call = apiInterface.loginRes(retailer_phone,retailer_password);
        call.enqueue(new Callback<List<LoginResponse>>() {
            @Override
            public void onResponse(Call<List<LoginResponse>> call, Response<List<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LoginResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
