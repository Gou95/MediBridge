package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Body.SendEmailBody;
import com.indosoft.medibridge.Body.UnlistedItemBody;
import com.indosoft.medibridge.Listener.SignUpListener;
import com.indosoft.medibridge.Listener.UnlistedItemListener;
import com.indosoft.medibridge.Model.SignUpResponse;
import com.indosoft.medibridge.Model.UnlistedItemResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnlistedItemRepository {
    public static UnlistedItemRepository repository;

    private final MutableLiveData<UnlistedItemResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static UnlistedItemRepository getInstance() {
        if (repository == null) {
            repository = new UnlistedItemRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public UnlistedItemRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }
    public MutableLiveData<UnlistedItemResponse> unlistedItemAdd(Context context, UnlistedItemBody body, UnlistedItemListener listener) {
        Call<UnlistedItemResponse> call = apiInterface.addUnlistedItem(body);
        call.enqueue(new Callback<UnlistedItemResponse>() {
            @Override
            public void onResponse(Call<UnlistedItemResponse> call, Response<UnlistedItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<UnlistedItemResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
