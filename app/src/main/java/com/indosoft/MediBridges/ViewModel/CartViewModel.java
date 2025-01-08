package com.indosoft.MediBridges.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.MediBridges.Listener.CartListener;
import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Repository.CartRepository;

import java.util.List;

public class CartViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<CardListResponse>> responseMutableLiveData;


    private CartRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<CardListResponse>>getLiveData(){
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
        repository = CartRepository.getInstance();
    }
    CartListener listener = new CartListener() {


        @Override
        public void onSuccess(List<CardListResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
       isFailed.setValue(error);
        }
    };
        public void getModelData() {
            isConnecting.setValue(true);  // Show loading state
            repository = CartRepository.getInstance();
            repository.getCartLiveData(context, listener);

        }


    }
