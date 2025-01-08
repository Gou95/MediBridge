package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Listener.UrgentProceedListener;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Model.UrgentProceedResponse;
import com.indosoft.MediBridges.Repository.OrderRepository;
import com.indosoft.MediBridges.Repository.UrgentProceedRepository;

public class UrgentProceedViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<UrgentProceedResponse> responseMutableLiveData;


    private UrgentProceedRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<UrgentProceedResponse>getLiveData(){
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
        repository = UrgentProceedRepository.getInstance();
    }
    UrgentProceedListener listener = new UrgentProceedListener() {


        @Override
        public void onSuccess(UrgentProceedResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getUrgentProceed(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = UrgentProceedRepository.getInstance();
        repository.urgentProceedData(context,retailer_id, listener);

    }

}
