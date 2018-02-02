package com.tms.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tms.R;
import com.tms.broadcast.TimeAlarm;
import com.tms.model.ReservationListingModel;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

public class ReservationListing extends Activity implements XmlListener {

	/**
	 * @author arvind.agarwal
	 * 
	 * this class is used to show reservation listing
	 */
	private ImageView btnBack;
	private Button btnNewReservation;
	private ListView listReservations;
	private ArrayList<ReservationListingModel> arrayList;

	ToroSharedPrefrnce sharedPrefer;

	private Adapter adapter;
	Typeface typeFace;
	TextView txtNoReservations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reservation_listing);

		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		((TextView) findViewById(R.id.txtTitle)).setTypeface(typeFace);
		((TextView) findViewById(R.id.txt)).setTypeface(typeFace);
		txtNoReservations=(TextView)findViewById(R.id.txtNoReservations);
		txtNoReservations.setTypeface(typeFace);
		sharedPrefer = new ToroSharedPrefrnce(this);
		initializingView();
		adapter = new Adapter();
		listReservations.setAdapter(adapter);

		// ////////////////////////////////////////////////////////////

	}

	/**
	 * initializing views
	 */
	private void initializingView() {
		arrayList = new ArrayList<ReservationListingModel>();

		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnNewReservation=(Button)findViewById(R.id.btnNewReservation);
		btnNewReservation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				
			}
		});
		listReservations = (ListView) findViewById(R.id.listReservations);

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Calendar cal = Calendar.getInstance();

		String month = "", day = "";

		month = "" + (cal.get(Calendar.MONTH) + 1);
		day = "" + cal.get(Calendar.DAY_OF_MONTH);

		if (month.length() == 1) {
			month = "0" + month;
		}

		if (day.length() == 1) {
			day = "0" + day;
		}
		String currentDate = cal.get(Calendar.YEAR) + "-" + month + "-" + day;

		if (CheckInternetConnectivity
				.checkinternetconnection(ReservationListing.this)) {
			
			
			String xmlToSend="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><reservationList><riderId><![CDATA["+ (new ToroSharedPrefrnce(ReservationListing.this).getUserId())
					+"]]></riderId></reservationList>";


			new SendXmlAsync(
					URL.BASE_URL
							+ URL.RESERVATIONLISTNG
						
							, xmlToSend,
					ReservationListing.this, ReservationListing.this, true)
					.execute();

		} else {
			showDialog("Alert!", "Please check internet connection");

		}
	}

	
	
	
	// showing alert dialog
	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(ReservationListing.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_warning);
		TextView txtTitle = (TextView) dialog
				.findViewById(R.id.dialog_title_text);
		TextView txtMessage = (TextView) dialog
				.findViewById(R.id.dialog_message_text);

		Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
		Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

		btnYes.setVisibility(View.GONE);
		btnNo.setVisibility(View.GONE);
		Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});

		txtTitle.setText(title);
		txtMessage.setText(message);

		dialog.show();

	}

	
	/*
	 * (non-Javadoc)
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String)
	 * this method is used to tackle webservice response 
	 * 
	 */
	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub\

		// {"userReservations":[{"resvId":"1","picUpLoc":"DT Cinema Chandigarh","dropOffLoc":"ISBT 43 Chandigarh","picUpTime":"12:12 PM","picUpDate":"12\/25\/2013","fareQuote":"191","taxiType":"Black
		// Car","reservationStatus":"0"}],"message":"Your reservations."}

		
