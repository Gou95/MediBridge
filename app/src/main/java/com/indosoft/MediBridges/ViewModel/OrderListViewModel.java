package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.MedicineListListener;
import com.indosoft.MediBridges.Listener.OrderListListener;
import com.indosoft.MediBridges.Model.MedicineListResponse;
import com.indosoft.MediBridges.Model.OrderListResponse;
import com.indosoft.MediBridges.Repository.MedicineRepository;
import com.indosoft.MediBridges.Repository.OrderListRepository;

import java.util.List;

public class OrderListViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<OrderListResponse>> responseMutableLiveData;


    private OrderListRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<OrderListResponse>>getLiveData(){
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
        repository = OrderListRepository.getInstance();
    }
    OrderListListener listener = new OrderListListener() {
        @Override
        public void onSuccess(List<OrderListResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void orderList(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = OrderListRepository.getInstance();
        repository.getOrderListData(context,retailer_id, listener);

    }
}
