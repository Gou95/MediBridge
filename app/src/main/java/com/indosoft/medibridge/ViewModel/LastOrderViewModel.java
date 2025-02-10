package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.LastOrderListener;
import com.indosoft.medibridge.Model.LastOrderResponse;
import com.indosoft.medibridge.Repository.LastOrderRepository;

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
