package com.tms;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.net.URLEncoder;

public class Geocoder {
	public static String reverseGeocode(String Address) {
		// http://maps.google.com/maps/geo?q=40.714224,-73.961452&output=json&oe=utf8&sensor=true_or_false&key=your_api_key
		String localityName = "";
		
		String serverAddress = "";

		try {
			String url=URLEncoder.encode(Address,"UTF-8");
			// build the URL using the latitude & longitude you want to lookup
			// NOTE: I chose XML return format here but you can choose something
			// else
			serverAddress="http://maps.googleapis.com/maps/api/geocode/json?address=";
String sensor="&sensor=true";
			// set up out communications stuff
			

			// Set up the initial connection
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response;
			HttpGet httpget = new HttpGet(serverAddress+url+sensor.trim());
			try {
				response = httpClient.execute(httpget);

				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();

					if (entity != null) {
						InputStream instream = entity.getContent();
						localityName = JsonUtil.convertStreamToString(instream);

						instream.close();

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//			try {
//				InputStreamReader isr = new InputStreamReader(
//						connection.getInputStream());
//				InputSource source = new InputSource(isr);
//				SAXParserFactory factory = SAXParserFactory.newInstance();
//				SAXParser parser = factory.newSAXParser();
//		XMLReader xr = parser.getXMLReader();
//				GoogleReverseGeocodeXmlHandler handler = new GoogleReverseGeocodeXmlHandler();
//
//				xr.setContentHandler(handler);
//				xr.parse(source);
//
//				localityName = handler.getLocalityName();
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}

		} catch (Exception ex) {
			ex.printStackTrace();
			localityName=ex.toString();
		}

		return localityName.toString();
	}

}
