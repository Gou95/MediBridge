package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Body.AddtoCartBody;
import com.indosoft.MediBridges.Body.SignUpBody;
import com.indosoft.MediBridges.Listener.AddtoCartListener;
import com.indosoft.MediBridges.Listener.SignUpListener;
import com.indosoft.MediBridges.Model.AddtoCartResponse;
import com.indosoft.MediBridges.Model.SignUpResponse;
import com.indosoft.MediBridges.Repository.AddtoCartRepository;
import com.indosoft.MediBridges.Repository.SignUpRepository;

public class AddtoCartViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<AddtoCartResponse> responseMutableLiveData;


    private AddtoCartRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<AddtoCartResponse>getLiveData(){
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
        repository = AddtoCartRepository.getInstance();
    }
    AddtoCartListener listener = new AddtoCartListener() {


        @Override
        public void onSuccess(AddtoCartResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getAddToCardData(AddtoCartBody body) {
        isConnecting.setValue(true);  // Show loading state
        repository = AddtoCartRepository.getInstance();
        repository.getAddCartData(context,body, listener);

    }


}
