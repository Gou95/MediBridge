package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.OrderListener;
import com.indosoft.medibridge.Listener.PlansListener;
import com.indosoft.medibridge.Model.OrderResponse;
import com.indosoft.medibridge.Model.PlansResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlansRepository {
    public static PlansRepository repository;

    private final MutableLiveData<List<PlansResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static PlansRepository getInstance() {
        if (repository == null) {
            repository = new PlansRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public PlansRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<PlansResponse>> getPlans(Context context, PlansListener listener) {
        Call<List<PlansResponse>> call = apiInterface.getPlans();
        call.enqueue(new Callback<List<PlansResponse>>() {
            @Override
            public void onResponse(Call<List<PlansResponse>> call, Response<List<PlansResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<PlansResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
