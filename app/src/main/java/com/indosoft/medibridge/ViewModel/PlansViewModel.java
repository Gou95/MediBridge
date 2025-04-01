package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.OrderListListener;
import com.indosoft.medibridge.Listener.PlansListener;
import com.indosoft.medibridge.Model.OrderListResponse;
import com.indosoft.medibridge.Model.PlansResponse;
import com.indosoft.medibridge.Repository.OrderListRepository;
import com.indosoft.medibridge.Repository.PlansRepository;

import java.util.List;

public class PlansViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<PlansResponse>> responseMutableLiveData;


    private PlansRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<PlansResponse>>getLiveData(){
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
        repository = PlansRepository.getInstance();
    }
    PlansListener listener = new PlansListener() {
        @Override
        public void onSuccess(List<PlansResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void plansList() {
        isConnecting.setValue(true);  // Show loading state
        repository = PlansRepository.getInstance();
        repository.getPlans(context, listener);

    }
}
