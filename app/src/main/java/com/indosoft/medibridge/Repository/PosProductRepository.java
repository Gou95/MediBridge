package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.PlansListener;
import com.indosoft.medibridge.Listener.PostProductListener;
import com.indosoft.medibridge.Model.GetPosProductResponse;
import com.indosoft.medibridge.Model.PlansResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosProductRepository {
    public static PosProductRepository repository;

    private final MutableLiveData<List<GetPosProductResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static PosProductRepository getInstance() {
        if (repository == null) {
            repository = new PosProductRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public PosProductRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<GetPosProductResponse>> getPosList(Context context,String retailer_id, PostProductListener listener) {
        Call<List<GetPosProductResponse>> call = apiInterface.getPosList(retailer_id);
        call.enqueue(new Callback<List<GetPosProductResponse>>() {
            @Override
            public void onResponse(Call<List<GetPosProductResponse>> call, Response<List<GetPosProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<GetPosProductResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
