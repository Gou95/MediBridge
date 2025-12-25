package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.StockitsListener;
import com.indosoft.medibridge.Listener.TotalSalesListener;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.Model.TotalSalesResponse;
import com.indosoft.medibridge.Repository.StockitsRepository;
import com.indosoft.medibridge.Repository.TotalSalesRepository;

import java.util.List;

public class TotalSalesViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<TotalSalesResponse>> responseMutableLiveData;


    private TotalSalesRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<TotalSalesResponse>>getLiveData(){
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
        repository = TotalSalesRepository.getInstance();
    }
    TotalSalesListener listener = new TotalSalesListener() {
        @Override
        public void onSuccess(List<TotalSalesResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getTotalSales(String retailer_id ) {
        isConnecting.setValue(true);  // Show loading state
        repository = TotalSalesRepository.getInstance();
        repository.getTotalSales(context,retailer_id, listener);

    }
}
