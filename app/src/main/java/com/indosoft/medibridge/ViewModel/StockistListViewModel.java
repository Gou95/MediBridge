package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.StockistListListener;
import com.indosoft.medibridge.Listener.StockitsListener;
import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.Repository.StockistListRepository;
import com.indosoft.medibridge.Repository.StockitsRepository;

import java.util.List;

public class StockistListViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<StockistListResponse>> responseMutableLiveData;


    private StockistListRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<StockistListResponse>>getLiveData(){
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
        repository = StockistListRepository.getInstance();
    }
    StockistListListener listener = new StockistListListener() {
        @Override
        public void onSuccess(List<StockistListResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void stockitsList(String city_id ) {
        isConnecting.setValue(true);  // Show loading state
        repository = StockistListRepository.getInstance();
        repository.getStockitsList(context,city_id, listener);

    }
}
