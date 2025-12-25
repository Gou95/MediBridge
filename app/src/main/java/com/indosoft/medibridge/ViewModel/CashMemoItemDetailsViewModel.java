package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.CashMemoDetailsListener;
import com.indosoft.medibridge.Listener.CashMemoItemDetailsListener;
import com.indosoft.medibridge.Model.CashMemoItemDetailsResponse;
import com.indosoft.medibridge.Model.CashMemodetailsResponse;
import com.indosoft.medibridge.Repository.CashMemoDetailsRepository;
import com.indosoft.medibridge.Repository.CashMemoItemDetailsRepository;

import java.util.List;

public class CashMemoItemDetailsViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<CashMemoItemDetailsResponse>> responseMutableLiveData;


    private CashMemoItemDetailsRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<CashMemoItemDetailsResponse>>getLiveData(){
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
        repository = CashMemoItemDetailsRepository.getInstance();
    }
    CashMemoItemDetailsListener listener = new CashMemoItemDetailsListener() {


        @Override
        public void onSuccess(List<CashMemoItemDetailsResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getItemsList(String retailer_id,String bill_no) {
        isConnecting.setValue(true);  // Show loading state
        repository = CashMemoItemDetailsRepository.getInstance();
        repository.getItemsList(context,retailer_id,bill_no, listener);

    }
}
