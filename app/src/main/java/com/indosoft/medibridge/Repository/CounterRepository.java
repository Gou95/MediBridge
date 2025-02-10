package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.CounterListener;
import com.indosoft.medibridge.Listener.OtpListener;
import com.indosoft.medibridge.Model.CounterResponse;
import com.indosoft.medibridge.Model.OtpResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CounterRepository {
    public static CounterRepository repository;

    private final MutableLiveData<CounterResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static CounterRepository getInstance() {
        if (repository == null) {
            repository = new CounterRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CounterRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<CounterResponse> getCount(Context context, String retailer_id, CounterListener listener) {
        Call<CounterResponse> call = apiInterface.getCounter(retailer_id);
        call.enqueue(new Callback<CounterResponse>() {
            @Override
            public void onResponse(Call<CounterResponse> call, Response<CounterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<CounterResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
