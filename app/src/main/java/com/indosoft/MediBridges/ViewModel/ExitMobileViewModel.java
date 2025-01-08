package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Body.ExitMobileBody;
import com.indosoft.MediBridges.Listener.ExitMobileListener;
import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Model.ExitMobileResponse;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Repository.ExitMobileRepository;
import com.indosoft.MediBridges.Repository.OrderRepository;

public class ExitMobileViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<ExitMobileResponse> responseMutableLiveData;


    private ExitMobileRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<ExitMobileResponse>getLiveData(){
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
        repository = ExitMobileRepository.getInstance();
    }
    ExitMobileListener listener = new ExitMobileListener() {


        @Override
        public void onSuccess(ExitMobileResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getMobileNum(ExitMobileBody body) {
        isConnecting.setValue(true);
        repository = ExitMobileRepository.getInstance();
        repository.getExitMobile(context,body, listener);

    }

}
