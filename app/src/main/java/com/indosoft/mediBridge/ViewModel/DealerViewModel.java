package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.CartListener;
import com.indosoft.mediBridge.Listener.DealerListener;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Repository.CartRepository;
import com.indosoft.mediBridge.Repository.DealersRepository;

import java.util.List;

public class DealerViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<DealersResponse>> responseMutableLiveData;


    private DealersRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<DealersResponse>>getLiveData(){
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
        repository = DealersRepository.getInstance();
    }
    DealerListener listener = new DealerListener() {
        @Override
        public void onSuccess(List<DealersResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getDealerData() {
        isConnecting.setValue(true);  // Show loading state
        repository = DealersRepository.getInstance();
        repository.getDealersData(context, listener);

    }


}
