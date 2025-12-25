package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.PosDetailsListener;
import com.indosoft.medibridge.Listener.PostProductListener;
import com.indosoft.medibridge.Model.GetPosProductResponse;
import com.indosoft.medibridge.Model.PosDetailsResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosDetailsRepository {
    public static PosDetailsRepository repository;

    private final MutableLiveData<List<PosDetailsResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static PosDetailsRepository getInstance() {
        if (repository == null) {
            repository = new PosDetailsRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public PosDetailsRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<PosDetailsResponse>> posList(Context context, String retailer_id, PosDetailsListener listener) {
        Call<List<PosDetailsResponse>> call = apiInterface.posList(retailer_id);
        call.enqueue(new Callback<List<PosDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<PosDetailsResponse>> call, Response<List<PosDetailsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<PosDetailsResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
