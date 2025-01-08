package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Body.SignUpBody;
import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Listener.SignUpListener;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Model.SignUpResponse;
import com.indosoft.MediBridges.Repository.OrderRepository;
import com.indosoft.MediBridges.Repository.SignUpRepository;

public class OrderViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<OrderResponse> responseMutableLiveData;


    private OrderRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<OrderResponse>getLiveData(){
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
        repository = OrderRepository.getInstance();
    }
    OrderListener listener = new OrderListener() {


        @Override
        public void onSuccess(OrderResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getOrderNumData(String retailer_id,String order_no) {
        isConnecting.setValue(true);  // Show loading state
        repository = OrderRepository.getInstance();
        repository.orderResData(context,retailer_id,order_no, listener);

    }
}
