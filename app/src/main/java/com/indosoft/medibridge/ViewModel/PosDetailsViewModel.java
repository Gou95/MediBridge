package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.PosDetailsListener;
import com.indosoft.medibridge.Listener.PostProductListener;
import com.indosoft.medibridge.Model.GetPosProductResponse;
import com.indosoft.medibridge.Model.PosDetailsResponse;
import com.indosoft.medibridge.Repository.PosDetailsRepository;
import com.indosoft.medibridge.Repository.PosProductRepository;

import java.util.List;

public class PosDetailsViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<PosDetailsResponse>> responseMutableLiveData;


    private PosDetailsRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<PosDetailsResponse>>getLiveData(){
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
        repository = PosDetailsRepository.getInstance();
    }
    PosDetailsListener listener = new PosDetailsListener() {
        @Override
        public void onSuccess(List<PosDetailsResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void posList(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = PosDetailsRepository.getInstance();
        repository.posList(context,retailer_id, listener);

    }
}
