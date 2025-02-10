package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Body.AddressUpdateBody;
import com.indosoft.medibridge.Listener.AddressUpdateListener;
import com.indosoft.medibridge.Model.AddressUpdateResponse;
import com.indosoft.medibridge.Repository.AddressUpdateRepository;

public class AddressUpdateViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<AddressUpdateResponse> responseMutableLiveData;


    private AddressUpdateRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<AddressUpdateResponse>getLiveData(){
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
        repository = AddressUpdateRepository.getInstance();
    }
    AddressUpdateListener listener = new AddressUpdateListener() {


        @Override
        public void onSuccess(AddressUpdateResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getUpdateAddress(String retailer_id, AddressUpdateBody body) {
        isConnecting.setValue(true);  // Show loading state
        repository = AddressUpdateRepository.getInstance();
        repository.getAddress(context,retailer_id,body, listener);

    }
}
