package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.DeliveryDayListener;
import com.indosoft.medibridge.Listener.ExpiryListListener;
import com.indosoft.medibridge.Model.DeliveryDayResponse;
import com.indosoft.medibridge.Model.ExpiryListResponse;
import com.indosoft.medibridge.Repository.DeliveryDayRepository;
import com.indosoft.medibridge.Repository.ExpiryListRepository;

import java.util.List;

public class ExpiryListViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<ExpiryListResponse>> responseMutableLiveData;


    private ExpiryListRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<ExpiryListResponse>>getLiveData(){
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
        repository = ExpiryListRepository.getInstance();
    }
    ExpiryListListener listener = new ExpiryListListener() {


        @Override
        public void onSuccess(List<ExpiryListResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getList() {
        isConnecting.setValue(true);  // Show loading state
        repository = ExpiryListRepository.getInstance();
        repository.getExpiry(context, listener);

    }
}
