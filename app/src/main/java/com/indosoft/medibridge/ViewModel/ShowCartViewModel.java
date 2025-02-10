package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.ShowCartListener;
import com.indosoft.medibridge.Model.ShowCartResponse;
import com.indosoft.medibridge.Repository.ShowCartRepository;

import java.util.List;

public class ShowCartViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<ShowCartResponse>> responseMutableLiveData;


    private ShowCartRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<ShowCartResponse>>getLiveData(){
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
        repository = ShowCartRepository.getInstance();
    }
    ShowCartListener listener = new ShowCartListener() {
        @Override
        public void onSuccess(List<ShowCartResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getShowPostCartData(String retailer_id) {
        isConnecting.setValue(true);
        repository = ShowCartRepository.getInstance();
        repository.getShowCartData(context,retailer_id, listener);

    }



    public void getCartData() {
        isConnecting.setValue(true);
        repository = ShowCartRepository.getInstance();
        repository.getShowGetCartData(context, listener);

    }
}
