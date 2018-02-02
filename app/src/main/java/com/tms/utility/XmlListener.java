package com.tms.utility;

/**
 * Interface to catch response of SendXmlAsync class
 * @author rishab.bhardwaj
 *
 */
public interface XmlListener {
	/**
	 * method to get response
	 * @param respose
	 * 					response from web service
	 */
	public void onResponse(String respose);

}
