package com.tms.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class ToroSharedPrefrnce {

	Context mContext;

	SharedPreferences mSharedPreferences;
	private String mTime;
	private int mDistanceInMeter,mTimeInSecond;


	public ToroSharedPrefrnce(Context mContext) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;

		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
	}


	public void clearPrefRence() {
		mSharedPreferences.edit().clear().commit();
	}
	
	public boolean getPaymentViaCreditCard(){
		
		return mSharedPreferences.getBoolean("creditcard_payment", false);
	}
	
	
    public void setPaymentViaCreditCard(Boolean isPaymentViaCreditCard){
    	mSharedPreferences.edit().putBoolean("creditcard_payment", isPaymentViaCreditCard).commit();
    	
    }
	
    
	public boolean isTutorialShown(){
		
		return mSharedPreferences.getBoolean("isTutorialShown", true);
	}
	
	
    public void setTutorialShown(Boolean isTutorialShown){
    	mSharedPreferences.edit().putBoolean("isTutorialShown", isTutorialShown).commit();
    	
    }
	
	/*public String getBookingId() {
		return mSharedPreferences.getString("BOOKING_ID", "0");
	}

	public void setBookingId(String bookingId) {

		mSharedPreferences.edit().putString("BOOKING_ID", bookingId).commit();

	}
	public String getPaymentStatus() {
		return mSharedPreferences.getString("PAYMENT_STATUS", "0");
	}

	public void setPaymentStatus(String paymentStatus) {

		mSharedPreferences.edit().putString("PAYMENT_STATUS", paymentStatus).commit();

	}
	*/
	
	public boolean isAccountVerified() {
		return mSharedPreferences.getBoolean("verified_account", false);
	}

	public void setAccountVerified(Boolean verified) {

		mSharedPreferences.edit().putBoolean("verified_account", verified).commit();

	}
	

	public String getZip() {
		return mSharedPreferences.getString("zip", "");
	}

	public void setZip(String zip) {

		mSharedPreferences.edit().putString("zip", zip).commit();

	}
	
	
	public String getCountryCode() {
		return mSharedPreferences.getString("COUNTRYCODE", "+1");
	}

	public void setCountryCode(String countrycode) {

		mSharedPreferences.edit().putString("COUNTRYCODE", countrycode).commit();

	}

	public String getReferal() {
		return mSharedPreferences.getString("REFERAL", "");
	}

	public void setReferal(String referal) {

		mSharedPreferences.edit().putString("REFERAL", referal).commit();

	}

	public String getCardType() {
		return mSharedPreferences.getString("cardType", "");
	}

	public void setCardType(String cardType) {

		mSharedPreferences.edit().putString("cardType", cardType).commit();

	}

	public String getImageUrl() {
		return mSharedPreferences.getString("imageUrl", "");
	}

	public void setImageUrl(String imgUrl) {

		mSharedPreferences.edit().putString("imageUrl", imgUrl).commit();

	}

	public String getEmail() {
		return mSharedPreferences.getString("email", "");
	}

	public void setEmail(String email) {

		mSharedPreferences.edit().putString("email", email).commit();

	}

	public String getPhone() {
		return mSharedPreferences.getString("phone", "");
	}

	public void setPhone(String phone) {

		mSharedPreferences.edit().putString("phone", phone).commit();
	}

	public String getPassword() {
		return mSharedPreferences.getString("password", "");
	}

	public void setPassword(String password) {

		mSharedPreferences.edit().putString("password", password).commit();
	}

	public String getFirstName() {
		return mSharedPreferences.getString("firstName", "");
	}

	public void setFirstName(String firstName) {

		mSharedPreferences.edit().putString("firstName", firstName).commit();
	}

	public String getLastname() {
		return mSharedPreferences.getString("lastname", "");
	}

	public void setLastname(String lastname) {

		mSharedPreferences.edit().putString("lastname", lastname).commit();
	}

	public String getCreditcardno() {
		return mSharedPreferences.getString("creditcardno", "");
	}

	public void setCreditcardno(String creditcardno) {

		mSharedPreferences.edit().putString("creditcardno", creditcardno)
				.commit();
	}

	public String getExpirydateMonth() {
		return mSharedPreferences.getString("expirydatemonth", "");
	}

	public void setExpirydateMonth(String expirydatemonth) {

		mSharedPreferences.edit().putString("expirydatemonth", expirydatemonth)
				.commit();
	}

	public String getExpirydateYear() {
		return mSharedPreferences.getString("expirydateyear", "");
	}

	public void setExpirydateYear(String expirydateyear) {

		mSharedPreferences.edit().putString("expirydateyear", expirydateyear)
				.commit();
	}

	public String getCvv() {
		return mSharedPreferences.getString("cvv", "");
	}

	public void setCvv(String cvv) {

		mSharedPreferences.edit().putString("cvv", cvv).commit();
	}

	public String getPhoto() {
		return mSharedPreferences.getString("photo", "");
	}

	public void setPhoto(String photo) {

		mSharedPreferences.edit().putString("photo", photo).commit();
	}

	public String getUserId() {
		return mSharedPreferences.getString("userId", "");
	}

	public void setUserId(String userId) {

		mSharedPreferences.edit().putString("userId", userId).commit();
	}

	public String getGcm() {
		return mSharedPreferences.getString("GCM", "");
	}

	public void setGcm(String GCM) {

		mSharedPreferences.edit().putString("GCM", GCM).commit();
	}

	public String getimageName() {
		return mSharedPreferences.getString("imgeName", "");
	}

	public void setImageName(String imgeName) {

		mSharedPreferences.edit().putString("imgeName", imgeName).commit();
	}

	public String getDistance() {
		return mSharedPreferences.getString("distance", "");
	}

	public void setDistance(String distance) {

		mSharedPreferences.edit().putString("distance", distance).commit();
	}

	public String getCounrty() {
		return mSharedPreferences.getString("Counrty", "");
	}

	public void setCounrty(String Counrty) {

		mSharedPreferences.edit().putString("Counrty", Counrty).commit();
	}
	public String geSourceAddress() {
		return mSharedPreferences.getString("sourceAddress", "");
	}

	public void setSourceAddress(String sourceAddress) {
		mSharedPreferences.edit().putString("sourceAddress", sourceAddress).commit();
	}

	public String geDestAddress() {
		return mSharedPreferences.getString("destAddress", "");
	}

	public void setDestAddress(String sourceAddress) {
		mSharedPreferences.edit().putString("destAddress", sourceAddress).commit();
	}

    public void setTime(String time) {
		mSharedPreferences.edit().putString("time", time).commit();
    }
	public String getTime() {
		return mSharedPreferences.getString("time", "");
	}

	public void setDistanceInMeter(int distanceInMeter) {
		mSharedPreferences.edit().putInt("distanceInMeter", distanceInMeter).commit();
	}

	public void setTimeInSeconds(int timeInSeconds) {
		mSharedPreferences.edit().putInt("timeInSeconds", timeInSeconds).commit();
	}

	public int getDistanceInMeter() {
		return mSharedPreferences.getInt("distanceInMeter",0);
	}
	public int getTimeInSeconds() {
		return mSharedPreferences.getInt("timeInSeconds",0);
	}

	public void setRecents(Set<String> someStringSet) {
		mSharedPreferences.edit().putStringSet("reccentsid", someStringSet).commit();
	}

	public Set<String> getRecents() {
		Set<String> someStringSet = mSharedPreferences.getStringSet("reccentsid", null);
		return someStringSet;
	}
}
