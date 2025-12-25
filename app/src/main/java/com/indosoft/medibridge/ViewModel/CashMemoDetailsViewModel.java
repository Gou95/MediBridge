package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.CashMemoDetailsListener;
import com.indosoft.medibridge.Listener.CashMemoListListener;
import com.indosoft.medibridge.Model.CashMemoListResponse;
import com.indosoft.medibridge.Model.CashMemodetailsResponse;
import com.indosoft.medibridge.Repository.CashMemoDetailsRepository;
import com.indosoft.medibridge.Repository.CashMemoListRepository;

import java.util.List;

public class CashMemoDetailsViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<CashMemodetailsResponse>> responseMutableLiveData;


    private CashMemoDetailsRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<CashMemodetailsResponse>>getLiveData(){
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
        repository = CashMemoDetailsRepository.getInstance();
    }
    CashMemoDetailsListener listener = new CashMemoDetailsListener() {


        @Override
        public void onSuccess(List<CashMemodetailsResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getCashMemoDetails(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = CashMemoDetailsRepository.getInstance();
        repository.getCashMemoDetails(context,retailer_id, listener);

    }
}
