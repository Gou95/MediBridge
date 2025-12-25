package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Model.CashMemoListResponse;
import com.indosoft.medibridge.Repository.CashMemoListRepository;
import com.indosoft.medibridge.Listener.CashMemoListListener;

import java.util.List;

public class CashMemoListViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<CashMemoListResponse>> responseMutableLiveData;


    private CashMemoListRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<CashMemoListResponse>>getLiveData(){
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
        repository = CashMemoListRepository.getInstance();
    }
    CashMemoListListener listener = new CashMemoListListener() {


        @Override
        public void onSuccess(List<CashMemoListResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getMedicine(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = CashMemoListRepository.getInstance();
        repository.getMedicine(context,retailer_id, listener);

    }
}
