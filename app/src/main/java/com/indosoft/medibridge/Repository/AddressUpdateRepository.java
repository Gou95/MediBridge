package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Body.AddressUpdateBody;
import com.indosoft.medibridge.Listener.AddressUpdateListener;
import com.indosoft.medibridge.Model.AddressUpdateResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressUpdateRepository {

    public static AddressUpdateRepository repository;

    private final MutableLiveData<AddressUpdateResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static AddressUpdateRepository getInstance() {
        if (repository == null) {
            repository = new AddressUpdateRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public AddressUpdateRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<AddressUpdateResponse> getAddress(Context context, String retailer_id, AddressUpdateBody body, AddressUpdateListener listener) {
        Call<AddressUpdateResponse> call = apiInterface.addressUpdate(retailer_id,body);
        call.enqueue(new Callback<AddressUpdateResponse>() {
            @Override
            public void onResponse(Call<AddressUpdateResponse> call, Response<AddressUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<AddressUpdateResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
