package com.tms.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tms.R;
import com.tms.utility.URL;
import com.tms.utility.Utility;

public class AboutUs extends Activity {
	
	// font style
	Typeface typeFace;
	
	

	@Override
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activy_about_us);
		typeFace=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE);
		
		//setting fontstyle on header
		((TextView)findViewById(R.id.headerText)).setTypeface(typeFace);
		ImageView btnBAck = (ImageView) findViewById(R.id.btnBack);
		
		
		//back button click
		btnBAck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
		// initiating webView object
		
		WebView webView=(WebView)findViewById(R.id.txtAboutUs);
		
		
		//loading about us url in webview
		
		webView.loadUrl(URL.BASE_URL+URL.ABOUT_US);
	

		
		
	}
	
	
}
