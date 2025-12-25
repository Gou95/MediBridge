package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.MedicineListListener;
import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.Repository.MedicineRepository;

import java.util.List;

public class MedicineViewModel extends ViewModel {

    private Context context;
    private final MutableLiveData<List<MedicineListResponse.Datum>> medicineLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private MedicineRepository repository;

    public void init(Context context) {
        this.context = context;
        repository = MedicineRepository.getInstance();
    }

    public LiveData<List<MedicineListResponse.Datum>> getLiveData() {
        return medicineLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    // ✅ SEARCH CALL
    public void searchMedicine(String query) {
        loading.setValue(true);

        repository.searchMedicine( query, new MedicineListListener() {
            @Override
            public void onSuccess(List<MedicineListResponse.Datum> response) {
                loading.setValue(false);
                medicineLiveData.setValue(response);
            }

            @Override
            public void onError(String err) {
                loading.setValue(false);
                error.setValue(err);
            }
        });
    }
}
