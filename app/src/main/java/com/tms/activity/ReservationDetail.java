package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationDetail extends Activity  implements XmlListener {
	
	/**
	 * @author arvind.agarwal
	 * 
	 * this method is used to show reservation details
	 */
	
	TextView txtStatus,txtName,txtPick,txtDestination,txtPickTime,txtPickDate,txtPhNumber,txtCarType,txtAirLine,txtFlight,txtFare,txtWeight,txtDimensions,txtComments,txtItem;
	ImageView btnBack;
	Button btnConfirmation,btndriverDetails,btnCancel;
	Intent intent;
	ReservationListingModel model;
	
	private String driverName="",driverPhone="",taxiNumber,taxiModel="";
	
	private Typeface typeFace;
	
	String reservation_id="";
	
	private LinearLayout layoutBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reservation_details);
		typeFace=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE);
		((TextView)findViewById(R.id.txtTitle)).setTypeface(typeFace);
		initialize();
		
		intent=getIntent();
		
		
		if(intent.getStringExtra("via").equals("LIST")){
		model=(ReservationListingModel)intent.getSerializableExtra("reservationObj");
		
		
		reservation_id=model.getResvId();
		txtPick.setText(model.getPicUpLoc());
		txtDestination.setText(model.getDropOffLoc());
		txtPickTime.setText(model.getPicUpTime());
		txtPickDate.setText(model.getPicUpDate());
		txtCarType.setText(model.getTaxiType());
		txtAirLine.setText(model.getAirLine());
		txtFlight.setText(model.getFlight());
		txtName.setText(model.getRiderName());
		txtPhNumber.setText(model.getRiderPhone());
		

		
		if(!model.getItemToMove().equals("")){
			
			txtItem.setVisibility(View.VISIBLE);
			txtWeight.setVisibility(View.VISIBLE);
			txtDimensions.setVisibility(View.VISIBLE);
			txtComments.setVisibility(View.VISIBLE);
			
			txtItem.setText(model.getItemToMove());
			txtWeight.setText(model.getWeight());
			txtDimensions.setText(model.getDimension());
			txtComments.setText(model.getComment());
		
		}
		
		
		txtStatus.setText(model.getTitle());
		
		
		if(model.getStatus().contains("Cancelled") || model.getStatus().contains("Completed")){
			
			layoutBtn.setVisibility(View.GONE);
		}
		
		
		txtFare.setText(model.getFareQuote());
		
		
		driverName=model.getDriverName();
		driverPhone=model.getDriverPhone();
		taxiNumber=model.getTaxiNumber();
		taxiModel=model.getTaxiModel();
		
		
		if(driverName.equals("")){
			
			btndriverDetails.setText("No driver assigned");
			
			btndriverDetails.setAlpha(0.7f);
		}
		
		if(model.getStatus().contains(""))
		
		if(model.getStatus().contains("Pending")){
			btnConfirmation.setVisibility(View.VISIBLE);
			btndriverDetails.setVisibility(View.GONE);
		}else{
			btnConfirmation.setVisibility(View.GONE);
			btndriverDetails.setVisibility(View.VISIBLE);
			
			
		}
		}
		
		
	}
	
	
	/**
	 * initializing view
	 */
	public void initialize(){
		btnBack=(ImageView) findViewById(R.id.btnBack);
		
		txtStatus=(TextView)findViewById(R.id.txtStatus);
		
		btnCancel=(Button)findViewById(R.id.btnCancel);
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		txtName=(TextView)findViewById(R.id.edtName);
		txtPick=(TextView)findViewById(R.id.edtPLoc);
		txtDestination=(TextView)findViewById(R.id.edtDLoc);
		txtPickTime=(TextView)findViewById(R.id.edtPTime);
		txtPickDate=(TextView)findViewById(R.id.edtPDate);
		txtPhNumber=(TextView)findViewById(R.id.edtPhNumber);
		txtCarType=(TextView)findViewById(R.id.edtCarType);
		txtAirLine=(TextView)findViewById(R.id.edtAirLine);
		txtFlight=(TextView)findViewById(R.id.edtFlight);
		btnConfirmation=(Button)findViewById(R.id.btnConfirmation);
		txtFare=(TextView)findViewById(R.id.edtFare);
		btndriverDetails=(Button)findViewById(R.id.btndriverDetails);
		layoutBtn=(LinearLayout)findViewById(R.id.layoutBtn);
		
		txtItem=(TextView)findViewById(R.id.edtItemToMove);
		txtWeight=(TextView)findViewById(R.id.edtWeight);
		txtDimensions=(TextView)findViewById(R.id.edtDimensions);
		txtComments=(TextView)findViewById(R.id.edtComments);
		
		
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(CheckInternetConnectivity.checkinternetconnection(ReservationDetail.this)){
					String xmlToSend="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><cancelReservation><reservationId><![CDATA["+reservation_id+"]]></reservationId><riderId><![CDATA["+new ToroSharedPrefrnce(ReservationDetail.this).getUserId()+"]]></riderId><curDate><![CDATA[]]></curDate><curTime><![CDATA[]]></curTime></cancelReservation>";
			
					
					new  SendXmlAsync(URL.BASE_URL+URL.CANCEL_RESERVATION, xmlToSend, ReservationDetail.this,ReservationDetail.this, true).execute();
				
				
				}
				
			}
		});

		
		btnConfirmation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(CheckInternetConnectivity.checkinternetconnection(ReservationDetail.this)){
					
					String xmlToSend="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><confirmReservation><riderId><![CDATA["+(new ToroSharedPrefrnce(ReservationDetail.this).getUserId())+"]]></riderId><reservationId><![CDATA["+model.getResvId()+"]]></reservationId></confirmReservation>";
					
					
					new SendXmlAsync(URL.BASE_URL+URL.CONFIRM_RESERVATION, xmlToSend, ReservationDetail.this, ReservationDetail.this, true).execute();
					
					
				}
				
			}
		});
		
		btndriverDetails.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!driverName.equals("")){
					
					showdriverDetails(driverName, driverPhone, taxiModel, taxiNumber);
				}
				
				
				
			}
		});
		
		
		
		
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(intent.getStringExtra("via").equals("PUSH")){
			
			model=new ReservationListingModel();
			
			
			reservation_id=getIntent().getStringExtra("RESERVATION_ID");
		
		if(CheckInternetConnectivity.checkinternetconnection(ReservationDetail.this)){
	
		String xmltoSend="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><reservationList><reservationId><![CDATA["+getIntent().getStringExtra("RESERVATION_ID")+"]]></reservationId></reservationList>";
			new SendXmlAsync(URL.BASE_URL+URL.RESERVATIONDETAIL, xmltoSend, ReservationDetail.this, ReservationDetail.this, true).execute();
		}else{
			
		}
		
		btnConfirmation.setVisibility(View.GONE);
		btndriverDetails.setVisibility(View.VISIBLE); 
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String)
	 * this method is used to tackle webservice response
	 */

	@Override
	public void onResponse(String respose) {
		
		
		//cancel reservation response
		
		if(respose.contains("cancelReservation")){
	
			
			try {
				JSONObject jsonObject=new JSONObject(respose);
				
			
				
				if(jsonObject.getString("cancelReservation").equals("1")){
					
					showDialog("Message", jsonObject.getString("message"));
					
				}else{
					showDialog("Message", jsonObject.getString("message"));
				}
				
				
				
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		/*	{"cancelReservation":"-2","message":"Server Error."}
			Success : {"cancelReservation":"1","message":"Reservation cancelled successfully."}*/
			
		}else
		// TODO Auto-generated method stub
		
		 if(respose.contains("confirmReservation")){
				
				/*Server Error : {"confirmReservation":"-2","message":"Server Error."}
			Success : {"confirmReservation":"1","message":"your reservation has been confirmed successfully."}
			Already Confirmed : {"confirmReservation":"-4","message":"Reservation already confirmed."}
			Payment Declined (Un-confirmed) : {"confirmReservation":"-3","message":"Please check with your bank and try agian. If this continues to happen, please try a different card."}
				*/
				
				try {
					JSONObject jsonObject=new JSONObject(respose);
					
					
					if(jsonObject.getString("confirmReservation").equals("1")){
						
						showDialog("Message", jsonObject.getString("message"));
						
					}else{
						showDialog("Message", jsonObject.getString("message"));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		
		
		// reservation details
		
		
	
if(respose.contains("reservationDetail")){
	
	
	try {
		JSONObject jObj=new JSONObject(respose);
		JSONObject jObjChild=jObj.getJSONObject("reservationDetail");
		txtName.setText(jObjChild.getString("name"));
		txtPick.setText(jObjChild.getString("picUpLoc"));
		txtDestination.setText(jObjChild.getString("dropOffLoc"));
		txtPickTime.setText(jObjChild.getString("picUpTime"));
		txtPickDate.setText(jObjChild.getString("picUpDate"));
		txtCarType.setText(jObjChild.getString("carType"));
		txtAirLine.setText(jObjChild.getString("airline"));
		txtFlight.setText(jObjChild.getString("flightNumber"));
		txtPhNumber.setText(jObjChild.getString("phone"));
		txtFare.setText(jObjChild.getString("fareQuote"));
		if(!jObjChild.getString("itemToMove").equals("")){
			
			txtItem.setText(jObjChild.getString("itemToMove"));
			txtWeight.setText(jObjChild.getString("weight"));
			txtDimensions.setText(jObjChild.getString("dimension"));
			txtComments.setText(jObjChild.getString("comment"));
			txtItem.setVisibility(View.VISIBLE);
			txtWeight.setVisibility(View.VISIBLE);
			txtDimensions.setVisibility(View.VISIBLE);
			txtComments.setVisibility(View.VISIBLE);
		
			
		}
		
		
		
		
		driverName=jObjChild.getString("driverName");
		driverPhone=jObjChild.getString("driverPhone");
		taxiModel=jObjChild.getString("taxiModel");
		taxiNumber=jObjChild.getString("taxiNumber");
		
		
		
		if(driverName.equals("")){
			
			btndriverDetails.setText("No driver assigned");
			btndriverDetails.setAlpha(.7f);
		}
		
		
		
		
		
		
		
		model.setResvId(jObjChild.getString("id"));
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	
}
	
	
	}
	
	private void setNotification(String dateTimeStr,String id,String strDate,String strTime,String pickUpLoc)
	{

		
		SimpleDateFormat formatToCompare = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm a");

		Date dateNotification = null;
		
			try {
				dateNotification = formatToCompare
						.parse(dateTimeStr);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		Intent intent = null;
		intent = new Intent(getApplicationContext(), TimeAlarm.class);
		intent.putExtra("NOTIFICATION", "You have a reservation for "+strDate+" at "+strTime+".\nPickup Location "+pickUpLoc);
		intent.putExtra("ID", Integer.parseInt(id));
		intent.putExtra("LONG", dateNotification.getTime()-5400000);

		PendingIntent sender = PendingIntent.getBroadcast(
				getApplicationContext(),Integer.parseInt(id),
				intent, 0);

	
		
		
		if(dateNotification.getTime()>System.currentTimeMillis()){

			AlarmManager am = null;
			am = (AlarmManager) getSystemService(ALARM_SERVICE);

			// am.setRepeating(AlarmManager.RTC_WAKEUP,
			// dateNotification.getTime(), AlarmManager.INTERVAL_DAY,
			// sender);
			
		
			am.set(AlarmManager.RTC_WAKEUP, dateNotification.getTime()-5400000,
					sender);
		}

		

	}
	
	
	/**
	 * 
	 * @param title
	 * @param message
	 * show alert message
	 */
	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(ReservationDetail.this,
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
				
				finish();

			}
		});

		txtTitle.setText(title);
		txtMessage.setText(message);

		dialog.show();

	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		/*if(GCMIntentService.player!=null){
			
			GCMIntentService.player.stop();
			GCMIntentService.player=null;
		}*/
	}
	
	
	/**\
	 * 
	 * @param name
	 * @param contact
	 * @param taxi
	 * @param number
	 * 
	 * show driver details 
	 */
	private void showdriverDetails(String name,final String contact,String taxi,String number ){
		
		final Dialog dialog=new Dialog(ReservationDetail.this, style.Theme_Translucent_NoTitleBar_Fullscreen);
	dialog.setContentView(R.layout.dialog_driver_details);
	
	
	final TextView txtDriverName,txtContact,txtTaxiType,txtNumber;
	ImageView imgCancel;
	
	imgCancel=(ImageView)dialog.findViewById(R.id.imgCancel);
	
	txtDriverName=(TextView)dialog.findViewById(R.id.txtNameActual);
	txtContact=(TextView)dialog.findViewById(R.id.txtContactActual);
	txtTaxiType=(TextView)dialog.findViewById(R.id.txtTaxiTypeActual);
	txtNumber=(TextView)dialog.findViewById(R.id.txtTaxiNumberActual);
		
	txtDriverName.setText(name);
	txtContact.setText(Html.fromHtml("<u>"+contact+"</u>"));
	txtTaxiType.setText(taxi);
	txtNumber.setText(number);
	
	
	imgCancel.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.dismiss();
		}
	});
	
	txtContact.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String uri = "tel:" + contact;
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse(uri));
			startActivity(intent);
		}
	});
	
	
	dialog.show();
		
		
	}
	

}
