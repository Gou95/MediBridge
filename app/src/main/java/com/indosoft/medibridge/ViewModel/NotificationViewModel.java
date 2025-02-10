package com.indosoft.medibridge.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.indosoft.medibridge.Listener.NotificationListener;
import com.indosoft.medibridge.Model.NotificationResponse;
import com.indosoft.medibridge.Repository.NotificationRepository;

import java.util.List;

public class NotificationViewModel extends ViewModel {

    private Context context;

    private MutableLiveData<String> isFailed = new MutableLiveData<>();

    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();

    private MutableLiveData<List<NotificationResponse>> responseMutableLiveData;

    private final MutableLiveData<Integer> unreadCount = new MutableLiveData<>(0);
    private NotificationRepository repository;

    public LiveData<String> getIsFailed(){
        return isFailed;
    }

    public LiveData<Boolean>getIsConnecting(){
        return isConnecting;


    }

    public LiveData<List<NotificationResponse>>getLiveData(){
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
        repository = NotificationRepository.getInstance();
    }
    NotificationListener listener = new NotificationListener() {
        @Override
        public void onSuccess(List<NotificationResponse> response) {
            responseMutableLiveData.setValue(response);
            int count = 0;
            for (NotificationResponse notification : response) {
                if (!notification.getId().equals("32")) {
                    count++;
                }
            }
            unreadCount.setValue(count);
        }

        @Override
        public void onError(String error) {
            isFailed.setValue(error);
        }
    };
    public void notificationList() {
        isConnecting.setValue(true);  // Show loading state
        repository = NotificationRepository.getInstance();
        repository.getNotification(context, listener);

    }

//    public void markNotificationsAsSeen() {
//        repository.markNotificationsAsSeen(context, success -> {
//            if (success) {
//                unreadCount.setValue(0); // Reset unread count
//            }
//        });
//    }
    }

