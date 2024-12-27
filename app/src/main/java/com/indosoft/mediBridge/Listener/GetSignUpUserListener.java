package com.indosoft.mediBridge.Listener;

import com.indosoft.mediBridge.Model.GetSignUpUserResponse;

import java.util.List;

public interface GetSignUpUserListener {
    void onSuccess(List<GetSignUpUserResponse> responses);
    void onError(String error);

}
