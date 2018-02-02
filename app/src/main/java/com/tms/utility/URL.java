package com.tms.utility;

public class URL {

//	public static String TMS_LINK = "http://demo.taximobilesolutions.com/";
	public static String TMS_LINK = "http://taxidispatch.azurewebsites.net/";
	// public static String SITE_LINK =
	// "http://americantopflight.demo.netsmartz.us/";
//	public static String SITE_LINK = "http://demo.taximobilesolutions.com/";
	public static String SITE_LINK = "http://taxidispatch.azurewebsites.net/";
//	public static String GOOGLEAPIKEY ="AIzaSyDLJ-4UWr75-1LKQ5Kt8UAvkhFXprG8cqA";



	//public static String GOOGLEAPIKEY ="AIzaSyBWMLid5R2Ypjk93meFMT13M6KjNdUlZm0";//Commented on 26Sept, 2017
	public static String GOOGLEAPIKEY ="AIzaSyBcIHhaTpj01Yh2QQpfdumJ9Yp6xYo3cxY";
	public static String SUPPORT_MAIL = "info@taximobilesolutions.com";

	// public static final String BASE_URL =
	// "http://americantopflight.demo.netsmartz.us/";

	//public static String BASE_URL = "http://alphacar.taximobilesolutions.com/";
//	 public static String BASE_URL = "http://demo.taximobilesolutions.com/";  // demo
	 public static String BASE_URL = "http://taxidispatch.azurewebsites.net/";  // demo

	//public static String BASE_URL = "http://skylimo.taximobilesolutions.com/";  // live

	public static final String REFFERAL_BONUS = "clientservices/getReferralBonus";
	/* changed */
	public static final String ABOUT_US = "clientservices/aboutUS";
	/* changed */
	public static final String TERMS_CONDITION = "clientservices/termCondition";

	public static final String CANCEL_RESERVATION = "reservationservices/cancelReservation";
	/* changed */
	public static final String LOG_OUT = "clientservices/userLogout?riderId=";

	public static final String GETLATLONG = "webservice/getMyLatLong";
	/* changed */
	public static final String GETCOUNTRYCODE = "clientservices/getCountryCode";
	/* changed */
	//public static final String CHANGE_CARD = "clientservices/changeCreditCardV1";
    public static final String CHANGE_CARD = "clientservices/changeCreditCardV1";

	/* changed */
	public static final String REGISTRATION = "clientservices/userRegistrationV1";

	/* changed */
	public static final String LOGIN = "clientservices/userLoginV1";
	/* changed */
	public static final String UPDATE_INFO = "clientservices/updateBasicInfo";

	/* changed */
	public static final String VALIDATE_EMAIL = "clientservices/validateEmail";

	/* changed */
	public static final String FORGOTPASSWORD = "clientservices/forgotPassword";
	/* changed */
	public static final String VARIFY_MOBILE = "clientservices/verifyPhone";

	/* changed */
	public static final String RESEND_CODE = "clientservices/resendCode";
	/* changed */
	public static final String CHANGE_PHONE = "clientservices/changePhoneNumber";
	/* changed */
	public static final String CREDITCARDLIST = "clientservices/creditCardListing?userId=";
	/* changed */
	public static final String EDITCREDITCARD = "clientservices/editCreditCard";

	/* changed */
	//public static final String DELETECREDITCARD = "clientservices/deleteCardV1?cardId=";
    public static final String DELETECREDITCARD = "clientservices/deleteCard?cardId=";

	/* changed */
	//public static final String ADDCREDITCARD = "clientservices/addCreditCardV1";
    public static final String ADDCREDITCARD = "clientservices/addCreditCard";
	/* changed */
	public static final String EDIT_PROFILE = "clientservices/editProfile";
	/* changed */
	public static final String CHANGE_PASSWORD = "clientservices/changePassword";
	/* changed */
	public static final String MY_PROFILE_DETAIL = "clientservices/getUserDetail?userId=";

	/* changed */
	public static final String PROMO_CODE = "clientservices/promoCode";
	public static final String ADDRESS_LAT_LNG = "https://api.foursquare.com/v2/venues/search?ll=";
	public static final String QUERY = "&query=";

