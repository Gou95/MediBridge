package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.CityListener;
import com.indosoft.mediBridge.Listener.StateListener;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.Repository.CityRepository;
import com.indosoft.mediBridge.Repository.StatesRepository;

import java.util.List;

public class CityViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<StateCityResponse>> responseMutableLiveData;


    private CityRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<StateCityResponse>>getLiveData(){
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
        repository = CityRepository.getInstance();
    }
    CityListener listener = new CityListener() {
        @Override
        public void onSuccess(List<StateCityResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getCityData(String state_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = CityRepository.getInstance();
        repository.getCitiesData(context,state_id, listener);

    }
}
