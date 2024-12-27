package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Body.SignUpBody;
import com.indosoft.mediBridge.Listener.CartListener;
import com.indosoft.mediBridge.Listener.SignUpListener;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.SignUpResponse;
import com.indosoft.mediBridge.Repository.CartRepository;
import com.indosoft.mediBridge.Repository.SignUpRepository;

import java.util.List;

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
            responseMutableLiveData.setValue(response);
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
}