	// https://api.foursquare.com/v2/venues/search?ll=30.7500,76.7800&intent=checkin&query=pvr&oauth_token=44W1ZFMPPM0B15G5VTM1LLR0HSWYS2DZUAZ330JO2H44DWWB&v=20131003

	public static final String OAUTH_TOKEN = "&oauth_token=44W1ZFMPPM0B15G5VTM1LLR0HSWYS2DZUAZ330JO2H44DWWB&v=20130923";
	/* changed */
	public static final String GET_DRIVERS = "clientservices/getDrivers";
	public static final String INTENT_LOCATION = "&intent=checkin";
	public static final String UPDATE_LOCATION = "webservice/updateLocation";
	// https://api.foursquare.com/v2/venues/search?ll=31.3256,75.5792&intent=checkin&query=pvr&oauth_token=44W1ZFMPPM0B15G5VTM1LLR0HSWYS2DZUAZ330JO2H44DWWB&v=20131003
	// public static final String GET_DRIVER =
	// "webservice/getDriversByIdVal?userId=";

	/* changed */
	//public static final String GET_DRIVER = "clientservices/getDriversByIdV1?userId=";
    public static final String GET_DRIVER = "clientservices/getDriversById?userId=";

	public static final String LAT = "&latitude=";
	public static final String LNG = "&longitude=";
	public static final String TAXI_TYPE = "&taxiType=";
	/* changed */
	public static final String TAXI_DETAIL = "clientservices/getTaxiDetail?userId=";
	/* changed */
	public static final String FARE_QUOTE = "clientservices/fareQuote";
	/* changed */
	public static final String BOOK_NOW = "clientservices/bookCabV1";

	public static final String LOADING_REQUEST = "clientservices/arrivingDriverAndroid";

	/* changed */
	//public static final String CANCEL_REQUEST = "clientservices/cancelBookingReqV3";
    public static final String CANCEL_REQUEST = "clientservices/cancelBookingReq";
	/* changed */
	public static final String RATING_DRIVER = "clientservices/driverRating";
	/* changed */
	public static final String PROMO_CODE_NEW = "clientservices/getPromoCode?userId=";

	/* changed */
	public static final String RESERVATION = "reservationservices/reservation";

	public static final String CONFIRM_RESERVATION = "reservationservices/confirmReservation";

	/* changed */
	//public static final String DRIVER_TIP = "clientservices/updateTripPaymentV1";
    public static final String DRIVER_TIP = "clientservices/updateTripPayment";

	/* changed */
	public static final String GETPROMOCODELISTING = "clientservices/getPromoCodeListing?userId=";

	/* changed */
	public static final String SELECTPROMOCODE = "clientservices/selectPromoCode?userId=";

	public static final String RESERVATIONLISTNG = "reservationservices/reservationList";
	public static final String RESERVATIONDETAIL = "reservationservices/reservationDetail";

	public static final String ACTIVATIONACCOUNT = "webservice/checkUserStatus?riderId=";

	public static String COMPANY_LIST = BASE_URL+"company/getTaxiCompanyList";

