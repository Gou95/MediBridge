package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.NotificationResponse;

import java.util.List;

public interface NotificationListener {
    void onSuccess(List<NotificationResponse> responses);
    void onError(String error);
}
