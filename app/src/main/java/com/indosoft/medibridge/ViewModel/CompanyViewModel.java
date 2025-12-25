package com.indosoft.medibridgestockist.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridgestockist.Listener.CityListener;
import com.indosoft.medibridgestockist.Listener.CompanyListener;
import com.indosoft.medibridgestockist.Model.CityResponse;
import com.indosoft.medibridgestockist.Model.CompanyResponse;
import com.indosoft.medibridgestockist.Repository.CityRepository;
import com.indosoft.medibridgestockist.Repository.CompanyRepository;

import java.util.List;

public class CompanyViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<CompanyResponse>> responseMutableLiveData;


    private CompanyRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<CompanyResponse>>getLiveData(){
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
        repository = CompanyRepository.getInstance();
    }
    CompanyListener listener = new CompanyListener() {


        @Override
        public void onSuccess(List<CompanyResponse> response) {
            responseMutableLiveData.setValue(response);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void getCompany(String dealer_id) {
        isConnecting.setValue(true);  // Show loading state
        repository = CompanyRepository.getInstance();
        repository.getCompany(context,dealer_id, listener);

    }
}
