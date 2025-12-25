package com.indosoft.medibridge.Repository;

import com.indosoft.medibridge.Listener.MedicineListListener;
import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineRepository {

    private static MedicineRepository repository;
    private final ApiInterface apiInterface;

    // 🔥 VERY IMPORTANT: keep reference of last call
    private Call<MedicineListResponse> currentCall;

    public static MedicineRepository getInstance() {
        if (repository == null) {
            repository = new MedicineRepository();
        }
        return repository;
    }

    private MedicineRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    // ✅ SERVER SIDE SEARCH (FAST & SAFE)
    public void searchMedicine(
            String search,
            MedicineListListener listener
    ) {

        // ✅ cancel previous API call
        if (currentCall != null && currentCall.isExecuted()) {
            currentCall.cancel();
        }

        // ✅ make new call
        currentCall = apiInterface.medicineList(search);

        currentCall.enqueue(new Callback<MedicineListResponse>() {
            @Override
            public void onResponse(
                    Call<MedicineListResponse> call,
                    Response<MedicineListResponse> response
            ) {

                if (listener == null) return;

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null) {

                    List<MedicineListResponse.Datum> list =
                            response.body().getData();

                    listener.onSuccess(list);

                } else {
                    listener.onError("No medicine found");
                }
            }

            @Override
            public void onFailure(
                    Call<MedicineListResponse> call,
                    Throwable t
            ) {
                if (listener == null) return;

                // ❌ ignore cancelled calls
                if (call.isCanceled()) return;

                listener.onError(t.getMessage());
            }
        });
    }
}
