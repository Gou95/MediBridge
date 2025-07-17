package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Body.ExpiryRegisterBody;
import com.indosoft.medibridge.Body.PosAddBody;
import com.indosoft.medibridge.Body.PosUpdateBody;
import com.indosoft.medibridge.Body.RegisterExpiryBody;
import com.indosoft.medibridge.Body.SendEmailBody;
import com.indosoft.medibridge.Body.SignUpBody;
import com.indosoft.medibridge.Body.StockistBody;
import com.indosoft.medibridge.Body.UnlistedBody;
import com.indosoft.medibridge.Body.UpdateStatusBody;
import com.indosoft.medibridge.Listener.SignUpListener;
import com.indosoft.medibridge.Model.SignUpResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpRepository {
    public static SignUpRepository repository;

    private final MutableLiveData<SignUpResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static SignUpRepository getInstance() {
        if (repository == null) {
            repository = new SignUpRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public SignUpRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<SignUpResponse> sendEmail(Context context, SendEmailBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.sendEmail(body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
    public MutableLiveData<SignUpResponse> getSignUpData(Context context,SignUpBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.getRegisterRes(body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> getRegisterStockist(Context context, StockistBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.stockistRegister(body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
    public MutableLiveData<SignUpResponse> getExpiryRegister(Context context, ExpiryRegisterBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.expiryRegister(body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
    public MutableLiveData<SignUpResponse> getUnlisted(Context context, UnlistedBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.unlistedMedicine(body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> uploadRetailerImage(Context context, File imageFile, SignUpListener listener) {
        MutableLiveData<SignUpResponse> mutableLiveData = new MutableLiveData<>();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        Call<SignUpResponse> call = apiInterface.updateRetailerImage(body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Response not successful.");
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> updateStatus(Context context, String retailer_id, UpdateStatusBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.getUpdate(retailer_id,body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                    mutableLiveData.setValue(response.body());
                } else {
                    listener.onError("Response not successful.");
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> expiryStatus(Context context, String retailer_id, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.expiryStatus(retailer_id);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                    mutableLiveData.setValue(response.body());
                } else {
                    listener.onError("Response not successful.");
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> deleteOrder(Context context, String order_items_id, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.deleteOrder(order_items_id);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                    mutableLiveData.setValue(response.body());
                } else {
                    listener.onError("Response not successful.");
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> orderStatus(Context context, String order_items_id, String order_status, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.getOrderStatus(order_items_id,order_status);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> resetPassword(Context context, String retailer_id, String retailer_password, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.resetPassword(retailer_id,retailer_password);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> registerExpiry(Context context, RegisterExpiryBody registerBody, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.registerExpiry(registerBody);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
    public MutableLiveData<SignUpResponse> posAdd(Context context,String retailer_id, PosAddBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.posAdd(retailer_id,body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<SignUpResponse> posUpdate(Context context, String id, PosUpdateBody body, SignUpListener listener) {
        Call<SignUpResponse> call = apiInterface.posUpdate(id,body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }


}
