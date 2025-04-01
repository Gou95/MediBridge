package com.indosoft.medibridge.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.indosoft.medibridge.Adapter.NotificationAdapter;
import com.indosoft.medibridge.Model.NotificationResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.ViewModel.NotificationViewModel;
import com.indosoft.medibridge.databinding.ActivityNotificationBinding;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;
    NotificationAdapter adapter;
    NotificationViewModel notificationViewModel;
    ArrayList<NotificationResponse> list = new ArrayList<>();
    private NotificationManagerCompat notificationManagerCompat;
    private boolean isListCleared = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.init(this);

        notificationViewModel.notificationList();

        onAttachObservers();
        initclicks();
        //startNetworkService();
        adapter = new NotificationAdapter(this,list);
        binding.recyclerviewNotification.setAdapter(adapter);
        binding.recyclerviewNotification.setLayoutManager(new LinearLayoutManager(this));

        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);

        startNetworkService();
        notificationManagerCompat = NotificationManagerCompat.from(this);

//
//        FirebaseMessaging.getInstance().subscribeToTopic("notification")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = "Subscribed";
//                        if (!task.isSuccessful()) {
//                            msg = "Subscribe failed";
//                        }
//
//                        Toast.makeText(NotificationActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });

    }
    private void initclicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

    }

    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        notificationViewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                list.clear();
                list.addAll(responses);
                adapter.notifyDataSetChanged();

            }
        });

    }
    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
    }

//    private void scheduleNotificationWorker() {
//
//        PeriodicWorkRequest notificationWorkRequest =
//                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
//                        .build();
//
//        WorkManager.getInstance(this).enqueue(notificationWorkRequest);
//    }
}