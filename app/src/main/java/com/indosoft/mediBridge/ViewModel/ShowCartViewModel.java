package com.indosoft.mediBridge.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.mediBridge.Listener.MedicineListListener;
import com.indosoft.mediBridge.Listener.ShowCartListener;
import com.indosoft.mediBridge.Model.MedicineListResponse;
import com.indosoft.mediBridge.Model.ShowCartResponse;
import com.indosoft.mediBridge.Repository.MedicineRepository;
import com.indosoft.mediBridge.Repository.ShowCartRepository;

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
