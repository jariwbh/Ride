package com.tms.model;

public class GetPromoCodeListing {
	//	{"getPromoCodeListing":[{"id":"1","promoCodeId":"4","promoCode":"o782i","amount":"12%"}],"message":"You have promo codes."}

	boolean chk=false;
	public boolean isChk() {
		return chk;
	}
	public void setChk(boolean chk) {
		this.chk = chk;
	}
	String id="";
	String promoCodeId="";
	String promoCode="";
	String  amount="";
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPromoCodeId() {
		return promoCodeId;
	}
	public void setPromoCodeId(String promoCodeId) {
		this.promoCodeId = promoCodeId;
	}
	public String getPromoCode() {
		return promoCode;
	}
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	

}
