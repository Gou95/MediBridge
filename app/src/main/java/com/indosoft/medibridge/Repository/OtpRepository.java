package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.OtpListener;
import com.indosoft.medibridge.Model.OtpResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpRepository {

    public static OtpRepository repository;

    private final MutableLiveData<OtpResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static OtpRepository getInstance() {
        if (repository == null) {
            repository = new OtpRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public OtpRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<OtpResponse> getOtp(Context context, String retailer_phone, OtpListener listener) {
        Call<OtpResponse> call = apiInterface.otpGenerate(retailer_phone);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
