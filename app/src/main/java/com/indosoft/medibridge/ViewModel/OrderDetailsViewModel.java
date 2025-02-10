package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.OrderDetailListerner;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Repository.OrderDetailsRepository;

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
    public void getOrderDetailsData(String retailer_id,String order_no,String dealer_id,String order_status) {
        isConnecting.setValue(true);
        repository = OrderDetailsRepository.getInstance();
        repository.orderDetailsData(context,retailer_id,order_no,dealer_id, order_status, listener);

    }
}