	/*
	 * 
	 *
	 * 
	 * 
	 * public static String GOOGLEAPIKEY
	 * ="AIzaSyA74W0fUT2hA9wMOp4kn1GES-OagOTJTYs";
	 * 
	 *
	 * 
	 *
	 * 
	 * public static final String REFFERAL_BONUS =
	 * "clientservices/getReferralBonus"; changed public static final String
	 * ABOUT_US = "clientservices/aboutUS"; changed public static final String
	 * TERMS_CONDITION = "clientservices/termCondition";
	 * 
	 * public static final String CANCEL_RESERVATION =
	 * "reservationservices/cancelReservation"; changed public static final
	 * String LOG_OUT = "clientservices/userLogout?riderId=";
	 * 
	 * public static final String GETLATLONG = "webservice/getMyLatLong";
	 * changed public static final String GETCOUNTRYCODE =
	 * "clientservices/getCountryCode"; changed public static final String
	 * CHANGE_CARD = "clientservices/changeCreditCard";
	 * 
	 * changed public static final String REGISTRATION =
	 * "clientservices/userRegistration";
	 * 
	 * changed public static final String LOGIN = "clientservices/userLogin";
	 * changed public static final String UPDATE_INFO =
	 * "clientservices/updateBasicInfo";
	 * 
	 * changed public static final String VALIDATE_EMAIL =
	 * "clientservices/validateEmail";
	 * 
	 * changed public static final String FORGOTPASSWORD =
	 * "clientservices/forgotPassword"; changed public static final String
	 * VARIFY_MOBILE = "clientservices/verifyPhone";
	 * 
	 * changed public static final String RESEND_CODE =
	 * "clientservices/resendCode"; changed public static final String
	 * CHANGE_PHONE = "clientservices/changePhoneNumber"; changed public static
	 * final String CREDITCARDLIST = "clientservices/creditCardListing?userId=";
	 * changed public static final String EDITCREDITCARD =
	 * "clientservices/editCreditCard";
	 * 
	 * changed public static final String DELETECREDITCARD =
	 * "clientservices/deleteCard?cardId=";
	 * 
	 * changed public static final String ADDCREDITCARD =
	 * "clientservices/addCreditCard"; changed public static final String
	 * EDIT_PROFILE = "clientservices/editProfile"; changed public static final
	 * String CHANGE_PASSWORD = "clientservices/changePassword"; changed public
	 * static final String MY_PROFILE_DETAIL =
	 * "clientservices/getUserDetail?userId=";
	 * 
	 * changed public static final String PROMO_CODE =
	 * "clientservices/promoCode"; public static final String ADDRESS_LAT_LNG =
	 * "https://api.foursquare.com/v2/venues/search?ll="; public static final
	 * String QUERY = "&query=";
	 * 
	 * 
	 * 
	 * public static final String OAUTH_TOKEN =
	 * "&oauth_token=44W1ZFMPPM0B15G5VTM1LLR0HSWYS2DZUAZ330JO2H44DWWB&v=20130923"
	 * ; changed public static final String GET_DRIVERS =
	 * "clientservices/getDrivers"; public static final String INTENT_LOCATION =
	 * "&intent=checkin"; public static final String UPDATE_LOCATION =
	 * "webservice/updateLocation";
	 * 
	 * 
	 * changed public static final String GET_DRIVER =
	 * "clientservices/getDriversById?userId=";
	 * 
	 * public static final String LAT = "&latitude="; public static final String
	 * LNG = "&longitude="; public static final String TAXI_TYPE = "&taxiType=";
	 * changed public static final String TAXI_DETAIL =
	 * "clientservices/getTaxiDetail?userId="; changed public static final
	 * String FARE_QUOTE = "clientservices/fareQuote"; changed public static
	 * final String BOOK_NOW = "clientservices/bookCab";
	 * 
	 * public static final String LOADING_REQUEST =
	 * "clientservices/arrivingDriverAndroid";
	 * 
	 * changed public static final String CANCEL_REQUEST =
	 * "clientservices/cancelBookingReq"; changed public static final String
	 * RATING_DRIVER = "clientservices/driverRating"; changed public static
	 * final String PROMO_CODE_NEW = "clientservices/getPromoCode?userId=";
	 * 
	 * changed public static final String RESERVATION =
	 * "reservationservices/reservation";
	 * 
	 * public static final String CONFIRM_RESERVATION =
	 * "reservationservices/confirmReservation";
	 * 
	 * changed public static final String DRIVER_TIP =
	 * "clientservices/updateTripPayment";
	 * 
	 * changed public static final String GETPROMOCODELISTING =
	 * "clientservices/getPromoCodeListing?userId=";
	 * 
	 * changed public static final String SELECTPROMOCODE =
	 * "clientservices/selectPromoCode?userId=";
	 * 
	 * public static final String RESERVATIONLISTNG =
	 * "reservationservices/reservationList"; public static final String
	 * RESERVATIONDETAIL = "reservationservices/reservationDetail";
	 * 
	 * public static final String ACTIVATIONACCOUNT =
	 * "webservice/checkUserStatus?riderId=";
	 */
}
