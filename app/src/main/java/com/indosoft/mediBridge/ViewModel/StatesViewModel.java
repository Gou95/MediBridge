package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.CartListener;
import com.indosoft.mediBridge.Listener.StateListener;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Repository.CartRepository;
import com.indosoft.mediBridge.Repository.StatesRepository;

import java.util.List;

public class StatesViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<IndiaStateResponse>> responseMutableLiveData;


    private StatesRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<IndiaStateResponse>>getLiveData(){
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
        repository = StatesRepository.getInstance();
    }
    StateListener listener = new StateListener() {
        @Override
        public void onSuccess(List<IndiaStateResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getStateData() {
        isConnecting.setValue(true);  // Show loading state
        repository = StatesRepository.getInstance();
        repository.getStattesData(context, listener);

    }

}
