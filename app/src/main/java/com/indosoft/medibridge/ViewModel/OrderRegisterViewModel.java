package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.OrderRegisterListener;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Repository.OrderRegisterRepository;

import java.util.List;

public class OrderRegisterViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<OrderRegisterResponse>> responseMutableLiveData;


    private OrderRegisterRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<OrderRegisterResponse>>getLiveData(){
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
        repository = OrderRegisterRepository.getInstance();
    }
    OrderRegisterListener listener = new OrderRegisterListener() {
        @Override
        public void onSuccess(List<OrderRegisterResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void orderRegisterList(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = OrderRegisterRepository.getInstance();
        repository.getRegisterList(context,retailer_id, listener);

    }
}
