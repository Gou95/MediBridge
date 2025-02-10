package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.ProceedOrderListener;
import com.indosoft.medibridge.Model.ProceedOrderResponse;
import com.indosoft.medibridge.Repository.ProceedOrderRepository;

public class ProceedOrderViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<ProceedOrderResponse> responseMutableLiveData;


    private ProceedOrderRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<ProceedOrderResponse>getLiveData(){
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
        repository = ProceedOrderRepository.getInstance();
    }
    ProceedOrderListener listener = new ProceedOrderListener() {


        @Override
        public void onSuccess(ProceedOrderResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getProccedOrder(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = ProceedOrderRepository.getInstance();
        repository.proceedOrderData(context,retailer_id, listener);

    }
}
