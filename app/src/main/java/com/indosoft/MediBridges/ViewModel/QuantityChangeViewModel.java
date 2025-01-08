package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Listener.QuantityChangeListener;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Model.QuantityChangeResponse;
import com.indosoft.MediBridges.Repository.OrderRepository;
import com.indosoft.MediBridges.Repository.QuantityChangeRepository;

public class QuantityChangeViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<QuantityChangeResponse> responseMutableLiveData;


    private QuantityChangeRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<QuantityChangeResponse>getLiveData(){
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
        repository = QuantityChangeRepository.getInstance();
    }
    QuantityChangeListener listener = new QuantityChangeListener() {


        @Override
        public void onSuccess(QuantityChangeResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void updateQuantityChange(String cart_id,String product_id,String qty) {
        isConnecting.setValue(true);  // Show loading state
        repository = QuantityChangeRepository.getInstance();
        repository.getQtyChangeData(context,cart_id,product_id, qty,listener);

    }
}
