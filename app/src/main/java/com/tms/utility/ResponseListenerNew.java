package com.tms.utility;
/**
 * interface to catch response of WebServiceAsync class
 * 
 *
 */
public interface ResponseListenerNew {

/**
 * method to catch response
 * @param strresponse
 * 						response get from any webservice
 */
public void response(String strresponse, String via);
	
}
