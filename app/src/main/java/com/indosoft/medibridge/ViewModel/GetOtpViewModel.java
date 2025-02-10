package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.GetOtpListener;
import com.indosoft.medibridge.Model.GetOtpResponse;
import com.indosoft.medibridge.Repository.GetOtpRepository;

public class GetOtpViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<GetOtpResponse> responseMutableLiveData;


    private GetOtpRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<GetOtpResponse>getLiveData(){
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
        repository = GetOtpRepository.getInstance();
    }
    GetOtpListener listener = new GetOtpListener() {


        @Override
        public void onSuccess(GetOtpResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getOtpRes(String retailer_phone) {
        isConnecting.setValue(true);  // Show loading state
        repository = GetOtpRepository.getInstance();
        repository.getOtp(context,retailer_phone, listener);

    }
}
