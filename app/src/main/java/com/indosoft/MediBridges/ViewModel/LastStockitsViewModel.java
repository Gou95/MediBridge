package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.DealerListener;
import com.indosoft.MediBridges.Listener.LastStockitsListener;
import com.indosoft.MediBridges.Model.DealersResponse;
import com.indosoft.MediBridges.Model.LastStockitsResponse;
import com.indosoft.MediBridges.Repository.DealersRepository;
import com.indosoft.MediBridges.Repository.LastStockitsRepository;

import java.util.List;

public class LastStockitsViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<LastStockitsResponse>> responseMutableLiveData;


    private LastStockitsRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<LastStockitsResponse>>getLiveData(){
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
        repository = LastStockitsRepository.getInstance();
    }
    LastStockitsListener listener = new LastStockitsListener() {
        @Override
        public void onSuccess(List<LastStockitsResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void lastStockitsData(String retailer_id,String product_id) {
        isConnecting.setValue(true);
        repository = LastStockitsRepository.getInstance();
        repository.getLastStockits(context,retailer_id,product_id, listener);

    }

}
