package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.PlansListener;
import com.indosoft.medibridge.Listener.PostProductListener;
import com.indosoft.medibridge.Model.GetPosProductResponse;
import com.indosoft.medibridge.Model.PlansResponse;
import com.indosoft.medibridge.Repository.PlansRepository;
import com.indosoft.medibridge.Repository.PosProductRepository;

import java.util.List;

public class PosProductViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<GetPosProductResponse>> responseMutableLiveData;


    private PosProductRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<GetPosProductResponse>>getLiveData(){
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
        repository = PosProductRepository.getInstance();
    }
    PostProductListener listener = new PostProductListener() {
        @Override
        public void onSuccess(List<GetPosProductResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getPosList(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = PosProductRepository.getInstance();
        repository.getPosList(context,retailer_id, listener);

    }
}
