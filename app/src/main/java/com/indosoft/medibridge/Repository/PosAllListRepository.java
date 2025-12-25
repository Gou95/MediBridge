package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.PosAllListListener;
import com.indosoft.medibridge.Listener.PosDetailsListener;
import com.indosoft.medibridge.Model.PosAllListResponse;
import com.indosoft.medibridge.Model.PosDetailsResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosAllListRepository {
    public static PosAllListRepository repository;

    private final MutableLiveData<List<PosAllListResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public  static PosAllListRepository getInstance() {
        if (repository == null) {
            repository = new PosAllListRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public PosAllListRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<PosAllListResponse>> allPosList(Context context, String retailer_id,String addtime, PosAllListListener listener) {
        Call<List<PosAllListResponse>> call = apiInterface.allPosList(retailer_id,addtime);
        call.enqueue(new Callback<List<PosAllListResponse>>() {
            @Override
            public void onResponse(Call<List<PosAllListResponse>> call, Response<List<PosAllListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<PosAllListResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
}
}
