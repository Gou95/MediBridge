package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.NotificationListener;
import com.indosoft.medibridge.Model.NotificationResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {

    public static NotificationRepository repository;

    private final MutableLiveData<List<NotificationResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static NotificationRepository getInstance() {
        if (repository == null) {
            repository = new NotificationRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public NotificationRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<NotificationResponse>> getNotification(Context context, NotificationListener listener) {
        Call<List<NotificationResponse>> call = apiInterface.getNotification();
        call.enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

//    public void markNotificationsAsSeen(Context context, NotificationListener listener) {
//        // Assuming you have an API that accepts a request to mark notifications as seen
//        Call<Void> call = apiInterface.markNotificationsAsSeen();  // Replace with the correct API method
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    listener.onSuccess(null);  // Notify that notifications are marked as seen
//                } else {
//                    if (listener != null) {
//                        listener.onError("Failed to mark notifications as seen.");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                if (listener != null) {
//                    listener.onError("Something went wrong: " + t.getMessage());
//                }
//            }
//        });
//    }

}
