package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.CounterListener;
import com.indosoft.medibridge.Listener.OtpListener;
import com.indosoft.medibridge.Model.CounterResponse;
import com.indosoft.medibridge.Model.OtpResponse;
import com.indosoft.medibridge.Repository.CounterRepository;
import com.indosoft.medibridge.Repository.OtpRepository;

public class CounterViewModel extends ViewModel {
//    private Context context;
//
//    private MutableLiveData<String> isFailed = new MutableLiveData<>();
//
//    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();
//
//    private MutableLiveData<CounterResponse> responseMutableLiveData;
//
//
//    private CounterRepository repository;
//
//    public LiveData<String> getIsFailed(){
//        return isFailed;
//    }
//
//    public LiveData<Boolean>getIsConnecting(){
//        return isConnecting;
//
//
//    }
//
//    public LiveData<CounterResponse>getLiveData(){
//        if (responseMutableLiveData == null){
//            responseMutableLiveData = new MutableLiveData<>();
//        }
//
//        return responseMutableLiveData;
//    }
//    public void init(Context context){
//        this.context = context;
//        if (responseMutableLiveData == null){
//            return;
//        }
//        repository = CounterRepository.getInstance();
//    }
//    CounterListener listener = new CounterListener() {
//
//
//        @Override
//        public void onSuccess(CounterResponse response) {
//            responseMutableLiveData.setValue(response);
//        }
//
//        @Override
//        public void onError(String error) {
//            isFailed.setValue(error);
//        }
//    };
//    public void getCounter(String retailer_id) {
//        isConnecting.setValue(true);  // Show loading state
//        repository = CounterRepository.getInstance();
//        repository.getCount(context,retailer_id, listener);
//
//    }
//    public void updateCartCount(int newCount) {
//        if (responseMutableLiveData == null) {
//            responseMutableLiveData = new MutableLiveData<>();
//        }
//        CounterResponse counterResponse = new CounterResponse();
//        counterResponse.setMedicounter(String.valueOf(newCount)); // Assuming CounterResponse has a setCount() method
//        responseMutableLiveData.setValue(counterResponse);
//    }
}
