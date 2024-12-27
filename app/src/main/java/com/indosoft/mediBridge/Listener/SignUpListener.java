package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.SignUpResponse;

import java.util.List;

public interface SignUpListener {
    void onSuccess(SignUpResponse response);
    void onError(String error);
}
