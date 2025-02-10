package com.indosoft.medibridge.RetrofitServices;

import com.indosoft.medibridge.Body.AddressUpdateBody;
import com.indosoft.medibridge.Body.AddtoCartBody;
import com.indosoft.medibridge.Body.ExitMobileBody;
import com.indosoft.medibridge.Body.SignUpBody;
import com.indosoft.medibridge.Body.StockistBody;
import com.indosoft.medibridge.Body.UserUpdateBody;
import com.indosoft.medibridge.Model.AddressUpdateResponse;
import com.indosoft.medibridge.Model.AddtoCartResponse;
import com.indosoft.medibridge.Model.CardListResponse;
import com.indosoft.medibridge.Model.CityDealerResponse;
import com.indosoft.medibridge.Model.CounterResponse;
import com.indosoft.medibridge.Model.DealersResponse;
import com.indosoft.medibridge.Model.DeleteCartResponse;
import com.indosoft.medibridge.Model.DeliveryDayResponse;
import com.indosoft.medibridge.Model.ExitMobileResponse;
import com.indosoft.medibridge.Model.GetOtpResponse;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.Model.GetUrgentCartResponse;
import com.indosoft.medibridge.Model.IndiaStateResponse;
import com.indosoft.medibridge.Model.LastOrderResponse;
import com.indosoft.medibridge.Model.LastStockitsResponse;
import com.indosoft.medibridge.Model.LoginResponse;
import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.Model.NotificationResponse;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Model.OrderListResponse;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Model.OrderResponse;
import com.indosoft.medibridge.Model.OtpResponse;
import com.indosoft.medibridge.Model.ProceedOrderResponse;
import com.indosoft.medibridge.Model.QuantityChangeResponse;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.Model.ShowCartResponse;
import com.indosoft.medibridge.Model.SignUpResponse;
import com.indosoft.medibridge.Model.StateCityResponse;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.Model.UrgentCartResponse;
import com.indosoft.medibridge.Model.UrgentDeleteResponse;
import com.indosoft.medibridge.Model.UrgentProceedResponse;
import com.indosoft.medibridge.Model.UserUpdateResponse;

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

    @POST("showcart.php")
    Call<List<ShowCartResponse>> showCartList(@Query("retailer_id") String retailer_id);


    @POST("login.php")
    Call<List<LoginResponse>> loginRes(@Query("retailer_phone") String retailer_phone, @Query("retailer_password") String retailer_password);

    @POST("users.php")
    Call<SignUpResponse> getRegisterRes(@Body SignUpBody body);

    @POST("cart.php")
    Call<AddtoCartResponse> getAddToCartRes(@Body AddtoCartBody body);


    @PUT("users.php")
    Call<UserUpdateResponse> updateUsers(@Query("retailer_id") String retailer_id,
                                         @Body UserUpdateBody body);


    @PUT("showcart.php")
    Call<UrgentCartResponse> urgentCart(@Query("cart_id") String cart_id);

    @PUT("cartqtychange.php")
    Call<QuantityChangeResponse> qtyChange(@Query("cart_id") String cart_id, @Query("product_id") String product_id, @Query("qty") String qty);

    @DELETE("showcart.php")
    Call<DeleteCartResponse> deleteCart(@Query("cart_id") String cart_id);

    @POST("urgentcart.php")
    Call<List<GetUrgentCartResponse>> removeUrgentCart(@Query("retailer_id") String retailer_id);

    @POST("citystockist.php")
    Call<List<CityDealerResponse>> cityDealer(@Query("city_id") String city_id);

    @POST("orderslist.php")
    Call<List<OrderListResponse>> orderList(@Query("retailer_id") String retailer_id);

    @POST("lastorderno.php")
    Call<List<LastOrderResponse>> lastOrder(@Query("retailer_id") String retailer_id);

    @POST("proceedurgentorder.php")
    Call<UrgentProceedResponse> urgentProcced(@Query("retailer_id") String retailer_id);

    @POST("users.php")
    Call<ExitMobileResponse> exitMobile(@Body ExitMobileBody body);

    @POST("proceedorder.php")
    Call<ProceedOrderResponse> proceedOrder(@Query("retailer_id") String retailer_id);

    @POST("orders.php")
    Call<OrderResponse> orderRes(@Query("retailer_id") String retailer_id, @Query("order_no") String order_no);

    @POST("orderdetails.php")
    Call<List<OrderDetailsResponse>> getOrderDetails(@Query("retailer_id") String retailer_id, @Query("order_no") String order_no,
                                                     @Query("dealer_id") String dealer_id, @Query("order_status") String order_status);

    @DELETE("urgentcart.php")
    Call<UrgentDeleteResponse> deleteUrgentCart(@Query("cart_id") String cart_id);

    @POST("laststockist.php")
    Call<List<LastStockitsResponse>> lastStockits(@Query("retailer_id") String retailer_id, @Query("product_id") String product_id);

    @POST("recentstockist.php")
    Call<List<RecentStockitsResponse>> recentStockits(@Query("retailer_id") String retailer_id);

    @POST("stockistorderdetails.php")
    Call<List<StockitsResponse>> stockitsList(@Query("retailer_id") String retailer_id, @Query("dealer_id") String dealer_id);

    @PUT("showcart.php")
    Call<DeliveryDayResponse> deliveryDay(@Query("cart_id") String cart_id, @Query("delivery_day") String delivery_day);

    @GET("notifications.php")
    Call<List<NotificationResponse>> getNotification();

    @GET("notifications.php")
    Call<Void> markNotificationsAsSeen();

    @PUT("addressupdate.php")
    Call<AddressUpdateResponse> addressUpdate(@Query("retailer_id") String retailer_id, @Body AddressUpdateBody body);

    @PUT("generateotp.php")
    Call<OtpResponse> otpGenerate(@Query("retailer_phone") String retailer_phone);

    @GET("generateotp.php")
    Call<GetOtpResponse> getOtp(@Query("retailer_phone") String retailer_phone);

    @POST("orderregister.php")
    Call<List<OrderRegisterResponse>> orderRegister(@Query("retailer_id") String retailer_id);

    @POST("registerstockist.php")
    Call<SignUpResponse> stockistRegister(@Body StockistBody body);
    @GET("cartcounter.php")
    Call<CounterResponse> getCounter(@Query("retailer_id") String retailer_id);

}
