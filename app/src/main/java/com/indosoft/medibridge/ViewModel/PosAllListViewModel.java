package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.PosAllListListener;
import com.indosoft.medibridge.Listener.PosDetailsListener;
import com.indosoft.medibridge.Model.PosAllListResponse;
import com.indosoft.medibridge.Model.PosDetailsResponse;
import com.indosoft.medibridge.Repository.PosAllListRepository;
import com.indosoft.medibridge.Repository.PosDetailsRepository;

import java.util.List;

public class PosAllListViewModel extends ViewModel {
    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<PosAllListResponse>> responseMutableLiveData;


    private PosAllListRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<PosAllListResponse>>getLiveData(){
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
        repository = PosAllListRepository.getInstance();
    }
    PosAllListListener listener = new PosAllListListener() {
        @Override
        public void onSuccess(List<PosAllListResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void allPosList(String retailer_id,String addtime) {
        isConnecting.setValue(true);  // Show loading state
        repository = PosAllListRepository.getInstance();
        repository.allPosList(context,retailer_id,addtime, listener);

    }
}
