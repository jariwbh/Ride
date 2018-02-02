package com.tms.utility;
/**
 * interface to catch response of WebServiceAsync class
 * 
 *
 */
public interface ResponseListener {

/**
 * method to catch response
 * @param strresponse
 * 						response get from any webservice
 */
public void response(String strresponse, String webservice);
	
}
