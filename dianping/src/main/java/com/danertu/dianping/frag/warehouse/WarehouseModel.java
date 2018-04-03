package com.danertu.dianping.frag.warehouse;

import android.os.Handler;

import com.danertu.base.BaseModel;
import com.danertu.entity.WarehouseBean;
import com.danertu.tools.ApiService;
import com.danertu.tools.Logger;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danertu.dianping.frag.warehouse.WarehousePresenter.MSG_DATA_FAIL;
import static com.danertu.dianping.frag.warehouse.WarehousePresenter.MSG_DATA_SUCCESS;
import static com.danertu.dianping.frag.warehouse.WarehousePresenter.MSG_LOAD_MORE_FAIL;
import static com.danertu.dianping.frag.warehouse.WarehousePresenter.MSG_LOAD_MORE_NO_DATA;
import static com.danertu.dianping.frag.warehouse.WarehousePresenter.MSG_LOAD_MORE_SUCCESS;

/**
 * Created by Viz on 2017/12/21.
 */

public class WarehouseModel extends BaseModel {
    private List<WarehouseBean.ProductCategoryBean> categoryList;
    private List<WarehouseBean.WareHouseListBean> productList;
    private int totalPage = 0;
    private int totalCount = 0;

    public WarehouseModel() {
        super();
        categoryList = new ArrayList<>();
        productList = new ArrayList<>();
    }

    /**
     * @param handler
     * @param memLoginId
     * @param pageIndex
     * @param pageSize
     * @param productCategoryId
     * @param orderBy           默认时间 库存降序=1
     */
    public void getStockList(final Handler handler, String memLoginId, final int pageIndex, int pageSize, String productCategoryId, String orderBy) {
        Call<WarehouseBean> call = retrofit.create(ApiService.class).getStockList("0325", memLoginId, pageIndex, pageSize, productCategoryId, orderBy);
        call.enqueue(new Callback<WarehouseBean>() {
            @Override
            public void onResponse(Call<WarehouseBean> call, Response<WarehouseBean> response) {
                println(response.toString());
                if (response.code() != RESULT_OK) {
                    handler.sendEmptyMessage(MSG_SERVER_ERROR);
                    return;
                }
                WarehouseBean body = response.body();
                println(body);
                try {
                    totalPage = Integer.parseInt(body.getTotalPageCount_o());
                    totalCount = Integer.parseInt(body.getTotalCount_o());
                } catch (NumberFormatException e) {
                    if (pageIndex > 1) {
                        handler.sendEmptyMessage(MSG_LOAD_MORE_FAIL);
                    } else {
                        handler.sendEmptyMessage(MSG_DATA_FAIL);
                    }
                    e.printStackTrace();
                    return;
                }
                List<WarehouseBean.WareHouseListBean> wareHouseList = body.getWareHouseList();
                List<WarehouseBean.ProductCategoryBean> list = body.getProductCategory();
                if (categoryList.size() <= 0) {
                    categoryList.addAll(list);
                }
//                printlnE("总页数：" + totalPage);
//                printlnE("总计数：" + totalCount);
                printlnE(wareHouseList);
                if (pageIndex > 1) {
                    //第2、3、4.....页
                    if ((pageIndex >= totalPage)) {
                        //要加载的页大于等于总页数
                        if (productList.size() + wareHouseList.size() <= totalCount) {
                            //已经是最后一页，正常添加
                            handler.sendEmptyMessage(MSG_LOAD_MORE_SUCCESS);
                            productList.addAll(productList.size(), wareHouseList);
                        } else {
                            //当前列表的总数加上返回的列表数量大于总数，说明肯定超过了最大页，说明数据已经加载完
                            handler.sendEmptyMessage(MSG_LOAD_MORE_NO_DATA);
                        }
                    } else {
                        handler.sendEmptyMessage(MSG_LOAD_MORE_SUCCESS);
                        productList.addAll(productList.size(), wareHouseList);
                    }
                } else {
                    //第一页，正常添加
                    handler.sendEmptyMessage(MSG_DATA_SUCCESS);
                    productList.addAll(productList.size(), wareHouseList);
                }
            }

            @Override
            public void onFailure(Call<WarehouseBean> call, Throwable t) {
                if (pageIndex > 1) {
                    handler.sendEmptyMessage(MSG_LOAD_MORE_FAIL);
                } else {
                    handler.sendEmptyMessage(MSG_DATA_FAIL);
                }
                Logger.e(TAG, call.toString() + "/" + t);
            }
        });
    }

    public void clearData() {
        if (productList != null)
            productList.clear();
    }

    public List<WarehouseBean.ProductCategoryBean> getCategoryList() {
        return categoryList;
    }

    public List<WarehouseBean.WareHouseListBean> getProductList() {
        return productList;
    }

    public int getTotalPage() {
        return totalPage;
    }
}
