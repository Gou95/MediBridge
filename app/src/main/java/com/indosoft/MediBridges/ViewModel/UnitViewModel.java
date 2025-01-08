package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.StateListener;
import com.indosoft.MediBridges.Listener.UnitListener;
import com.indosoft.MediBridges.Model.IndiaStateResponse;
import com.indosoft.MediBridges.Model.UnitResponse;
import com.indosoft.MediBridges.Repository.StatesRepository;
import com.indosoft.MediBridges.Repository.UnitRepository;

import java.util.List;

public class UnitViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<UnitResponse>> responseMutableLiveData;


    private UnitRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<UnitResponse>>getLiveData(){
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
        repository = UnitRepository.getInstance();
    }
    UnitListener listener = new UnitListener() {
        @Override
        public void onSuccess(List<UnitResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getUnits() {
        isConnecting.setValue(true);  // Show loading state
        repository = UnitRepository.getInstance();
        repository.getUnitData(context, listener);

    }

}
