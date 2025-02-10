package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.GetOtpListener;
import com.indosoft.medibridge.Model.GetOtpResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetOtpRepository {

    public static GetOtpRepository repository;

    private final MutableLiveData<GetOtpResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static GetOtpRepository getInstance() {
        if (repository == null) {
            repository = new GetOtpRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public GetOtpRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<GetOtpResponse> getOtp(Context context, String retailer_phone, GetOtpListener listener) {
        Call<GetOtpResponse> call = apiInterface.getOtp(retailer_phone);
        call.enqueue(new Callback<GetOtpResponse>() {
            @Override
            public void onResponse(Call<GetOtpResponse> call, Response<GetOtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<GetOtpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
