package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Model.SignUpResponse;

public interface SignUpListener {
    void onSuccess(SignUpResponse response);
    void onError(String error);
}
