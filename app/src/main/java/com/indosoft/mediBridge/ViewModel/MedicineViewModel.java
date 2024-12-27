package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.DealerListener;
import com.indosoft.mediBridge.Listener.MedicineListListener;
import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Model.MedicineListResponse;
import com.indosoft.mediBridge.Repository.DealersRepository;
import com.indosoft.mediBridge.Repository.MedicineRepository;

import java.util.List;

public class MedicineViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<MedicineListResponse>> responseMutableLiveData;


    private MedicineRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<MedicineListResponse>>getLiveData(){
        if (responseMutableLiveData == null){
            responseMutableLiveData = new MutableLiveData<>();
        }

        return responseMutableLiveData;
    }
    public void init(Context context){
        this.context = context;
        if (responseMutableLiveData == null){
            return;
        }
        repository = MedicineRepository.getInstance();
    }
    MedicineListListener listener = new MedicineListListener() {
        @Override
        public void onSuccess(List<MedicineListResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getMedicineData() {
        isConnecting.setValue(true);  // Show loading state
        repository = MedicineRepository.getInstance();
        repository.getItemsData(context, listener);

    }
}
