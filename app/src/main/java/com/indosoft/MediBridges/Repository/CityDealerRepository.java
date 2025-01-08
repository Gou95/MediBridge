package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.CartListener;
import com.indosoft.MediBridges.Listener.CityDealerListener;
import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Model.CityDealerResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityDealerRepository {

    public static CityDealerRepository repository;

    private final MutableLiveData<List<CityDealerResponse>> mutableLiveData = new MutableLiveData<>();

    // Singleton pattern
    public static CityDealerRepository getInstance() {
        if (repository == null) {
            repository = new CityDealerRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CityDealerRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<CityDealerResponse>> getCityDealerData(Context context,String cart_id, CityDealerListener listener) {

        Call<List<CityDealerResponse>> call = apiInterface.cityDealer(cart_id);
        call.enqueue(new Callback<List<CityDealerResponse>>() {
            @Override
            public void onResponse(Call<List<CityDealerResponse>> call, Response<List<CityDealerResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {
                    // Handle unsuccessful response (e.g., server error)
                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CityDealerResponse>> call, Throwable t) {
                // Handle failure (e.g., network issue)
                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }


}
