package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Body.AddtoCartBody;
import com.indosoft.MediBridges.Body.UserUpdateBody;
import com.indosoft.MediBridges.Listener.AddtoCartListener;
import com.indosoft.MediBridges.Listener.UserUpdateListener;
import com.indosoft.MediBridges.Model.AddtoCartResponse;
import com.indosoft.MediBridges.Model.UserUpdateResponse;
import com.indosoft.MediBridges.Repository.AddtoCartRepository;
import com.indosoft.MediBridges.Repository.UserUpdateRepository;

public class UserUpdateViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<UserUpdateResponse> responseMutableLiveData;


    private UserUpdateRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<UserUpdateResponse>getLiveData(){
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
        repository = UserUpdateRepository.getInstance();
    }
    UserUpdateListener listener = new UserUpdateListener() {


        @Override
        public void onSuccess(UserUpdateResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getUserUpdateData(UserUpdateBody body) {
        isConnecting.setValue(true);  // Show loading state
        repository = UserUpdateRepository.getInstance();
        repository.getUserUpdateData(context,body, listener);

    }
}
