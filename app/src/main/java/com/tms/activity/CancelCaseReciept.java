package com.tms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tms.R;
import com.tms.newui.MainActivity4;
import com.tms.utility.Utility;

 /**
  * 
  * @author arvind.agarwal
  *
  */

public class CancelCaseReciept extends Activity{
	
	
	
	/**
	 * this activity is called only in cancel trip case
	 */
	
	private Button btnSubmit;
	private TextView txtDate,txtFare,imgReceipt_text2;
	private Intent intent;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_cancel_case_receipt);
		// initiating view components
		initializeView();
		
		//getting fare and date value from Driver Details class in case of cancellation
		 intent=getIntent();
		 
		 if(intent!=null){
			 
			 // setting date from cancel trip werb service response
			 txtDate.setText(intent.getStringExtra(Utility.PAYMENT_DECLINE_DATE_KEY));
			 
			 // setting cancellation fare amount from cancel webservice
			 txtFare.setText("$ "+intent.getStringExtra(Utility.PAYMENT_DECLINE_FARE_KEY));
			 
			 
			 if(intent.getStringExtra(Utility.PAYMENT_DECLINE_PAYVIA).equals("0")){
				 imgReceipt_text2.setText(getResources().getString(R.string.payvia_decline_cash));
			 }else{
				 imgReceipt_text2.setText(getResources().getString(R.string.payvia_decline));
			 }
			 
			 
		 }
		
		
		
	btnSubmit.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			startActivity(new Intent(CancelCaseReciept.this, MainActivity4.class));
			finish();
			
		}
	});
	}
	
	private void initializeView(){
		
		
		btnSubmit=(Button)findViewById(R.id.btnSubmit);
		txtFare=(TextView)findViewById(R.id.txtFare);
		txtDate=(TextView)findViewById(R.id.txtDate);
		imgReceipt_text2=(TextView)findViewById(R.id.imgReceipt_text2);
	
	
		
		
	}
}
