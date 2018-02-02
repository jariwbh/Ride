package com.tms.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationClass implements com.google.android.gms.location.LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	GoogleApiClient googleApiClient;

	public interface OnLocationFinder {
		public void OnGettingLatitudeLongitude(double latitude,
                                               double longitude, Location location, int uniqueCode);

		public void noLocationFound();

		public void onGettingAddress(String address, int uniqueCode);

		public void OnConnected();
	}

	/**
	 * LocationManager instance
	 */
	// A request to connect to Location Services
	private LocationRequest mLocationRequest;
	// Stores the current instantiation of the location client in this object
//	private LocationClient mLocationClient;
	/**
	 * Log TAG
	 */
	private final String TAG = "CurrentLocation";
	/**
	 * Interface instance
	 */
	private OnLocationFinder finder;
	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	private boolean mUpdatesRequested = false;
	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private int uniqueCode;

	/*
	 * Constants for location update parameters
	 */
	// Milliseconds per second
	public static final int MILLISECONDS_PER_SECOND = 1000;

	// The update interval
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

	// A fast interval ceiling
	public static final int FAST_CEILING_IN_SECONDS = 1;

	// Update interval in milliseconds
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;

	// A fast ceiling of update intervals, used when the app is visible
	public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
			* FAST_CEILING_IN_SECONDS;
	public static int PRIORITY_BALANCED_POWER_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
	public static int PRIORITY_HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
	public static int PRIORITY_LOW_POWER = LocationRequest.PRIORITY_LOW_POWER;
	public static int PRIORITY_NO_POWER = LocationRequest.PRIORITY_NO_POWER;
	// No Geoder Avalable
	private final String NO_GEOCODER_AVAILABLE = "Cannot get address. No geocoder available.";

	// Create an empty string for initializing strings
	private final String EMPTY_STRING = new String();

	public LocationClass(Context context) {
		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest.setInterval(MILLISECONDS_PER_SECOND);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
				.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
//		mLocationClient = new LocationClient(context, this, this);
		googleApiClient = new GoogleApiClient.Builder(context)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}

	public void onStart(OnLocationFinder listener) {
		/*
		 * Connect the client. Don't re-start any requests here; instead, wait
		 * for onResume()
		 */
		finder = listener;
		googleApiClient.connect();

	}

	public long getExpireDuration() {
		return mLocationRequest.getExpirationTime();
	}

	public void setExpireDuration(long milli) {
		mLocationRequest.setExpirationDuration(milli);
	}

	public long getExpireTime() {
		return mLocationRequest.getExpirationTime();
	}

	public void setExpireTime(long milli) {
		mLocationRequest.setExpirationTime(milli);
	}

	public long getFastestInterval() {
		return mLocationRequest.getFastestInterval();
	}

	public void setFastestInterval(long millis) {
		mLocationRequest.setFastestInterval(millis);
	}

	public long getInterval() {
		return mLocationRequest.getInterval();
	}

	public void setInterval(long millis) {
		mLocationRequest.setInterval(millis);
	}

	public int getPriority() {
		return mLocationRequest.getPriority();
	}

	public void setPriority(int priority) {
		mLocationRequest.setPriority(priority);
	}

	public int getNumUpdates() {
		return mLocationRequest.getNumUpdates();
	}

	public void setNumUpdates(int numUpdates) {
		mLocationRequest.setNumUpdates(numUpdates);
	}

	public float getSmallestDisplacement() {
		return mLocationRequest.getSmallestDisplacement();
	}

	public void setSmallestDisplacement(float smallestDisplacementMeters) {
		mLocationRequest.setSmallestDisplacement(smallestDisplacementMeters);
	}

	public void onStop() {
		// If the client is connected
		if (googleApiClient.isConnected()) {
			removeLocationUpdates(finder);
		}

		// After disconnect() is called, the client is considered "dead".
		googleApiClient.disconnect();
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 *
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected(Context context) {

		// Check that Google Play services is available
		int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context,resultCode
					, 0);
			if (dialog != null) {
				dialog.show();
			}
			return false;
		}
	}

	/***
	 * This method is used to getLatitude and longitude from current location
	 *
	 * @param context
	 *            context is passed

	 */
	public final void getLocations(Context context, OnLocationFinder callback,
			int uniqueCode) {
		// If Google Play Services is available
		finder = callback;
		if (servicesConnected(context)) {
			if (callback == null)
				return;
			// Get the current location
			if (!googleApiClient.isConnected()) {
				googleApiClient.connect();
			}
			Location currentLocation = null;
			if (android.os.Build.VERSION.SDK_INT >= 23) {
				if ((ContextCompat.checkSelfPermission(context,
						"android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED)
						&& (ContextCompat.checkSelfPermission(context,
						"android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED)) {

					currentLocation = LocationServices.FusedLocationApi
							.getLastLocation(googleApiClient);
				}
			}else {
				currentLocation = LocationServices.FusedLocationApi
						.getLastLocation(googleApiClient);
			}
			if (currentLocation != null) {
				callback.OnGettingLatitudeLongitude(
						currentLocation.getLatitude(),
						currentLocation.getLongitude(), currentLocation,
						uniqueCode);
			} else {
				callback.noLocationFound();
			}
		}
	}

	/**
	 * This method is used to update continuously again and again
	 *
	 * @param context
	 *            context
	 * @param callback
	 *
	 */
	public void startUpdates(Context context, OnLocationFinder callback,
			int uniqueCode) {
		mUpdatesRequested = true;
		if (callback == null)
			return;
		if (servicesConnected(context)) {
			requestLocationUpdates(callback, uniqueCode);
		}
	}

	public void stopUpdates(Context context, OnLocationFinder callback) {
		mUpdatesRequested = false;
		if (callback == null)
			return;
		if (servicesConnected(context)) {
			removeLocationUpdates(callback);
		}
	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	public void requestLocationUpdates(OnLocationFinder callback, int uniqueCode) {
		finder = callback;
		this.uniqueCode = uniqueCode;
//		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				googleApiClient, mLocationRequest, this);

	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	public void removeLocationUpdates(OnLocationFinder callback) {
		finder = callback;
//		mLocationClient.removeLocationUpdates(this);
		LocationServices.FusedLocationApi.removeLocationUpdates(
				googleApiClient, this);

	}

	/***
	 * This method is used to get address from latitude longitude
	 * 
	 * @param latitude
	 *            latitude is to enter
	 * @param longitude
	 *            longitude is to enter
	 * @param context
	 *            context is passed

	 */
	public final void getAddressFromLocation(double latitude, double longitude,
			Context context, int uniqueId, OnLocationFinder callback) {
		// In Gingerbread and later, use Geocoder.isPresent() to see if a
		// geocoder is available.
		finder = callback;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& !Geocoder.isPresent()) {
			// No geocoder is present. Issue an error message
			Toast.makeText(context, NO_GEOCODER_AVAILABLE, Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (servicesConnected(context)) {

			// Get the current location
			Location currentLocation = new Location("A");
			currentLocation.setLatitude(latitude);
			currentLocation.setLongitude(longitude);
			// Start the background task
			(new GetAddressTask(context, finder, uniqueId))
					.execute(currentLocation);
		}
	}

	/***
	 * This method is used to get latitude and longitude from address
	 * 
	 * @param context
	 *            context is passed
	 * @param add
	 *            address is passed

	 */
	public final void getLocationFromAddress(Context context, String add,
			int uniqueId, OnLocationFinder callback) {
		// In Gingerbread and later, use Geocoder.isPresent() to see if a
		// geocoder is available.
		finder = callback;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& !Geocoder.isPresent()) {
			// No geocoder is present. Issue an error message
			Toast.makeText(context, NO_GEOCODER_AVAILABLE, Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (servicesConnected(context)) {

			// Start the background task
			(new GetLocationTask(context, finder, uniqueId)).execute(add);
		}

	}

	/**
	 * This method is used to give distance between 2latlong
	 * 
	 * @param lat1
	 *            set latitude of position1
	 * @param lng1
	 *            set longitude of position1
	 * @param lat2
	 *            set latitude of position2
	 * @param lng2
	 *            set longitude of position2
	 * @return distance
	 */
	public final float getDistance(double lat1, double lng1, double lat2,
			double lng2) {
		Location locationA = new Location("point A");
		locationA.setLatitude(lat1);
		locationA.setLongitude(lng1);

		Location locationB = new Location("point B");

		locationB.setLatitude(lat2);
		locationB.setLongitude(lng2);

		return locationA.distanceTo(locationB);
	}

	/**
	 * Computes the distance in kilometers between two points on Earth.
	 * 
	 * @param lat1
	 *            Latitude of the first point
	 * @param lon1
	 *            Longitude of the first point
	 * @param lat2
	 *            Latitude of the second point
	 * @param lon2
	 *            Longitude of the second point
	 * @return Distance between the two points in kilometers.
	 */

	public final double distanceCalcByHaversine(double lat1, double lon1,
			double lat2, double lon2) {
		int EARTH_RADIUS_KM = 6371;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return EARTH_RADIUS_KM * c;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			finder.OnGettingLatitudeLongitude(location.getLatitude(),
					location.getLongitude(), location, uniqueCode);
		} else {
			finder.noLocationFound();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Error = " + getErrorString(connectionResult.getErrorCode()));
	}

	@Override
	public void onConnected(Bundle bundle) {
		// TODO Auto-generated method stub
		if (mUpdatesRequested) {
			removeLocationUpdates(finder);
		}
		finder.OnConnected();
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	public String getErrorString(int errorCode) {

		// Define a string to contain the error message
		String errorString;

		// Decide which error message to get, based on the error code.
		switch (errorCode) {
		case ConnectionResult.DEVELOPER_ERROR:
			errorString = "The application is misconfigured";
			break;

		case ConnectionResult.INTERNAL_ERROR:
			errorString = "An internal error occurred";
			break;

		case ConnectionResult.INVALID_ACCOUNT:
			errorString = "The specified account name is invalid";
			break;

		case ConnectionResult.LICENSE_CHECK_FAILED:
			errorString = "The app is not licensed to the user";
			break;

		case ConnectionResult.NETWORK_ERROR:
			errorString = "A network error occurred";
			break;

		case ConnectionResult.RESOLUTION_REQUIRED:
			errorString = "Additional resolution is required";
			break;

		case ConnectionResult.SERVICE_DISABLED:
			errorString = "Google Play services is disabled";
			break;

		case ConnectionResult.SERVICE_INVALID:
			errorString = "The version of Google Play services on this device is not authentic";
			break;

		case ConnectionResult.SERVICE_MISSING:
			errorString = "Google Play services is missing";
			break;

		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			errorString = "Google Play services is out of date";
			break;

		case ConnectionResult.SIGN_IN_REQUIRED:
			errorString = "The user is not signed in";
			break;

		default:
			errorString = "An unknown error occurred";
			break;
		}

		// Return the error message
		return errorString;
	}

	/**
	 * An AsyncTask that calls getFromLocation() in the background. The class
	 * uses the following generic types: Location - A
	 * {@link android.location.Location} object containing the current location,
	 * passed as the input parameter to doInBackground() Void - indicates that
	 * progress units are not used by this subclass String - An address passed
	 * to onPostExecute()
	 */
	private class GetAddressTask extends AsyncTask<Location, Void, String> {

		// Store the context passed to the AsyncTask when the system
		// instantiates it.
		private Context localContext;
		private OnLocationFinder finder;
		private int uniqueCode;

		// Constructor called by the system to instantiate the task
		public GetAddressTask(Context context, OnLocationFinder callback,
				int uniqueCode) {

			// Required by the semantics of AsyncTask
			super();
			this.uniqueCode = uniqueCode;
			// Set a Context for the background task
			localContext = context;
			this.finder = callback;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it,
		 * format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected String doInBackground(Location... params) {
			/*
			 * Get a new geocoding service instance, set for localized
			 * addresses. This example uses android.location.Geocoder, but other
			 * geocoders that conform to address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			Location location = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or
			// network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the
				 * latitude and longitude of the current location. Return at
				 * most 1 address.
				 */
				addresses = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				return EMPTY_STRING;

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				exception2.printStackTrace();
				//
				return EMPTY_STRING;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {

				// Get the first address
				Address address = addresses.get(0);
				StringBuilder builder = new StringBuilder();
				builder.append(address.getMaxAddressLineIndex() > 0 ? address
						.getAddressLine(0) : "");
				builder.append(",");
				builder.append(address.getLocality());
				builder.append(",");
				builder.append(address.getCountryName());
				// Return the text
				return builder.toString();

				// If there aren't any addresses, post a message
			} else {
				return "No address found for location";
			}
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text
		 * of the UI element that displays the address. This method runs on the
		 * UI thread.
		 */
		@Override
		protected void onPostExecute(String address) {
			if (this.finder == null) {
				return;
			}
			this.finder.onGettingAddress(address, uniqueCode);
		}
	}

	/**
	 * An AsyncTask that calls getFromLocation() in the background. The class
	 * uses the following generic types: Location - A
	 * {@link android.location.Location} object containing the current location,
	 * passed as the input parameter to doInBackground() Void - indicates that
	 * progress units are not used by this subclass String - An address passed
	 * to onPostExecute()
	 */
	private class GetLocationTask extends AsyncTask<String, Void, Location> {

		// Store the context passed to the AsyncTask when the system
		// instantiates it.
		private Context localContext;
		private OnLocationFinder finder;
		private int uniquecode;

		// Constructor called by the system to instantiate the task
		public GetLocationTask(Context context, OnLocationFinder callback,
				int uniqueCode) {

			// Required by the semantics of AsyncTask
			super();
			uniquecode = uniqueCode;
			// Set a Context for the background task
			localContext = context;
			this.finder = callback;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it,
		 * format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected Location doInBackground(String... params) {
			/*
			 * Get a new geocoding service instance, set for localized
			 * addresses. This example uses android.location.Geocoder, but other
			 * geocoders that conform to address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
			List<Address> addresses = null;
			// Try to get an address for the current location. Catch IO or
			// network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the
				 * latitude and longitude of the current location. Return at
				 * most 1 address.
				 */
				addresses = geocoder.getFromLocationName(params[0], 5);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				return null;

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				exception2.printStackTrace();
				//
				return null;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				Location location = new Location("A");
				location.setLatitude(address.getLatitude());
				location.setLongitude(address.getLongitude());
				// Format the first line of address

				return location;

				// If there aren't any addresses, post a message
			} else {
				return null;
			}
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text
		 * of the UI element that displays the address. This method runs on the
		 * UI thread.
		 */
		@Override
		protected void onPostExecute(Location location) {
			if (this.finder == null) {
				return;
			}
			if (location != null) {
				this.finder.OnGettingLatitudeLongitude(location.getLatitude(),
						location.getLongitude(), location, uniquecode);
			} else {
				this.finder.noLocationFound();
			}
		}
	}
}
