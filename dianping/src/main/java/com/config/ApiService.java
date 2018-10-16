package com.config;

import com.danertu.entity.BaseResultBean;
import com.danertu.entity.ChooseCouponBean;
import com.danertu.entity.CouponAllBean;
import com.danertu.entity.CouponBean;
import com.danertu.entity.CouponCountBean;
import com.danertu.entity.CouponDetailBean;
import com.danertu.entity.CouponProductsBean;
import com.danertu.entity.LeaderBean;
import com.danertu.entity.MyCouponBean;
import com.danertu.entity.NewOrderHeadBean;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.entity.QuanYanProductCategory;
import com.danertu.entity.ShopDetailBean;
import com.danertu.entity.ShopStateBean;
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
    /**
     * 需要授权
     * 获取订单头
     *
     * @param apiId       0036
     * @param orderNumber 订单号
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<OrderHead> getOrderHead(@Field("apiid") String apiId,
                                 @Field("ordernumber") String orderNumber);

    /**
     * 不需要授权
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
                                       @Field("la") String la,
                                       @Field("lt") String lt,
                                       @Field("shopid") String shopId);

    /**
     * 需要授权
     * 获取订单体
     *
     * @param apiId       0072
     * @param orderNumber 订单号
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<OrderBody> getOrderBody(@Field("apiid") String apiId,
                                 @Field("ordernumber") String orderNumber);

    /**
     * 需要授权
     * 取消订单
     *
     * @param apiId       0075
     * @param orderNumber 订单号
     * @return true:取消订单成功，false:取消失败
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<String> cancelOrder(@Field("apiid") String apiId,
                             @Field("ordernumber") String orderNumber);

    /**
     * 需要授权
     * 确认收货
     *
     * @param apiId       0076
     * @param orderNumber 订单号
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<String> sureTakeGoods(@Field("apiid") String apiId,
                               @Field("ordernumber") String orderNumber);


    /**
     * 不需要授权
     * 检查当前登录用户是否为店主
     *
     * @param apiId  0141
     * @param shopId 登录id
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<ShopStateBean> checkShopState(@Field("apiid") String apiId,
                                       @Field("shopid") String shopId);

    /**
     * 不需要授权
     * 获取上级店铺信息
     *
     * @param apiId  0245
     * @param shopId
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<LeaderBean> getLeaderInfo(@Field("apiid") String apiId,
                                   @Field("shopid") String shopId);

    /**
     * 需要授权
     * 获取囤货列表
     *
     * @param apiId             0325
     * @param memLoginId
     * @param pageIndex
     * @param pageSize
     * @param productCategoryId
     * @param orderBy
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<WarehouseBean> getStockList(@Field("apiid") String apiId,
                                     @Field("memLoginId") String memLoginId,
                                     @Field("orderBy") String orderBy,
                                     @Field("pageIndex") int pageIndex,
                                     @Field("pageSize") int pageSize,
                                     @Field("productCategoryId") String productCategoryId);

    /**
     * 需要授权
     * 获取囤货订单列表
     *
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
     * 需要授权
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
    Call<NewOrderHeadBean> getOrderByType(@Field("apiid") String apiId,
                                          @Field("memLoginId") String memLoginId,
                                          @Field("orderType") String orderType,
                                          @Field("pageIndex") int pageIndex,
                                          @Field("pageSize") int pageSize);

    /**
     * 不需要授权
     * 获取温泉产品分类
     *
     * @param apiId       0338
     * @param productGuid 产品guid
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<QuanYanProductCategory> getQuanYanProductCategory(@Field("apiid") String apiId,
                                                           @Field("productGuid") String productGuid);

    /**
     * 不需要授权
     * 获取可领的温泉/酒水/商城优惠券
     *
     * @param apiId     0341
     * @param useScope  1-温泉券，2-酒水券，0-商城券
     * @param pageIndex 页码
     * @param pageSize  页容量
     * @return
     */
    @POST(Constants.API_ADDRESS)
//    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<CouponBean> getCouponCenterList(@Field("apiid") String apiId,
                                         @Field("memLoginId") String memLoginId,
                                         @Field("pageIndex") int pageIndex,
                                         @Field("pageSize") int pageSize,
                                         @Field("useScope") String useScope);

    /**
     * 需要授权
     * 领取指定优惠券
     *
     * @param apiId      0342
     * @param shopId     店铺id
     * @param memLoginId 登录id
     * @param couponGuid 优惠券guid
     * @return
     */
    @POST(Constants.API_ADDRESS)
//    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<BaseResultBean> getCoupon(@Field("apiid") String apiId,
                                   @Field("couponGuid") String couponGuid,
                                   @Field("memLoginId") String memLoginId,
                                   @Field("shopId") String shopId);

    /**
     * 需要授权
     * 获取指定用户的已领取/已使用/已过期优惠券
     *
     * @param apiId            0343
     * @param memLoginId       用户id
     * @param isUsedOrIsDelete 0-个人优惠券列表，1-使用记录列表，2-已过期列表
     * @param pageIndex        页码
     * @param pageSize         页容量
     * @return
     */
    @POST(Constants.API_ADDRESS)
//    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<MyCouponBean> getMyCouponList(@Field("apiid") String apiId,
                                       @Field("isUsedOrIsDelete") String isUsedOrIsDelete,
                                       @Field("memLoginId") String memLoginId,
                                       @Field("pageIndex") int pageIndex,
                                       @Field("pageSize") int pageSize);

    /**
     * 需要授权
     * 获取商品可用的优惠券
     *
     * @param apiId      0344
     * @param multiParam 混合参数    [{"productGuids":["ff6c4fa4-f891-4dac-bd86-523abbeb6235"],"shopId":"13925340017"},{"productGuids":["dd70d3e2-580b-4531-90d1-07e04ef8cced"],"shopId":"13557013342"}]
     * @param memLoginId 登录id
     * @return
     */
    @POST(Constants.API_ADDRESS)
//    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<ChooseCouponBean> chooseCoupon(@Field("apiid") String apiId,
                                        @Field("memLoginId") String memLoginId,
                                        @Field("multiParam") String multiParam);

    /**
     * 需要授权
     * 获取指定优惠券适用的商品列表
     *
     * @param apiId      0345
     * @param couponGuid 优惠券guid
     * @return
     */
    @POST(Constants.API_ADDRESS)
//    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<CouponProductsBean> getProductsFromCoupon(@Field("apiid") String apiId,
                                                   @Field("couponGuid") String couponGuid,
                                                   @Field("memLoginId") String memLoginId);

    /**
     * 需要授权
     * 获取优惠券数量
     *
     * @param apiId      0346
     * @param memLoginId 登录id
     * @return
     */
    @POST(Constants.API_ADDRESS)
//    @POST("http://192.168.1.195:8081/ApiTest/RequestApi.aspx")
    @FormUrlEncoded
    Call<CouponCountBean> getCouponCount(@Field("apiid") String apiId,
                                         @Field("memLoginId") String memLoginId);

    /**
     * 不需要授权
     * 获取已领取的优惠券详细
     *
     * @param apiId            0347
     * @param couponRecordGuid
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<CouponBean> getCouponDetail(@Field("apiid") String apiId,
                                     @Field("couponRecordGuid") String couponRecordGuid,
                                     @Field("memLoginId") String memLoginId);

    /**
     * 获取未领取的优惠券详细
     *
     * @param apiId      0348
     * @param couponGuid
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<CouponBean> getCouponDetail(@Field("apiid") String apiId,
                                     @Field("couponGuid") String couponGuid);


}

