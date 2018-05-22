package com.danertu.tools;

public class AccountUtil {
    private AESEncrypt aes = null;

    public AccountUtil() throws Exception {
        aes = new AESEncrypt();
    }

    /**
     * @param uid          uid
     * @param inoutTag     0表示充值，1表示提现
     * @param money
     * @param remark
     * @param rechargeWay
     * @param rechargeCode
     * @return
     * @throws Exception
     */
    public String getPostInfo(String uid, int inoutTag, double money,
                              String remark, String rechargeWay, String rechargeCode) throws Exception {
        String msg = uid + "|" + inoutTag + "|" + money + "|" + remark + "|" + rechargeWay + "|" + rechargeCode;
        return aes.encrypt(msg);
    }

    /**
     * 提现提交
     *
     * @param uid
     * @param money
     * @param remark
     * @return 加密后的提现信息
     * @throws Exception
     */
    public String getPostTakeMoneyInfo(String uid, double money, String remark) throws Exception {
        String msg = uid + "|" + money + "|" + remark + "|" + 0;
        return aes.encrypt(msg);
    }

    /**
     * @param uid
     * @param orderNum
     * @param orderMoney
     * @return 加密后的支付信息
     * @throws Exception
     */
    public String getPayInfo(String uid, String orderNum, String orderMoney) throws Exception {
        String msg = uid + "|" + orderNum + "|" + orderMoney;
        return aes.encrypt(msg);
    }

    /**
     * 2018年1月3日
     * 囤货订单账号支付、退货运费账号支付
     *
     * @param memLoginId
     * @param orderNum
     * @param payPrice
     * @param deviceType
     * @return
     * @throws Exception
     */
    public String getPayInfo(String memLoginId, String orderNum, String payPrice, String deviceType) throws Exception {
        String msg = memLoginId + "|" + orderNum + "|" + payPrice + "|" + deviceType;
        Logger.e(this.getClass().getSimpleName(), "{memLoginId:" + memLoginId + ",orderNum:" + orderNum + ",payPrice:" + payPrice + ",deviceType:" + deviceType + "}");
        return aes.encrypt(msg);
    }

    /**
     * @param uid
     * @param shopid
     * @param info       info[][0] 商品id，info[][1] 商品名称，info[][2] 商品单价，info[][3] 商品数量
     * @param totalPrice 点餐总价
     * @param tableNo    桌号（可空）
     * @param payType    支付方式 ：1（线下付）或2（线上支付）
     * @param mobile
     * @param address
     * @param remark     备注
     * @param pwdNo      口令
     * @return 点菜加密信息
     * @throws Exception
     */
    public String getChooseFoodInfo(String uid, String shopid, String[][] info,
                                    double totalPrice, String tableNo, int payType,
                                    String mobile, String address, String remark, String pwdNo) throws Exception {
        String result = "";
        for (String[] item : info) {
            result += item[0] + "," + item[1] + "," + item[2] + "," + item[3] + ";";
        }
        String productInfo = result;
        String msg = uid + "|" + shopid + "|" + productInfo + "|"
                + totalPrice + "|" + tableNo + "|" + payType + "|"
                + mobile + "|" + address + "|" + remark + "|" + pwdNo;
        result = aes.encrypt(msg);
        return result;
    }

    /**
     * @param uid
     * @param shopid
     * @param reserveNo   预订单编码, 生成规则，客户端按照时间戳，精确到毫秒，18位
     * @param reserveTime 大致到达时间
     * @param payType     支付方式 ：1（线下付）或2（线上支付）
     * @param personNum   人数
     * @param name        联系人
     * @param mobile      联系电话
     * @param price       预订价格
     * @return 预订加密信息
     * @throws Exception
     */
    public String getReserveInfo(String uid, String shopid, String reserveNo,
                                 String reserveTime, int payType, int personNum,
                                 String name, String mobile, String price) throws Exception {
        String msg = uid + "|" + shopid + "|" + reserveNo + "|"
                + reserveTime + "|" + payType + "|" + personNum + "|"
                + name + "|" + mobile + "|" + price;
        return aes.encrypt(msg);
    }

    /**
     * @param uid
     * @param shopid
     * @param tableNo     桌号
     * @param serviceType 呼叫服务类型 : 1.加水 ，2.纸巾，3.服务员 ，4.结账
     * @return 店内服务加密信息
     * @throws Exception
     */
    public String getShopServiceInfo(String uid, String shopid, String tableNo, int serviceType) throws Exception {
        String msg = uid + "|" + shopid + "|" + tableNo + "|" + serviceType;
        return aes.encrypt(msg);
    }
}
