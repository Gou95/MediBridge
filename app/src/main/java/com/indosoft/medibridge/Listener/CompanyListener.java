package com.indosoft.medibridgestockist.Listener;

import com.indosoft.medibridgestockist.Model.CompanyResponse;

import java.util.List;

public interface CompanyListener {
    void onSuccess(List<CompanyResponse> responses);
    void onError(String error);
}
