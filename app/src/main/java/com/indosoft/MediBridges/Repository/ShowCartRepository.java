package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.MedicineListListener;
import com.indosoft.MediBridges.Listener.ShowCartListener;
import com.indosoft.MediBridges.Model.MedicineListResponse;
import com.indosoft.MediBridges.Model.ShowCartResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowCartRepository {

    public static ShowCartRepository repository;

    private final MutableLiveData<List<ShowCartResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static ShowCartRepository getInstance() {
        if (repository == null) {
            repository = new ShowCartRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public ShowCartRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<ShowCartResponse>> getShowCartData(Context context,String retailer_id, ShowCartListener listener) {
        Call<List<ShowCartResponse>> call = apiInterface.showCartList(retailer_id);
        call.enqueue(new Callback<List<ShowCartResponse>>() {
            @Override
            public void onResponse(Call<List<ShowCartResponse>> call, Response<List<ShowCartResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ShowCartResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<List<ShowCartResponse>> getShowGetCartData(Context context, ShowCartListener listener) {
        Call<List<ShowCartResponse>> call = apiInterface.showGetCartList();
        call.enqueue(new Callback<List<ShowCartResponse>>() {
            @Override
            public void onResponse(Call<List<ShowCartResponse>> call, Response<List<ShowCartResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ShowCartResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }
}
