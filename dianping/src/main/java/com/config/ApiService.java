package com.config;

import com.config.Constants;
import com.danertu.entity.BaseResultBean;
import com.danertu.entity.CouponBean;
import com.danertu.entity.MyCouponBean;
import com.danertu.entity.OrderHead;
import com.danertu.entity.ShopDetailBean;
import com.danertu.entity.WareHouseOrderBean;
import com.danertu.entity.WarehouseBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 接口请求
 * Created by Viz on 2017/12/21.
 */

public interface ApiService {
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<WarehouseBean> getStockList(@Field("apiid") String apiId,
                                     @Field("memLoginId") String memLoginId,
                                     @Field("pageIndex") int pageIndex,
                                     @Field("pageSize") int pageSize,
                                     @Field("productCategoryId") String productCategoryId,
                                     @Field("orderBy") String orderBy);

    /**
     * @param apiId      0328
     * @param memLoginId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<WareHouseOrderBean> getStockOrder(@Field("apiid") String apiId,
                                           @Field("memLoginId") String memLoginId,
                                           @Field("pageIndex") int pageIndex,
                                           @Field("pageSize") int pageSize);

    /**
     * 根据分类获取订单信息
     *
     * @param apiId      0337
     * @param memLoginId 登录id
     * @param pageIndex  页码
     * @param pageSize   页容量
     * @param orderType  订单类型  0--全部，1--待付款，2--待发货，3--待收货，4--退款
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<OrderHead> getOrderByType(@Field("apiid") String apiId,
                                   @Field("memLoginId") String memLoginId,
                                   @Field("pageIndex") int pageIndex,
                                   @Field("pageSize") int pageSize,
                                   @Field("orderType") int orderType);

    /**
     * @param apiId            0343
     * @param memLoginId       用户id
     * @param isUsedOrIsDelete 0-个人优惠券列表，1-使用记录列表，2-已过期列表
     * @param pageIndex        页码
     * @param pageSize         页容量
     * @return
     */
//    @POST(Constants.API_ADDRESS)
    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<MyCouponBean> getMyCouponList(@Field("apiid") String apiId,
                                       @Field("memLoginId") String memLoginId,
                                       @Field("isUsedOrIsDelete") String isUsedOrIsDelete,
                                       @Field("pageIndex") int pageIndex,
                                       @Field("pageSize") int pageSize);

    /**
     * 获取优惠券列表
     *
     * @param apiId     0341
     * @param useScope  1-温泉券，2-酒水券，0-商城券
     * @param pageIndex 页码
     * @param pageSize  页容量
     * @return
     */
//    @POST(Constants.API_ADDRESS)
    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<CouponBean> getCouponCenterList(@Field("apiid") String apiId,
                                         @Field("memLoginId") String memLoginId,
                                         @Field("useScope") String useScope,
                                         @Field("pageIndex") int pageIndex,
                                         @Field("pageSize") int pageSize);

    /**
     * 领取指定优惠券
     *
     * @param apiId      0342
     * @param memLoginId 登录id
     * @param couponGuid 优惠券guid
     * @return
     */
//    @POST(Constants.API_ADDRESS)
    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<BaseResultBean> getCoupon(@Field("apiid") String apiId,
                                   @Field("memLoginId") String memLoginId,
                                   @Field("couponGuid") String couponGuid);

    /**
     * 获取店铺详细信息
     *
     * @param apiId  0041
     * @param shopId
     * @param la
     * @param lt
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<ShopDetailBean> getShopDetail(@Field("apiid") String apiId,
                                       @Field("shopid") String shopId,
                                       @Field("la") String la,
                                       @Field("lt") String lt);
}
