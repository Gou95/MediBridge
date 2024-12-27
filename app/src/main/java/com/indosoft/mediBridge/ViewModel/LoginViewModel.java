package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.CartListener;
import com.indosoft.mediBridge.Listener.LoginListener;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.LoginResponse;
import com.indosoft.mediBridge.Repository.CartRepository;
import com.indosoft.mediBridge.Repository.LoginRepository;

import java.util.List;

public class LoginViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<LoginResponse>> responseMutableLiveData;


    private LoginRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<LoginResponse>>getLiveData(){
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
        repository = LoginRepository.getInstance();
    }
    LoginListener listener = new LoginListener() {


        @Override
        public void onSuccess(List<LoginResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getLoginResData(String mobile,String password) {
        isConnecting.setValue(true);  // Show loading state
        repository = LoginRepository.getInstance();
        repository.getLoginData(context,mobile,password, listener);

    }


}
