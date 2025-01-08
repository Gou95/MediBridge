package com.indosoft.MediBridges.RetrofitServices;

import com.indosoft.MediBridges.Body.AddtoCartBody;
import com.indosoft.MediBridges.Body.ExitMobileBody;
import com.indosoft.MediBridges.Body.OrderDetailsBody;
import com.indosoft.MediBridges.Body.SignUpBody;
import com.indosoft.MediBridges.Body.UserUpdateBody;
import com.indosoft.MediBridges.Model.AddtoCartResponse;
import com.indosoft.MediBridges.Model.CardListResponse;
import com.indosoft.MediBridges.Model.CityDealerResponse;
import com.indosoft.MediBridges.Model.DealersResponse;
import com.indosoft.MediBridges.Model.DeleteCartResponse;
import com.indosoft.MediBridges.Model.DeliveryDayResponse;
import com.indosoft.MediBridges.Model.ExitMobileResponse;
import com.indosoft.MediBridges.Model.GetSignUpUserResponse;
import com.indosoft.MediBridges.Model.GetUrgentCartResponse;
import com.indosoft.MediBridges.Model.IndiaStateResponse;
import com.indosoft.MediBridges.Model.LastOrderResponse;
import com.indosoft.MediBridges.Model.LastStockitsResponse;
import com.indosoft.MediBridges.Model.LoginResponse;
import com.indosoft.MediBridges.Model.MedicineListResponse;
import com.indosoft.MediBridges.Model.OrderDetailsResponse;
import com.indosoft.MediBridges.Model.OrderListResponse;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Model.ProceedOrderResponse;
import com.indosoft.MediBridges.Model.QuantityChangeResponse;
import com.indosoft.MediBridges.Model.RecentStockitsResponse;
import com.indosoft.MediBridges.Model.ShowCartResponse;
import com.indosoft.MediBridges.Model.SignUpResponse;
import com.indosoft.MediBridges.Model.StateCityResponse;
import com.indosoft.MediBridges.Model.StockitsResponse;
import com.indosoft.MediBridges.Model.UnitResponse;
import com.indosoft.MediBridges.Model.UrgentCartResponse;
import com.indosoft.MediBridges.Model.UrgentDeleteResponse;
import com.indosoft.MediBridges.Model.UrgentProceedResponse;
import com.indosoft.MediBridges.Model.UserUpdateResponse;

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
    Call<List<LoginResponse>> loginRes(@Query("retailer_phone") String retailer_phone,@Query("retailer_password") String retailer_password);

    @POST("users.php")
    Call<SignUpResponse> getRegisterRes(@Body SignUpBody body);

    @POST("cart.php")
    Call<AddtoCartResponse> getAddToCartRes(@Body AddtoCartBody body);


    @PUT("users.php")
    Call<UserUpdateResponse> updateUsers(@Body UserUpdateBody body);


    @PUT("showcart.php")
    Call<UrgentCartResponse> urgentCart(@Query("cart_id") String cart_id);

    @PUT("cartqtychange.php")
    Call<QuantityChangeResponse> qtyChange(@Query("cart_id") String cart_id, @Query("product_id") String product_id, @Query("qty") String qty);
    @DELETE("showcart.php")
    Call<DeleteCartResponse> deleteCart(@Query("cart_id") String cart_id );

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
    Call<OrderResponse> orderRes(@Query("retailer_id") String retailer_id,@Query("order_no") String order_no);

    @POST("orderdetails.php")
    Call<List<OrderDetailsResponse>> getOrderDetails(@Query("retailer_id") String retailer_id,@Query("order_no") String order_no);
    @DELETE("urgentcart.php")
    Call<UrgentDeleteResponse> deleteUrgentCart(@Query("cart_id") String cart_id);

    @POST("laststockist.php")
    Call<List<LastStockitsResponse>> lastStockits(@Query("retailer_id") String retailer_id,@Query("product_id") String product_id);

    @POST("recentstockist.php")
    Call<List<RecentStockitsResponse>> recentStockits(@Query("retailer_id") String retailer_id);

    @POST("stockistorderdetails.php")
    Call<List<StockitsResponse>> stockitsList(@Query("retailer_id") String retailer_id,@Query("dealer_id") String dealer_id);

    @PUT("updatedeliveryday.php")
    Call<DeliveryDayResponse> deliveryDay(@Query("cart_id") String cart_id, @Query("delivery_day") String delivery_day);
}
