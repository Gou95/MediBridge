package com.indosoft.mediBridge.RetrofitServices;

import com.indosoft.mediBridge.Body.AddtoCartBody;
import com.indosoft.mediBridge.Body.SignUpBody;
import com.indosoft.mediBridge.Model.AddtoCartResponse;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Model.GetSignUpUserResponse;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.LoginResponse;
import com.indosoft.mediBridge.Model.MedicineListResponse;
import com.indosoft.mediBridge.Model.ShowCartResponse;
import com.indosoft.mediBridge.Model.SignUpResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.Model.UnitResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("state.php")
    Call<List<CardListResponse>> cartList();

    @GET("state.php")
    Call<List<IndiaStateResponse>> stateList();

    @GET("dealers.php")
    Call<List<DealersResponse>> dealersList();
    @GET("uom.php")
    Call<List<UnitResponse>> unitList();

    @GET("items.php")
    Call<List<MedicineListResponse>> medicineList();

    @GET("users.php")
    Call<List<GetSignUpUserResponse>> getUserList();

    @GET("showcart.php")
    Call<List<ShowCartResponse>> showGetCartList();
    @FormUrlEncoded
    @POST("city.php")
    Call<List<StateCityResponse>> cityList(@Field("state_id") String state_id);
//    @FormUrlEncoded
//    @POST("showcart.php")
//    Call<List<ShowCartResponse>> showCartList(@Field("retailer_id") String retailer_id);

//    @GET("showcart.php")
//    Call<List<ShowCartResponse>> showCartList(@Query("retailer_id") String retailer_id);

    @POST("showcart.php")
    Call<List<ShowCartResponse>> showCartList(@Query("retailer_id") String retailer_id);

    @FormUrlEncoded
    @POST("login.php")
    Call<List<LoginResponse>> loginRes(@Field("mobile") String mobile,@Field("password") String password);

    @POST("users.php")
    Call<SignUpResponse> getRegisterRes(@Body SignUpBody body);

    @POST("cart.php")
    Call<AddtoCartResponse> getAddToCartRes(@Body AddtoCartBody body);

}
