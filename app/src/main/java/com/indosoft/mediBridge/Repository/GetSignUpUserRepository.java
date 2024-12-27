package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Listener.CityListener;
import com.indosoft.mediBridge.Listener.GetSignUpUserListener;
import com.indosoft.mediBridge.Model.GetSignUpUserResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetSignUpUserRepository {

    public static GetSignUpUserRepository repository;

    private final MutableLiveData<List<GetSignUpUserResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static GetSignUpUserRepository getInstance() {
        if (repository == null) {
            repository = new GetSignUpUserRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public GetSignUpUserRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<GetSignUpUserResponse>> getAllUsersData(Context context, GetSignUpUserListener listener) {
        Call<List<GetSignUpUserResponse>> call = apiInterface.getUserList();
        call.enqueue(new Callback<List<GetSignUpUserResponse>>() {
            @Override
            public void onResponse(Call<List<GetSignUpUserResponse>> call, Response<List<GetSignUpUserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GetSignUpUserResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
