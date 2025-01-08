package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Body.OrderDetailsBody;
import com.indosoft.MediBridges.Listener.OrderDetailListerner;
import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Model.OrderDetailsResponse;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Repository.OrderDetailsRepository;
import com.indosoft.MediBridges.Repository.OrderRepository;

import java.util.List;

public class OrderDetailsViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<OrderDetailsResponse>> responseMutableLiveData;


    private OrderDetailsRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<OrderDetailsResponse>>getLiveData(){
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
        repository = OrderDetailsRepository.getInstance();
    }
    OrderDetailListerner listener = new OrderDetailListerner() {


        @Override
        public void onSuccess(List<OrderDetailsResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getOrderDetailsData(String retailer_id,String order_no) {
        isConnecting.setValue(true);
        repository = OrderDetailsRepository.getInstance();
        repository.orderDetailsData(context,retailer_id,order_no, listener);

    }
}
