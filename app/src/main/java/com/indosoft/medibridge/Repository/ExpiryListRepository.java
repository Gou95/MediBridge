package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Body.ExitMobileBody;
import com.indosoft.medibridge.Listener.ExitMobileListener;
import com.indosoft.medibridge.Listener.ExpiryListListener;
import com.indosoft.medibridge.Model.ExitMobileResponse;
import com.indosoft.medibridge.Model.ExpiryListResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpiryListRepository {
    public static ExpiryListRepository repository;

    private final MutableLiveData<List<ExpiryListResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static ExpiryListRepository getInstance() {
        if (repository == null) {
            repository = new ExpiryListRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public ExpiryListRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<ExpiryListResponse>> getExpiry(Context context, ExpiryListListener listener) {
        Call<List<ExpiryListResponse>> call = apiInterface.getExpiryList();
        call.enqueue(new Callback<List<ExpiryListResponse>>() {
            @Override
            public void onResponse(Call<List<ExpiryListResponse>> call, Response<List<ExpiryListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<ExpiryListResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
