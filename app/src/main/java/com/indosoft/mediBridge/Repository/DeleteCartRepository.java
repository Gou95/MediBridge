package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Listener.DealerListener;
import com.indosoft.mediBridge.Listener.DeleteCartListener;
import com.indosoft.mediBridge.Model.AddtoCartResponse;
import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Model.DeleteCartResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteCartRepository {

    public static DeleteCartRepository repository;

    private final MutableLiveData<DeleteCartResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static DeleteCartRepository getInstance() {
        if (repository == null) {
            repository = new DeleteCartRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public DeleteCartRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<DeleteCartResponse> getDeleteData(Context context,String cart_id, DeleteCartListener listener) {
        Call<DeleteCartResponse> call = apiInterface.deleteCart(cart_id);
        call.enqueue(new Callback<DeleteCartResponse>() {
            @Override
            public void onResponse(Call<DeleteCartResponse> call, Response<DeleteCartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteCartResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
