package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.LastStockitsListener;
import com.indosoft.MediBridges.Listener.RecentStockitslistener;
import com.indosoft.MediBridges.Model.LastStockitsResponse;
import com.indosoft.MediBridges.Model.RecentStockitsResponse;
import com.indosoft.MediBridges.Repository.LastStockitsRepository;
import com.indosoft.MediBridges.Repository.RecentStockitsRepository;

import java.util.List;

public class RecentStockitsViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<RecentStockitsResponse>> responseMutableLiveData;


    private RecentStockitsRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<RecentStockitsResponse>>getLiveData(){
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
        repository = RecentStockitsRepository.getInstance();
    }
    RecentStockitslistener listener = new RecentStockitslistener() {
        @Override
        public void onSuccess(List<RecentStockitsResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void recentStockits(String retailer_id) {
        isConnecting.setValue(true);
        repository = RecentStockitsRepository.getInstance();
        repository.getRecent(context,retailer_id, listener);

    }
}
