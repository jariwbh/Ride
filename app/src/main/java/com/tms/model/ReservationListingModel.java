package com.tms.model;

import java.io.Serializable;

public class ReservationListingModel implements Serializable {
	//	{"userReservations":[{"resvId":"1","picUpLoc":"DT Cinema Chandigarh","dropOffLoc":"ISBT 43 Chandigarh","picUpTime":"12:12 PM","picUpDate":"12\/25\/2013","fareQuote":"191","taxiType":"Black Car","reservationStatus":"0"}],"message":"Your reservations."}

	private String   title="",resvId="",picUpLoc="",dropOffLoc="",picUpTime="",picUpDate="",fareQuote="",taxiType="",reservationStatus="",taxiNumber="",taxiModel="",driverImage="",driverName="",driverPhone="",status=""
			,itemToMove="",
			weight="",
			dimension="",
			comment="" ;
	
	
	
	public String getItemToMove() {
		return itemToMove;
	}

	public void setItemToMove(String itemToMove) {
		this.itemToMove = itemToMove;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaxiNumber() {
		return taxiNumber;
	}

	public void setTaxiNumber(String taxiNumber) {
		this.taxiNumber = taxiNumber;
	}

	public String getTaxiModel() {
		return taxiModel;
	}

	public void setTaxiModel(String taxiModel) {
		this.taxiModel = taxiModel;
	}

	public String getDriverImage() {
		return driverImage;
	}

	public void setDriverImage(String driverImage) {
		this.driverImage = driverImage;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	private String riderName="",riderPhone="",flight="",airLine="";
	public String getRiderName() {
		return riderName;
	}

	public void setRiderName(String riderName) {
		this.riderName = riderName;
	}

	public String getRiderPhone() {
		return riderPhone;
	}

	public void setRiderPhone(String riderPhone) {
		this.riderPhone = riderPhone;
	}

	public String getFlight() {
		return flight;
	}

	public void setFlight(String flight) {
		this.flight = flight;
	}

	public String getAirLine() {
		return airLine;
	}

	public void setAirLine(String airLine) {
		this.airLine = airLine;
	}

	public String getResvId() {
		return resvId;
	}

	public void setResvId(String resvId) {
		this.resvId = resvId;
	}

	public String getPicUpLoc() {
		return picUpLoc;
	}

	public void setPicUpLoc(String picUpLoc) {
		this.picUpLoc = picUpLoc;
	}

	public String getDropOffLoc() {
		return dropOffLoc;
	}

	public void setDropOffLoc(String dropOffLoc) {
		this.dropOffLoc = dropOffLoc;
	}

	public String getPicUpTime() {
		return picUpTime;
	}

	public void setPicUpTime(String picUpTime) {
		this.picUpTime = picUpTime;
	}

	public String getPicUpDate() {
		return picUpDate;
	}

	public void setPicUpDate(String picUpDate) {
		this.picUpDate = picUpDate;
	}

	public String getFareQuote() {
		return fareQuote;
	}

	public void setFareQuote(String fareQuote) {
		this.fareQuote = fareQuote;
	}

	public String getTaxiType() {
		return taxiType;
	}

	public void setTaxiType(String taxiType) {
		this.taxiType = taxiType;
	}

	public String getReservationStatus() {
		return reservationStatus;
	}

	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}
	

}
