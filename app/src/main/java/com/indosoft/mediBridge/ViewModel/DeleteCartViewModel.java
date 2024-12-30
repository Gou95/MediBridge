package com.indosoft.mediBridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.DealerListener;
import com.indosoft.mediBridge.Listener.DeleteCartListener;
import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Model.DeleteCartResponse;
import com.indosoft.mediBridge.Repository.DealersRepository;
import com.indosoft.mediBridge.Repository.DeleteCartRepository;

import java.util.List;

public class DeleteCartViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<DeleteCartResponse> responseMutableLiveData;


    private DeleteCartRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<DeleteCartResponse>getLiveData(){
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
        repository = DeleteCartRepository.getInstance();
    }
    DeleteCartListener listener = new DeleteCartListener() {
        @Override
        public void onSuccess(DeleteCartResponse response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void deleteCartData(String cart_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = DeleteCartRepository.getInstance();
        repository.getDeleteData(context,cart_id, listener);

    }

}
