package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.UrgentCartListener;
import com.indosoft.mediBridge.Listener.UrgentDeleteListener;
import com.indosoft.mediBridge.Model.UrgentCartResponse;
import com.indosoft.mediBridge.Model.UrgentDeleteResponse;
import com.indosoft.mediBridge.Repository.UrgentCartRepository;
import com.indosoft.mediBridge.Repository.UrgentDeleteRepository;

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
