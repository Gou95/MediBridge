package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.CashMemoPdfListener;
import com.indosoft.medibridge.Model.CashMemoPdfResponse;
import com.indosoft.medibridge.Repository.CashMemoPdfRepository;

import java.util.List;

public class CashMemoPdfViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<CashMemoPdfResponse>> responseMutableLiveData;


    private CashMemoPdfRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<CashMemoPdfResponse>>getLiveData(){
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
        repository = CashMemoPdfRepository.getInstance();
    }
    CashMemoPdfListener listener = new CashMemoPdfListener() {


        @Override
        public void onSuccess(List<CashMemoPdfResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getPdf(String sale_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = CashMemoPdfRepository.getInstance();
        repository.getPdf(context,sale_id, listener);

    }
}
