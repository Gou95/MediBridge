package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Body.AddtoCartBody;
import com.indosoft.MediBridges.Body.UserUpdateBody;
import com.indosoft.MediBridges.Listener.AddtoCartListener;
import com.indosoft.MediBridges.Listener.UserUpdateListener;
import com.indosoft.MediBridges.Model.AddtoCartResponse;
import com.indosoft.MediBridges.Model.UserUpdateResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserUpdateRepository {
    public static UserUpdateRepository repository;

    private final MutableLiveData<UserUpdateResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static UserUpdateRepository getInstance() {
        if (repository == null) {
            repository = new UserUpdateRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public UserUpdateRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<UserUpdateResponse> getUserUpdateData(Context context, UserUpdateBody body, UserUpdateListener listener) {
        Call<UserUpdateResponse> call = apiInterface.updateUsers(body);
        call.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}