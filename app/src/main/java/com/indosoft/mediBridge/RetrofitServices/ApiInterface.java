package com.indosoft.mediBridge.RetrofitServices;

import com.indosoft.mediBridge.Body.AddtoCartBody;
import com.indosoft.mediBridge.Body.SignUpBody;
import com.indosoft.mediBridge.Body.UserUpdateBody;
import com.indosoft.mediBridge.Model.AddtoCartResponse;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.CityDealerResponse;
import com.indosoft.mediBridge.Model.DealersResponse;
import com.indosoft.mediBridge.Model.DeleteCartResponse;
import com.indosoft.mediBridge.Model.GetSignUpUserResponse;
import com.indosoft.mediBridge.Model.GetUrgentCartResponse;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.LoginResponse;
import com.indosoft.mediBridge.Model.MedicineListResponse;
import com.indosoft.mediBridge.Model.ShowCartResponse;
import com.indosoft.mediBridge.Model.SignUpResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.Model.UnitResponse;
import com.indosoft.mediBridge.Model.UrgentCartResponse;
import com.indosoft.mediBridge.Model.UrgentDeleteResponse;
import com.indosoft.mediBridge.Model.UserUpdateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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


    @POST("login.php")
    Call<List<LoginResponse>> loginRes(@Query("retailer_phone") String retailer_phone,@Query("retailer_password") String retailer_password);

    @POST("users.php")
    Call<SignUpResponse> getRegisterRes(@Body SignUpBody body);

    @POST("cart.php")
    Call<AddtoCartResponse> getAddToCartRes(@Body AddtoCartBody body);


    @PUT("users.php")
    Call<UserUpdateResponse> updateUsers(@Body UserUpdateBody body);

    @PUT("showcart.php")
    Call<UrgentCartResponse> urgentCart(@Query("cart_id") String cart_id);

    @DELETE("showcart.php")
    Call<DeleteCartResponse> deleteCart(@Query("cart_id") String cart_id );

    @POST("urgentcart.php")
    Call<List<GetUrgentCartResponse>> removeUrgentCart(@Query("retailer_id") String retailer_id);

    @POST("citystockist.php")
    Call<List<CityDealerResponse>> cityDealer(@Query("city_id") String city_id);

    @DELETE("urgentcart.php")
    Call<UrgentDeleteResponse> deleteUrgentCart(@Query("cart_id") String cart_id);
}
