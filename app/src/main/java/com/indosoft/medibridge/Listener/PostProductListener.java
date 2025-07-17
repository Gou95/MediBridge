package com.indosoft.medibridge.Listener;

import com.indosoft.medibridge.Model.GetPosProductResponse;

import java.util.List;

public interface PostProductListener {
    void onSuccess(List<GetPosProductResponse> responses);
    void onError(String error);
}
