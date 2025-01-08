package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.UrgentCartListener;
import com.indosoft.MediBridges.Listener.UrgentDeleteListener;
import com.indosoft.MediBridges.Model.UrgentCartResponse;
import com.indosoft.MediBridges.Model.UrgentDeleteResponse;
import com.indosoft.MediBridges.Repository.UrgentCartRepository;
import com.indosoft.MediBridges.Repository.UrgentDeleteRepository;

public class UrgentDeleteViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<UrgentDeleteResponse> responseMutableLiveData;


    private UrgentDeleteRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<UrgentDeleteResponse>getLiveData(){
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
        repository = UrgentDeleteRepository.getInstance();
    }
    UrgentDeleteListener listener = new UrgentDeleteListener() {
        @Override
        public void onSuccess(UrgentDeleteResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void deleteUrgentCart(String cart_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = UrgentDeleteRepository.getInstance();
        repository.urgentDeleteCartData(context,cart_id, listener);

    }
}
