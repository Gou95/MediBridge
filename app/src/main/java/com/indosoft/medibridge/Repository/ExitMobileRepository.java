package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Body.ExitMobileBody;
import com.indosoft.medibridge.Listener.ExitMobileListener;
import com.indosoft.medibridge.Model.ExitMobileResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExitMobileRepository {
    public static ExitMobileRepository repository;

    private final MutableLiveData<ExitMobileResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static ExitMobileRepository getInstance() {
        if (repository == null) {
            repository = new ExitMobileRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public ExitMobileRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<ExitMobileResponse> getExitMobile(Context context, ExitMobileBody body, ExitMobileListener listener) {
        Call<ExitMobileResponse> call = apiInterface.exitMobile(body);
        call.enqueue(new Callback<ExitMobileResponse>() {
            @Override
            public void onResponse(Call<ExitMobileResponse> call, Response<ExitMobileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<ExitMobileResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
