package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Body.ExpiryRegisterBody;
import com.indosoft.medibridge.Body.SignUpBody;
import com.indosoft.medibridge.Body.StockistBody;
import com.indosoft.medibridge.Body.UnlistedBody;
import com.indosoft.medibridge.Body.UpdateStatusBody;
import com.indosoft.medibridge.Listener.SignUpListener;
import com.indosoft.medibridge.Model.SignUpResponse;
import com.indosoft.medibridge.Repository.SignUpRepository;

import java.io.File;

public class SignUpViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<SignUpResponse> responseMutableLiveData;


    private SignUpRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<SignUpResponse>getLiveData(){
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
        repository = SignUpRepository.getInstance();
    }
    SignUpListener listener = new SignUpListener() {


        @Override
        public void onSuccess(SignUpResponse response) {
            if (responseMutableLiveData != null) {  // ✅ Added null check
                responseMutableLiveData.postValue(response);
            }
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getSignData(SignUpBody body) {
        isConnecting.setValue(true);  // Show loading state
        repository = SignUpRepository.getInstance();
        repository.getSignUpData(context,body, listener);

    }

    public void registerStockist(StockistBody body) {
        isConnecting.setValue(true);
        repository = SignUpRepository.getInstance();
        repository.getRegisterStockist(context,body, listener);

    }
    public void expiryRegister(ExpiryRegisterBody body) {
        isConnecting.setValue(true);  // Show loading state
        repository = SignUpRepository.getInstance();
        repository.getExpiryRegister(context,body, listener);

    }
    public void unlistedMedicine(UnlistedBody body) {
        isConnecting.setValue(true);  // Show loading state
        repository = SignUpRepository.getInstance();
        repository.getUnlisted(context,body, listener);

    }
    public void uploadImage(File imageFile) {
        isConnecting.setValue(true);  // Show loading state
        repository = SignUpRepository.getInstance();
        repository.uploadRetailerImage(context,imageFile, listener);

    }
    public void updateStatus(String retailer_id, UpdateStatusBody body) {
        isConnecting.setValue(true);  // Show loading state
        repository = SignUpRepository.getInstance();
        repository.updateStatus(context,retailer_id,body, listener);

    }
    public void expiryStatus(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = SignUpRepository.getInstance();
        repository.expiryStatus(context,retailer_id, listener);

    }
}
