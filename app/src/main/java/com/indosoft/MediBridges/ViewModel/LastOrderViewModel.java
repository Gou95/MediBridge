package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.GetUrgentCartListener;
import com.indosoft.MediBridges.Listener.LastOrderListener;
import com.indosoft.MediBridges.Model.GetUrgentCartResponse;
import com.indosoft.MediBridges.Model.LastOrderResponse;
import com.indosoft.MediBridges.Repository.GetUrgentCartRepository;
import com.indosoft.MediBridges.Repository.LastOrderRepository;

import java.util.List;

public class LastOrderViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<LastOrderResponse>> responseMutableLiveData;


    private LastOrderRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<LastOrderResponse>>getLiveData(){
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
        repository = LastOrderRepository.getInstance();
    }
    LastOrderListener listener = new LastOrderListener() {
        @Override
        public void onSuccess(List<LastOrderResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getOrder(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = LastOrderRepository.getInstance();
        repository.getLastOrder(context,retailer_id, listener);

    }


}
