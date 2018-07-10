package com.danertu.dianping.fragment.stockorder;

import android.os.Handler;

import com.danertu.base.BaseModel;
import com.danertu.entity.WareHouseOrderBean;
import com.config.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danertu.dianping.fragment.stockorder.StockOrderPresenter.MSG_GET_DATA_FAIL;
import static com.danertu.dianping.fragment.stockorder.StockOrderPresenter.MSG_GET_DATA_SUCCESS;
import static com.danertu.dianping.fragment.stockorder.StockOrderPresenter.MSG_LOAD_MORE_FAIL;
import static com.danertu.dianping.fragment.stockorder.StockOrderPresenter.MSG_LOAD_MORE_SUCCESS;
import static com.danertu.dianping.fragment.stockorder.StockOrderPresenter.MSG_NO_MORE_DATA;

/**
 * Created by Viz on 2017/12/21.
 */

public class StockOrderModel extends BaseModel {
    private List<WareHouseOrderBean.WareHouseOrderListBean> orderLists;
    private String totalPage;
    private String totalCount;

    public StockOrderModel() {
        super();
        orderLists = new ArrayList<>();
    }

    /**
     * 获取提货订单
     * @see 注意：这里的分页当要加载的页数大于总页数时，接口还是会返回最后一页的数据，所以需要特殊处理
     *
     * @param handler
     * @param menLoginId
     * @param pageIndex
     * @param pageSize
     */
    public void getStockOrder(final Handler handler, String menLoginId, final int pageIndex, int pageSize) {
        Call<WareHouseOrderBean> call = retrofit.create(ApiService.class).getStockOrder("0328", menLoginId, pageIndex, pageSize);
        call.enqueue(new Callback<WareHouseOrderBean>() {
            @Override
            public void onResponse(Call<WareHouseOrderBean> call, Response<WareHouseOrderBean> response) {
                if (response.code() != RESULT_OK) {
                    handler.sendEmptyMessage(MSG_SERVER_ERROR);
                    return;
                }
                List<WareHouseOrderBean.WareHouseOrderListBean> list = response.body().getWareHouseOrderList();

                try {
                    totalPage = response.body().getTotalPageCount_o();
                    totalCount = response.body().getTotalCount_o();

                    if (pageIndex > 1) {
                        //第2、3、4.....页
                        if ((pageIndex >= Integer.parseInt(totalPage))) {
                            //要加载的页大于等于总页数
                            if (orderLists.size() + list.size() <= (Integer.parseInt(totalCount))) {
                                //已经是最后一页，正常添加
                                orderLists.addAll(orderLists.size(), list);
                                handler.sendEmptyMessage(MSG_LOAD_MORE_SUCCESS);
                            } else {
                                //当前列表的总数加上返回的列表数量大于总数，说明肯定超过了最大页，说明数据已经加载完
                                handler.sendEmptyMessage(MSG_NO_MORE_DATA);
                            }
                        }else {
                            orderLists.addAll(orderLists.size(), list);
                            handler.sendEmptyMessage(MSG_LOAD_MORE_SUCCESS);
                        }
                    } else {
                        //第一页，正常添加
                        orderLists.addAll(orderLists.size(), list);
                        handler.sendEmptyMessage(MSG_GET_DATA_SUCCESS);
                    }


                } catch (NumberFormatException e) {
                    if (pageIndex > 1) {
                        handler.sendEmptyMessage(MSG_LOAD_MORE_FAIL);
                    } else {
                        handler.sendEmptyMessage(MSG_GET_DATA_FAIL);
                    }
                    e.printStackTrace();
                    return;
                }



            }

            @Override
            public void onFailure(Call<WareHouseOrderBean> call, Throwable t) {
                if (pageIndex > 1) {
                    handler.sendEmptyMessage(MSG_LOAD_MORE_FAIL);
                } else {
                    handler.sendEmptyMessage(MSG_GET_DATA_FAIL);
                }
            }
        });
    }

    public void clearData() {
        if (orderLists != null)
            orderLists.clear();
    }

    public List<WareHouseOrderBean.WareHouseOrderListBean> getOrderLists() {
        return orderLists;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public String getTotalCount() {
        return totalCount;
    }
}
