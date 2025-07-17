package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.RecentStockitslistener;
import com.indosoft.medibridge.Listener.RecievedOrderListener;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.Model.RecievedOrderResponse;
import com.indosoft.medibridge.Repository.RecentStockitsRepository;
import com.indosoft.medibridge.Repository.RecievedProductRepository;

import java.util.List;

public class RecievedProductViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<RecievedOrderResponse>> responseMutableLiveData;


    private RecievedProductRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<RecievedOrderResponse>>getLiveData(){
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
        repository = RecievedProductRepository.getInstance();
    }
    RecievedOrderListener listener = new RecievedOrderListener() {
        @Override
        public void onSuccess(List<RecievedOrderResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getRecievedOrder(String retailer_id) {
        isConnecting.setValue(true);
        repository = RecievedProductRepository.getInstance();
        repository.getRecieved(context,retailer_id, listener);

    }
}
