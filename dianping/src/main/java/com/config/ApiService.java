package com.config;

import com.config.Constants;
import com.danertu.entity.OrderHead;
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
     *
     * @param apiId 0328
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
     * @param apiId  0337
     * @param memLoginId 登录id
     * @param pageIndex 页码
     * @param pageSize  页容量
     * @param orderType 订单类型  0--全部，1--待付款，2--待发货，3--待收货，4--退款
     * @return
     */
    @POST(Constants.API_ADDRESS)
    @FormUrlEncoded
    Call<OrderHead> getOrderByType(@Field("apiid")String apiId,
                                   @Field("memLoginId")String memLoginId,
                                   @Field("pageIndex")int pageIndex,
                                   @Field("pageSize")int pageSize,
                                   @Field("orderType")int orderType);
}
