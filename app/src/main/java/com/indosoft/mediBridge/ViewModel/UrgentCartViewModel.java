package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.UnitListener;
import com.indosoft.mediBridge.Listener.UrgentCartListener;
import com.indosoft.mediBridge.Model.UnitResponse;
import com.indosoft.mediBridge.Model.UrgentCartResponse;
import com.indosoft.mediBridge.Repository.UnitRepository;
import com.indosoft.mediBridge.Repository.UrgentCartRepository;

import java.util.List;

public class UrgentCartViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<UrgentCartResponse> responseMutableLiveData;


    private UrgentCartRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<UrgentCartResponse>getLiveData(){
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
        repository = UrgentCartRepository.getInstance();
    }
    UrgentCartListener listener = new UrgentCartListener() {
        @Override
        public void onSuccess(UrgentCartResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getUrgentCart(String cart_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = UrgentCartRepository.getInstance();
        repository.moveUrgentCartData(context,cart_id, listener);

    }
}
