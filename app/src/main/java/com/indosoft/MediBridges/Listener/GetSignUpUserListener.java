package com.indosoft.MediBridges.Listener;

import com.indosoft.MediBridges.Model.GetSignUpUserResponse;

import java.util.List;

public interface GetSignUpUserListener {
    void onSuccess(List<GetSignUpUserResponse> responses);
    void onError(String error);

}
