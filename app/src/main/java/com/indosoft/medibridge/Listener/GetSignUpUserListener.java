package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.GetSignUpUserResponse;

import java.util.List;

public interface GetSignUpUserListener {
    void onSuccess(List<GetSignUpUserResponse> responses);
    void onError(String error);

}
