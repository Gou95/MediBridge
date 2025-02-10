package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.OtpListener;
import com.indosoft.medibridge.Model.OtpResponse;
import com.indosoft.medibridge.Repository.OtpRepository;

public class OtpViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<OtpResponse> responseMutableLiveData;


    private OtpRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<OtpResponse>getLiveData(){
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
        repository = OtpRepository.getInstance();
    }
    OtpListener listener = new OtpListener() {


        @Override
        public void onSuccess(OtpResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getOrderNumData(String retailer_phone) {
        isConnecting.setValue(true);  // Show loading state
        repository = OtpRepository.getInstance();
        repository.getOtp(context,retailer_phone, listener);

    }
}
