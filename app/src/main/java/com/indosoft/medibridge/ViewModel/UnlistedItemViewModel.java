package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Body.UnlistedItemBody;
import com.indosoft.medibridge.Listener.SignUpListener;
import com.indosoft.medibridge.Listener.UnlistedItemListener;
import com.indosoft.medibridge.Model.SignUpResponse;
import com.indosoft.medibridge.Model.UnlistedItemResponse;
import com.indosoft.medibridge.Repository.SignUpRepository;
import com.indosoft.medibridge.Repository.UnlistedItemRepository;

public class UnlistedItemViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<UnlistedItemResponse> responseMutableLiveData;


    private UnlistedItemRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<UnlistedItemResponse>getLiveData(){
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
        repository = UnlistedItemRepository.getInstance();
    }
    UnlistedItemListener listener = new UnlistedItemListener() {


        @Override
        public void onSuccess(UnlistedItemResponse response) {
            if (responseMutableLiveData != null) {  // ✅ Added null check
                responseMutableLiveData.postValue(response);
            }
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void unlistedItemAdd(UnlistedItemBody body) {
        isConnecting.setValue(true);
        repository = UnlistedItemRepository.getInstance();
        repository.unlistedItemAdd(context,body, listener);
    }
}
