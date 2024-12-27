package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Listener.MedicineListListener;
import com.indosoft.mediBridge.Listener.UnitListener;
import com.indosoft.mediBridge.Model.MedicineListResponse;
import com.indosoft.mediBridge.Model.UnitResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineRepository {
    public static MedicineRepository repository;

    private final MutableLiveData<List<MedicineListResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static MedicineRepository getInstance() {
        if (repository == null) {
            repository = new MedicineRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public MedicineRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<MedicineListResponse>> getItemsData(Context context, MedicineListListener listener) {
        Call<List<MedicineListResponse>> call = apiInterface.medicineList();
        call.enqueue(new Callback<List<MedicineListResponse>>() {
            @Override
            public void onResponse(Call<List<MedicineListResponse>> call, Response<List<MedicineListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MedicineListResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
