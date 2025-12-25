package com.indosoft.medibridgestockist.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridgestockist.Listener.CityListener;
import com.indosoft.medibridgestockist.Listener.CompanyListener;
import com.indosoft.medibridgestockist.Model.CityResponse;
import com.indosoft.medibridgestockist.Model.CompanyResponse;
import com.indosoft.medibridgestockist.RetrofitServices.ApiInterface;
import com.indosoft.medibridgestockist.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyRepository {
    public static CompanyRepository repository;

    private final MutableLiveData<List<CompanyResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static CompanyRepository getInstance() {
        if (repository == null) {
            repository = new CompanyRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CompanyRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<CompanyResponse>> getCompany(Context context, String dealer_id, CompanyListener listener) {
        Call<List<CompanyResponse>> call = apiInterface.getCompany(dealer_id);
        call.enqueue(new Callback<List<CompanyResponse>>() {
            @Override
            public void onResponse(Call<List<CompanyResponse>> call, Response<List<CompanyResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CompanyResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
