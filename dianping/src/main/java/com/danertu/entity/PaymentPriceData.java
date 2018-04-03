package com.danertu.entity;

import com.danertu.tools.Logger;
import com.danertu.tools.MathUtils;
import com.danertu.widget.CommonTools;
public class PaymentPriceData{
	/**没优惠前的商品总价含运费*/
	private double priceSum;
	private double favourablePrice;
	private double favTicket;
	private double favNumMoney;
	/** 积分抵扣的实际金额 */
	public double canUseJlbMoney;
	private boolean isUseJLB;
	private int useScore = 0;
	private double favCashTicket;
	private boolean canWithOthersFav;

	public double getFavNumMoney() {
		return favNumMoney;
	}

	public void setFavNumMoney(double favNumMoney) {
		this.favNumMoney = favNumMoney;
	}

	public void setCanWithOthersFav(boolean is){
		this.canWithOthersFav = is;
	}
	
	public void setUseJLB(boolean isUse){
		this.isUseJLB = isUse;
		useScore = isUseJLB ? MathUtils.multiply(canUseJlbMoney, 100).intValue() : 0;
	}
	
	public double getFavourablePrice() {
		return favourablePrice;
	}

	public void setFavourablePrice(double favourablePrice) {
		this.favourablePrice = favourablePrice;
	}

	public double getFavTicket() {
		return favTicket;
	}

	public void setFavTicket(double favTicket) {
		this.favTicket = favTicket;
	}

	public double getFavJlbMoney() {
		return canUseJlbMoney;
	}

	public void setCanUseJlbMoney(double favJlbMoney) {
		this.canUseJlbMoney = favJlbMoney;
	}

	public int getUseScore() {
		return useScore;
	}

	public double getFavCashTicket() {
		return favCashTicket;
	}

	public void setFavCashTicket(int favCashTicket) {
		this.favCashTicket = favCashTicket;
		if (getHandleSum() <= favCashTicket) {
			this.favCashTicket = favCashTicket - 0.01;
		}
	}

	public void setPriceSum(double priceSum){
		this.priceSum = priceSum;
	}
	
	public final double getPriceSum(){
		return priceSum;
	}
	
	public double getHandleSum(){
		double favMoney = 0;
		if(isUseJLB)
			favMoney += canUseJlbMoney;
		Logger.e("getHandleSum()", favCashTicket +", "+ favMoney +", "+ favourablePrice +", "+ favTicket);
		if(canWithOthersFav)
			return priceSum -favTicket;
		return priceSum-favCashTicket-favMoney-favourablePrice-favTicket-favNumMoney;
	}
	
	public String getTotalPrice(){
		return CommonTools.formatZero2Str(getHandleSum());
	}
}