//		{"reservationList":[{"id":"1","driverId":"1","name":"shaktimasn","picUpLoc":"Chandigarh IT Park, Chandigarh, India","dropOffLoc":"Kharar, Punjab, India","picUpTime":"14:52 pm","picUpDate":"February 04, 2015","phone":"7696209733","fareQuote":"100","carType":"Xpress","reservationStatus":"Reservation Cancelled.","driverStatus":"1","paymentStatus":"1","invoiceId":"54d08672775eb","airline":"","flightNumber":"","reservStatus":"0","taxiType":"1","taxiNumber":"XPress","taxiModel":"Lexus RX 400","driverImage":"http:\/\/timerides.taximobilesolutions.com\/assets\/userImages\/1422951838kapil.jpg","driverName":"Kapil","driverPhone":"+19780666296"},{"id":"2","driverId":"1","name":"arvind","picUpLoc":"Chandigarh IT Park, Chandigarh, India","dropOffLoc":"Kharar, Punjab, India","picUpTime":"15:05 pm","picUpDate":"February 04, 2015","phone":"7696209733","fareQuote":"100","carType":"Xpress","reservationStatus":"Reservation Cancelled.","driverStatus":"1","paymentStatus":"1","invoiceId":"54d089a351ece","airline":"","flightNumber":"","reservStatus":"0","taxiType":"1","taxiNumber":"XPress","taxiModel":"Lexus RX 400","driverImage":"http:\/\/timerides.taximobilesolutions.com\/assets\/userImages\/1422951838kapil.jpg","driverName":"Kapil","driverPhone":"+19780666296"},{"id":"3","driverId":"1","name":"Arvind","picUpLoc":"Kasauli, Himachal Pradesh, India","dropOffLoc":"Chandigarh, India","picUpTime":"17:42 pm","picUpDate":"February 04, 2015","phone":"7696209733","fareQuote":"237","carType":"Xpress","reservationStatus":"Reservation Completed.","driverStatus":"1","paymentStatus":"1","invoiceId":"54d0acd2e28ed","airline":"","flightNumber":"","reservStatus":"2","taxiType":"1","taxiNumber":"XPress","taxiModel":"Lexus RX 400","driverImage":"http:\/\/timerides.taximobilesolutions.com\/assets\/userImages\/1422951838kapil.jpg","driverName":"Kapil","driverPhone":"+19780666296"},{"id":"4","driverId":"0","name":"Aggarwal","picUpLoc":"Kasauli, Himachal Pradesh, India","dropOffLoc":"Sector 17, Chandigarh, India","picUpTime":"14:45 pm","picUpDate":"February 05, 2015","phone":"7696209733","fareQuote":"235","carType":"Xpress","reservationStatus":"Reservation Cancelled.","driverStatus":"0","paymentStatus":"1","invoiceId":"54d1d50004f1c","airline":"","flightNumber":"","reservStatus":"0","taxiType":"","taxiNumber":"","taxiModel":"","driverImage":"","driverName":"","driverPhone":""},{"id":"5","driverId":"0","name":"Ritesh","picUpLoc":"Kurali, Punjab, India","dropOffLoc":"Kharar, Punjab, India","picUpTime":"17:23 pm","picUpDate":"February 05, 2015","phone":"766209733","fareQuote":"55","carType":"Xpress","reservationStatus":"Reservation Cancelled.","driverStatus":"0","paymentStatus":"1","invoiceId":"54d1f9fcb73bb","airline":"","flightNumber":"","reservStatus":"0","taxiType":"","taxiNumber":"","taxiModel":"","driverImage":"","driverName":"","driverPhone":""},{"id":"11","driverId":"0","name":"Arvind Aggarwal","picUpLoc":"Chandigarh IT Park, Chandigarh, India","dropOffLoc":"Kharar, Punjab, India","picUpTime":"19:13 pm","picUpDate":"February 05, 2015","phone":"7696209733","fareQuote":"100","carType":"Xpress","reservationStatus":"Confirmation Pending.","driverStatus":"0","paymentStatus":"0","invoiceId":"","airline":"","flightNumber":"","reservStatus":"1","taxiType":"","taxiNumber":"","taxiModel":"","driverImage":"","driverName":"","driverPhone":""},{"id":"12","driverId":"0","name":"Kapil","picUpLoc":"Chandigarh IT Park, Chandigarh, India","dropOffLoc":"Kharar Railway Station, Railway Station Road, Khuni Majra, Punjab, India","picUpTime":"19:22 pm","picUpDate":"February 05, 2015","phone":"7696209733","fareQuote":"111","carType":"Xpress","reservationStatus":"Confirmation Pending.","driverStatus":"0","paymentStatus":"0","invoiceId":"","airline":"","flightNumber":"","reservStatus":"1","taxiType":"","taxiNumber":"","taxiModel":"","driverImage":"","driverName":"","driverPhone":""},{"id":"6","driverId":"0","name":"Pankaj","picUpLoc":"332 Jefferson Road, Rochester, NY, United States","dropOffLoc":"Netsmartz House, Phase - I, Chandigarh, India","picUpTime":"20:09 pm","picUpDate":"February 06, 2015","phone":"15543438181513","fareQuote":"36","carType":"BLACK CAR","reservationStatus":"Confirmation Pending.","driverStatus":"0","paymentStatus":"0","invoiceId":"","airline":"","flightNumber":"","reservStatus":"1","taxiType":"","taxiNumber":"","taxiModel":"","driverImage":"","driverName":"","driverPhone":""},{"id":"7","driverId":"0","name":"Panakaj","picUpLoc":"332 Jefferson Davis Highway, Falmouth, VA, United States","dropOffLoc":"332 Jefferson Road, Rochester, NY, United States","picUpTime":"20:11 pm","picUpDate":"February 07, 2015","phone":"5464434644","fareQuote":"11","carType":"BLACK CAR","reservationStatus":"Reservation Cancelled.","driverStatus":"0","paymentStatus":"0","invoiceId":"","airline":"","flightNumber":"","reservStatus":"0","taxiType":"","taxiNumber":"","taxiModel":"","driverImage":"","driverName":"","driverPhone":""}],"message":"You have reservations."}
		Log.i("respose", respose);

		if (respose.contains("cancelReservation")) {

			try {
				JSONObject jsonObj = new JSONObject(respose);
				if (jsonObj.getString("cancelReservation").equals("1")) {

					showDialog("Message", jsonObj.getString("message"));

					onResume();

				} else {
					showDialog("Message", jsonObj.getString("message"));

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (respose.contains("reservationList")) {
			arrayList.clear();
			
	//		 {"reservationList":[{"id":"1","driverId":"0","name":"Kapil Bansal","picUpLoc":"DT Cinema Chandigarh","dropOffLoc":"ISBT 43 Chandigarh","picUpTime":"January 12, 2015","picUpDate":"February 12, 2015","phone":"+919780666296","fareQuote":"39","carType":"2","reservationStatus":"No driver assigned.","driverStatus":"0","paymentStatus":"1","invoiceId":"54b3ae746c671","airline":"Flight Name","flightNumber":"123456","reservStatus":"1"}],"message":"You have reservations."}

			try {
				JSONObject jsonObj = new JSONObject(respose);
				if(jsonObj.getString("reservationList").equals("-3")){
					
					
				}else{
					
				
				
				
				
				JSONArray jsoArray = jsonObj.getJSONArray("reservationList");

				if (jsoArray != null && jsoArray.length() != 0) {

					for (int i = 0; i < jsoArray.length(); i++) {

						ReservationListingModel model = new ReservationListingModel();
						model.setResvId(jsoArray.getJSONObject(i).getString(
								"id"));
						model.setPicUpLoc(jsoArray.getJSONObject(i).getString(
								"picUpLoc"));
						model.setDropOffLoc(jsoArray.getJSONObject(i)
								.getString("dropOffLoc"));
						model.setPicUpTime(jsoArray.getJSONObject(i).getString(
								"picUpTime"));
						model.setPicUpDate(jsoArray.getJSONObject(i).getString(
								"picUpDate"));
						model.setFareQuote(jsoArray.getJSONObject(i).getString(
								"fareQuote"));
						model.setTaxiType(jsoArray.getJSONObject(i).getString(
								"carType"));
						model.setAirLine(jsoArray.getJSONObject(i).getString(
								"airline"));
						model.setFlight(jsoArray.getJSONObject(i).getString(
								"flightNumber"));
						model.setRiderName(jsoArray.getJSONObject(i).getString(
								"name"));
						model.setRiderPhone(jsoArray.getJSONObject(i)
								.getString("phone"));
						model.setReservationStatus(jsoArray.getJSONObject(i)
								.getString("reservStatus"));
						
//	"taxiType":"","taxiNumber":"","taxiModel":"","driverImage":"","driverName":"","driverPhone":""
						
						model.setTaxiNumber(jsoArray.getJSONObject(i)
								.getString("taxiNumber"));		
						model.setTaxiModel(jsoArray.getJSONObject(i)
								.getString("taxiModel"));
						model.setDriverImage(jsoArray.getJSONObject(i)
								.getString("driverImage"));
						model.setDriverName(jsoArray.getJSONObject(i)
								.getString("driverName"));
						model.setDriverPhone(jsoArray.getJSONObject(i)
								.getString("driverPhone"));
						
						model.setStatus(jsoArray.getJSONObject(i)
								.getString("reservationStatus"));
						
						
						model.setTitle(jsoArray.getJSONObject(i)
								.getString("resTextStatus"));
						
						model.setItemToMove(jsoArray.getJSONObject(i)
								.getString("itemToMove"));
						model.setWeight(jsoArray.getJSONObject(i).getString("weight"));
						model.setDimension(jsoArray.getJSONObject(i).getString("dimension"));
						model.setComment(jsoArray.getJSONObject(i).getString("comment"));
						

						arrayList.add(model);
					}

				} else {

					showDialog("Message", jsonObj.getString("message"));

				}
				
				}
				
				
				if(arrayList.size()==0){
					
					txtNoReservations.setVisibility(View.VISIBLE);
					txtNoReservations.bringToFront();
					
				}else{
					txtNoReservations.setVisibility(View.GONE);
					listReservations.bringToFront();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			adapter.notifyDataSetChanged();

		}

	}
	
	
	//Adapter for listview

	private class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = new ViewHolder();

			LayoutInflater inflater = LayoutInflater
					.from(ReservationListing.this);
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.reservation_list, null);

				holder.txtLocation = (TextView) convertView
						.findViewById(R.id.txtLoc);
				holder.txtFare = (TextView) convertView
						.findViewById(R.id.txtFare);
				holder.btnCancel = (Button) convertView
						.findViewById(R.id.btnCancel);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.txtLocation.setText(arrayList.get(position).getPicUpLoc());
			holder.txtFare.setText("on "
					+ arrayList.get(position).getPicUpDate() + " at "
					+ arrayList.get(position).getPicUpTime());

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(ReservationListing.this,
							com.tms.activity.ReservationDetail.class);
					// intent.putExtra("RESERVATION_ID",
					// arrayList.get(position).getResvId());
					intent.putExtra("reservationObj", arrayList.get(position));
					intent.putExtra("via", "LIST");
					startActivity(intent);

				}
			});

		/*	holder.btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					createalert(position);

				}
			});
*/
			return convertView;
		}

	}
	
	// this class holds object of listView

	class ViewHolder {

		TextView txtLocation, txtFare;

		Button btnCancel;
	}

	private void cancelNotification(String dateTimeStr, String id,
			String strDate, String strTime, String pickUpLoc) {

		SimpleDateFormat formatToCompare = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm a");

		Date dateNotification = null;

		try {
			dateNotification = formatToCompare.parse(dateTimeStr);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = null;
		intent = new Intent(getApplicationContext(), TimeAlarm.class);
		intent.putExtra("NOTIFICATION", "You have a reservation for " + strDate
				+ " at " + strTime + ".\nPickup Location " + pickUpLoc);
		intent.putExtra("ID", Integer.parseInt(id));
		intent.putExtra("LONG", dateNotification.getTime() - 5400000);

		PendingIntent sender = PendingIntent.getBroadcast(
				getApplicationContext(), Integer.parseInt(id), intent, 0);

		if (dateNotification.getTime() > System.currentTimeMillis()) {

			AlarmManager am = null;
			am = (AlarmManager) getSystemService(ALARM_SERVICE);

			// am.setRepeating(AlarmManager.RTC_WAKEUP,
			// dateNotification.getTime(), AlarmManager.INTERVAL_DAY,
			// sender);

			am.cancel(sender);
		}

	}

	
	// this alert message is used to get confirmation for canceling reservation 
	public void createalert(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ReservationListing.this);

		builder.setTitle("Alert!");
		builder.setMessage("Are you sure want to cancel the reservation?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				Calendar cal = Calendar.getInstance();

				String hour = "" + cal.get(Calendar.HOUR_OF_DAY);
				String minute = "" + cal.get(Calendar.MINUTE);
				String sec = "" + cal.get(Calendar.SECOND);

				String month = "" + (cal.get(Calendar.MONTH) + 1);
				String day = "" + cal.get(Calendar.DAY_OF_MONTH);

				if (hour.length() == 1) {
					hour = "0" + hour;
				}
				if (minute.length() == 1) {
					minute = "0" + minute;
				}

				if (sec.length() == 1) {
					sec = "0" + sec;
				}

				if (month.length() == 1) {
					month = "0" + month;
				}

				if (day.length() == 1) {
					day = "0" + day;
				}

				String currentTime = hour + ":" + minute + ":" + sec;
				String currentDate = cal.get(Calendar.YEAR) + "-" + month + "-"
						+ day;

				// String str=
				// "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><cancelReservation><reservationId><![CDATA[42]]></reservationId><riderId><![CDATA[10]]></riderId><curDate><![CDATA[2014-01-29]]></curDate><curTime><![CDATA[21:28:10]]></curTime></cancelReservation>";

				
				
				// webservice hit for cancel previous reservation
				String entity = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><cancelReservation><reservationId><![CDATA["
						+ arrayList.get(position).getResvId()
						+ "]]></reservationId><riderId><![CDATA["
						+ sharedPrefer.getUserId()
						+ "]]></riderId><curDate><![CDATA["
						+ currentDate
						+ "]]></curDate><curTime><![CDATA["
						+ currentTime
						+ "]]></curTime></cancelReservation>";

				new SendXmlAsync(URL.BASE_URL + URL.CANCEL_RESERVATION, entity,
						ReservationListing.this, ReservationListing.this, true)
						.execute();

				cancelNotification(arrayList.get(position).getPicUpDate() + " "
						+ arrayList.get(position).getPicUpTime(), arrayList
						.get(position).getResvId(), arrayList.get(position)
						.getPicUpDate(),
						arrayList.get(position).getPicUpTime(),
						arrayList.get(position).getPicUpLoc());

			}
		});

		builder.setNegativeButton("No", null);

		builder.show();

	}

}
