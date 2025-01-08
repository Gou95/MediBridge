package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.StateListener;
import com.indosoft.MediBridges.Listener.StockitsListener;
import com.indosoft.MediBridges.Model.IndiaStateResponse;
import com.indosoft.MediBridges.Model.StockitsResponse;
import com.indosoft.MediBridges.Repository.StatesRepository;
import com.indosoft.MediBridges.Repository.StockitsRepository;

import java.util.List;

public class StockitsViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<StockitsResponse>> responseMutableLiveData;


    private StockitsRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<StockitsResponse>>getLiveData(){
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
        repository = StockitsRepository.getInstance();
    }
    StockitsListener listener = new StockitsListener() {
        @Override
        public void onSuccess(List<StockitsResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void stockitsOrderList(String retailer_id ,String dealer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = StockitsRepository.getInstance();
        repository.getStockitsOrder(context,retailer_id,dealer_id, listener);

    }
}
