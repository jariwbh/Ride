package com.tms.model;

public class Credicardslisting {
	
	String cardId="",cardNo="",cardExpiryDateYear="",cardType="",zipCode="",cardExpiryDateMonth="";
	
	String prim="";

	public String getPrim() {
		return prim;
	}

	public void setPrim(String prim) {
		this.prim = prim;
	}

	boolean isChecked=false;
	
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getCardExpiryDateYear() {
		return cardExpiryDateYear;
	}

	public void setCardExpiryDateYear(String cardExpiryDateYear) {
		this.cardExpiryDateYear = cardExpiryDateYear;
	}

	public String getCardExpiryDateMonth() {
		return cardExpiryDateMonth;
	}

	public void setCardExpiryDateMonth(String cardExpiryDateMonth) {
		this.cardExpiryDateMonth = cardExpiryDateMonth;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	
	

}
