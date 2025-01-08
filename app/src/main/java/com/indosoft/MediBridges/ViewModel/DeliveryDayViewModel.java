package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Body.SignUpBody;
import com.indosoft.MediBridges.Listener.DeliveryDayListener;
import com.indosoft.MediBridges.Listener.SignUpListener;
import com.indosoft.MediBridges.Model.DeliveryDayResponse;
import com.indosoft.MediBridges.Model.SignUpResponse;
import com.indosoft.MediBridges.Repository.DeliveryDayRepository;
import com.indosoft.MediBridges.Repository.SignUpRepository;

public class DeliveryDayViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<DeliveryDayResponse> responseMutableLiveData;


    private DeliveryDayRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<DeliveryDayResponse>getLiveData(){
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
        repository = DeliveryDayRepository.getInstance();
    }
    DeliveryDayListener listener = new DeliveryDayListener() {


        @Override
        public void onSuccess(DeliveryDayResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void deliveryDayData(String cart_id,String delivery_day) {
        isConnecting.setValue(true);  // Show loading state
        repository = DeliveryDayRepository.getInstance();
        repository.updateDay(context,cart_id,delivery_day, listener);

    }
}
