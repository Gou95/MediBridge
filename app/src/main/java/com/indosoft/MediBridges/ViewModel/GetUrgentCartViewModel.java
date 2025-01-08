package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.GetSignUpUserListener;
import com.indosoft.MediBridges.Listener.GetUrgentCartListener;
import com.indosoft.MediBridges.Model.GetSignUpUserResponse;
import com.indosoft.MediBridges.Model.GetUrgentCartResponse;
import com.indosoft.MediBridges.Repository.GetSignUpUserRepository;
import com.indosoft.MediBridges.Repository.GetUrgentCartRepository;

import java.util.List;

public class GetUrgentCartViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<GetUrgentCartResponse>> responseMutableLiveData;


    private GetUrgentCartRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<GetUrgentCartResponse>>getLiveData(){
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
        repository = GetUrgentCartRepository.getInstance();
    }
    GetUrgentCartListener listener = new GetUrgentCartListener() {
        @Override
        public void onSuccess(List<GetUrgentCartResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getRemoveAllCartData(String retailer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = GetUrgentCartRepository.getInstance();
        repository.getAllRemoveCartData(context,retailer_id, listener);

    }
}
