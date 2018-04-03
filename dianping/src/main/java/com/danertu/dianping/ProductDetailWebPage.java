package com.danertu.dianping;

import com.danertu.tools.Logger;
import com.danertu.tools.ProductDetailUtil;
import com.danertu.tools.XNUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

public class ProductDetailWebPage extends HtmlActivity {
    private ProductDetailUtil util = null;
    private boolean isbackcall = true;

    @JavascriptInterface
    public boolean isIsbackcall() {
        return isbackcall;
    }

    @JavascriptInterface
    public void setIsbackcall(boolean isbackcall) {
        this.isbackcall = isbackcall;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int canBuyCount = 2;
        Bundle bundle = getIntent().getExtras();
        try {
            String count = bundle.getString(ProductDetailsActivity2.KEY_CAN_BUY);
            if (TextUtils.isEmpty(count))
                canBuyCount = 2;
            else
                canBuyCount = Integer.parseInt(count);
        } catch (Exception e) {
            canBuyCount = 2;
        }
        util = new ProductDetailUtil(this, canBuyCount, db);
    }

    /**
     * 将商品加入购物车
     *
     * @param productID   productID
     * @param productName productName
     * @param aID         aID
     * @param buyPrice    buyPrice
     * @param proImage    proImage
     * @param buyCount    buyCount
     * @param supId       supId
     * @param shopID      shopID
     * @return true/false
     */
    @JavascriptInterface
    private boolean putInShopCar(final String productID, final String productName, final String aID, final String buyPrice, final String proImage, final String buyCount, final String supId, final String shopID) {
        return putInShopCar(productID, productName, aID, buyPrice, proImage, buyCount, supId, shopID, "", "");
    }

    /**
     * 加入商品到购物车
     *
     * @param productID   productID
     * @param productName productName
     * @param aID         aID
     * @param buyPrice    buyPrice
     * @param proImage    proImage
     * @param buyCount    buyCount
     * @param supId       supId
     * @param shopID      shopID
     */
    @JavascriptInterface
    private boolean putInShopCar(final String productID, final String productName, final String aID, final String buyPrice, final String proImage, final String buyCount, final String supId, final String shopID, String attrJson, String shopName) {
        return util.putInShopCar(productID, productName, aID, buyPrice, proImage, buyCount, getUid(), supId, shopID, attrJson, shopName, "");
    }

    /**
     * 跳转至支付中心
     *
     * @param guid       guid
     * @param sCount     sCount
     * @param imgName    imgName
     * @param supId      supId
     * @param shopid     shopid
     * @param agentId    agentId
     * @param proName    proName
     * @param sPrice     sPrice
     * @param createUser createUser
     * @since versionCode 77
     */
    @JavascriptInterface
    public void toPayCenter(final String guid, final String sCount, final String imgName, final String supId, final String shopid, final String agentId, final String proName, final String sPrice, final String marketPrice, final String createUser, final String discountNum, final String discountPrice) {
        Logger.e("test", "ProductDetailWebPage toPayCenter 11 discountNum=" + discountNum + " discountPrice=" + discountPrice);
        toPayCenter(guid, sCount, imgName, supId, shopid, agentId, proName, sPrice, marketPrice, createUser, "", discountNum, discountPrice);
    }

    @JavascriptInterface
    public void toPayCenter(final String guid, final String sCount, final String imgName, final String supId, final String shopid, final String agentId, final String proName, final String sPrice, final String marketPrice, final String createUser, final String attrJson, final String discountNum, final String discountPrice) {
        runOnUiThread(new Runnable() {
            public void run() {
                util.toPayCenter(guid, sCount, imgName, supId, shopid, agentId, proName, sPrice, marketPrice, createUser, isIsbackcall(), attrJson, "", "", "", discountNum, discountPrice);
            }
        });
    }

    @JavascriptInterface
    public void setXNUtil() {
        util.setXNUtil(new XNUtil(this));
    }

    @JavascriptInterface
    public void contactService(final String shopid, final String guid, final String proName, final String price, final String imgPath) {
        runOnUiThread(new Runnable() {
            public void run() {
                util.contactService(shopid, guid, proName, price, imgPath);
            }
        });
    }

    @JavascriptInterface
    public void setContactService(String proName, String orderPrice, String userName, String userId) {
        util.setContactService(proName, orderPrice, userName, userId);
    }

    @JavascriptInterface
    public void postCustomerTrack() {
        util.postCustomerTrack();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
