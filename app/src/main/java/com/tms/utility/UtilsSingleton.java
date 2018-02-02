package com.tms.utility;

import java.util.ArrayList;

import com.tms.model.LocationsName;

public class UtilsSingleton {

    private UtilsSingleton() {

		// TODO Auto-generated constructor stub
	}

	public static UtilsSingleton instance;

	public static UtilsSingleton getInstance() {
		if (instance == null) {
			instance = new UtilsSingleton();
		}
		return instance;

	}
	
	public static  int counterBack;

	
	public double backGroundLat,backGroundLong;
	
	
	public String  bookingId="0";
	public String paymentStatus="0";
	
	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	/*
	 * current lat
	 */
	public double current_lat;
	public double getBackGroundLat() {
		return backGroundLat;
	}

	public void setBackGroundLat(double backGroundLat) {
		this.backGroundLat = backGroundLat;
	}

	public double getBackGroundLong() {
		return backGroundLong;
	}

	public void setBackGroundLong(double backGroundLong) {
		this.backGroundLong = backGroundLong;
	}

	/*
	 * Current Longitude
	 */
	public double current_longi;

	public double getCurrent_lat() {
		return current_lat;
	}

	public void setCurrent_lat(double current_lat) {
		this.current_lat = current_lat;
	}

	public double getCurrent_longi() {
		return current_longi;
	}

	public void setCurrent_longi(double current_longi) {
		this.current_longi = current_longi;
	}

	public  ArrayList<LocationsName> mRecentSearch = new ArrayList<LocationsName>();
}
