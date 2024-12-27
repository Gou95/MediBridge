package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.CityListener;
import com.indosoft.mediBridge.Listener.GetSignUpUserListener;
import com.indosoft.mediBridge.Model.GetSignUpUserResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.Repository.CityRepository;
import com.indosoft.mediBridge.Repository.GetSignUpUserRepository;

import java.util.List;

public class GetSignUpUserViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<GetSignUpUserResponse>> responseMutableLiveData;


    private GetSignUpUserRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<GetSignUpUserResponse>>getLiveData(){
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
        repository = GetSignUpUserRepository.getInstance();
    }
    GetSignUpUserListener listener = new GetSignUpUserListener() {
        @Override
        public void onSuccess(List<GetSignUpUserResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getAllSignUPData() {
        isConnecting.setValue(true);  // Show loading state
        repository = GetSignUpUserRepository.getInstance();
        repository.getAllUsersData(context, listener);

    }
}
