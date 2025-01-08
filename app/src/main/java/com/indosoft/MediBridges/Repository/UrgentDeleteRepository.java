package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.UrgentCartListener;
import com.indosoft.MediBridges.Listener.UrgentDeleteListener;
import com.indosoft.MediBridges.Model.UrgentCartResponse;
import com.indosoft.MediBridges.Model.UrgentDeleteResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UrgentDeleteRepository {

    public static UrgentDeleteRepository repository;

    private final MutableLiveData<UrgentDeleteResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static UrgentDeleteRepository getInstance() {
        if (repository == null) {
            repository = new UrgentDeleteRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public UrgentDeleteRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<UrgentDeleteResponse> urgentDeleteCartData(Context context, String cart_id, UrgentDeleteListener listener) {
        Call<UrgentDeleteResponse> call = apiInterface.deleteUrgentCart(cart_id);
        call.enqueue(new Callback<UrgentDeleteResponse>() {
            @Override
            public void onResponse(Call<UrgentDeleteResponse> call, Response<UrgentDeleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<UrgentDeleteResponse> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }


}
