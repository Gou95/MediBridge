package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Listener.GetSignUpUserListener;
import com.indosoft.mediBridge.Listener.GetUrgentCartListener;
import com.indosoft.mediBridge.Model.GetSignUpUserResponse;
import com.indosoft.mediBridge.Model.GetUrgentCartResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetUrgentCartRepository {
    public static GetUrgentCartRepository repository;

    private final MutableLiveData<List<GetUrgentCartResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static GetUrgentCartRepository getInstance() {
        if (repository == null) {
            repository = new GetUrgentCartRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public GetUrgentCartRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<GetUrgentCartResponse>> getAllRemoveCartData(Context context,String retailer_id, GetUrgentCartListener listener) {
        Call<List<GetUrgentCartResponse>> call = apiInterface.removeUrgentCart(retailer_id);
        call.enqueue(new Callback<List<GetUrgentCartResponse>>() {
            @Override
            public void onResponse(Call<List<GetUrgentCartResponse>> call, Response<List<GetUrgentCartResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GetUrgentCartResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
