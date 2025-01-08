package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.CartListener;
import com.indosoft.MediBridges.Listener.CityDealerListener;
import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Model.CityDealerResponse;
import com.indosoft.MediBridges.Repository.CartRepository;
import com.indosoft.MediBridges.Repository.CityDealerRepository;

import java.util.List;

public class CityDealerViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<CityDealerResponse>> responseMutableLiveData;


    private CityDealerRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<CityDealerResponse>>getLiveData(){
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
        repository = CityDealerRepository.getInstance();
    }
    CityDealerListener listener = new CityDealerListener() {


        @Override
        public void onSuccess(List<CityDealerResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void cityDealerData(String cart_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = CityDealerRepository.getInstance();
        repository.getCityDealerData(context,cart_id, listener);

    }


}
