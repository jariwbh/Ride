package com.tms;

import android.R.style;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.ResponseListener;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.WebServiceAsynk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * 
 * @author arvind.agarwal
 *this class is used to share promo code on Facebook, via SMS, Email, Twitter 
 */
public class SharingToro extends Activity implements OnClickListener,
		ResponseListener {

	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	Session session;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");

	private Button btnFaceBook, btnTwitter, btnTextSMS, btnEmail;

	ImageView btnBack;
	private TextView txtViewPromoCode;
	ToroSharedPrefrnce mSharedPrefer;
	Typeface typeFace, typeFaceLight;
	private UiLifecycleHelper uiHelper;
	private String APP_NAME;
	private String REFFERAL_BONUS = "";

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			// System.out.println("Logged in...");
		} else if (state.isClosed()) {
			// System.out.println("Logged out...");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_toro_ride);
		APP_NAME = getResources().getString(R.string.aplication_name);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
//getting type Face
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		typeFaceLight = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_HELVATICA_LIGHT);
		((TextView) findViewById(R.id.header_text)).setTypeface(typeFace);
		((TextView) findViewById(R.id.txtIcon)).setTypeface(typeFaceLight);

		((TextView) findViewById(R.id.textPromoCode))
				.setTypeface(typeFaceLight);
		// ((TextView)findViewById(R.id.textPromoCode_1)).setTypeface(typeFaceLight);
		((TextView) findViewById(R.id.textPromoCode_2))
				.setTypeface(typeFaceLight);
		((TextView) findViewById(R.id.textPromoCode_3))
				.setTypeface(typeFaceLight);
		// ((TextView)findViewById(R.id.textPromoCode_4)).setTypeface(typeFaceLight);

		((TextView) findViewById(R.id.textPromoCode_5))
				.setTypeface(typeFaceLight);
		((TextView) findViewById(R.id.textPromoCode_6))
				.setTypeface(typeFaceLight);
		// ((TextView)findViewById(R.id.textPromoCode_7)).setTypeface(typeFaceLight);
		((TextView) findViewById(R.id.textPromoCode_8))
				.setTypeface(typeFaceLight);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Settings.setShouldAutoPublishInstall(true);

		session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}

			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}

		initialize();

		btnFaceBook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (FacebookDialog.canPresentShareDialog(SharingToro.this,
						FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {

					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
							SharingToro.this)
							.setLink(
									URL.SITE_LINK + "invite/"
											+ mSharedPrefer.getReferal())

							.setDescription(
									"Use my " + APP_NAME + " code \""
											+ mSharedPrefer.getReferal()
											+ "\" and get " + REFFERAL_BONUS
											+ " off your first ride with " + APP_NAME
											+ ". Redeem it at " + URL.SITE_LINK
											+ "invite/"
											+ mSharedPrefer.getReferal())
							.setCaption(
									getResources().getString(R.string.aplication_name))
							.build();
					uiHelper.trackPendingDialogCall(shareDialog.present());

				} else {
					
					
					if(session.isOpened()){
					
					
					

					Bundle params = new Bundle();
					params.putString("caption",
							getResources().getString(R.string.aplication_name));
					// params.putString("caption", URLConstants.URL_TO_SHARE);
					params.putString("description", "Use my " + APP_NAME
							+ " code \"" + mSharedPrefer.getReferal()
							+ "\" and get  " + REFFERAL_BONUS
							+ " off your first ride with " + APP_NAME
							+ ". Redeem it at " + URL.SITE_LINK + "invite/"
							+ mSharedPrefer.getReferal());
					params.putString("link", URL.SITE_LINK + "invite/"
							+ mSharedPrefer.getReferal());
					// params.putString("picture",
					// "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

					WebDialog feedDialog = new WebDialog.FeedDialogBuilder(
							SharingToro.this, session,
							params).build();
					feedDialog.show();
					
					}

				}

			}
		});

		// upDateView();

		printHashKey();

	}

	private void initialize() {

		mSharedPrefer = new ToroSharedPrefrnce(SharingToro.this);

		btnFaceBook = (Button) findViewById(R.id.btnFacebook);
		btnFaceBook.setTypeface(typeFaceLight);
		btnTwitter = (Button) findViewById(R.id.btnTwitter);
		btnTwitter.setTypeface(typeFaceLight);
		btnTextSMS = (Button) findViewById(R.id.btnSms);
		btnTextSMS.setTypeface(typeFaceLight);
		btnEmail = (Button) findViewById(R.id.btnEmail);
		btnEmail.setTypeface(typeFaceLight);
		btnBack = (ImageView) findViewById(R.id.btnBack);
		txtViewPromoCode = (TextView) findViewById(R.id.textPromoCode_1);
		txtViewPromoCode.setText(mSharedPrefer.getReferal());

		btnTwitter.setOnClickListener(this);
		btnTextSMS.setOnClickListener(this);
		btnEmail.setOnClickListener(this);
		btnBack.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		session = Session.getActiveSession();

		session = Session.getActiveSession();

		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {
					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {
						Log.e("Activity",
								String.format("Error: %s", error.toString()));
					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
						Log.i("Activity", "Success!");
					}
				});

		/*
		 * if (session.isOpened()) {
		 * 
		 * // Check for publish permissions List<String> permissions =
		 * session.getPermissions(); if (!isSubsetOf(PERMISSIONS, permissions))
		 * {
		 * 
		 * Session.NewPermissionsRequest newPermissionsRequest = new
		 * Session.NewPermissionsRequest( this, PERMISSIONS);
		 * session.requestNewPublishPermissions(newPermissionsRequest); return;
		 * } }
		 */

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {

			// upDateView();

		}

	}

	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(SharingToro.this)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}


	
	

	private void showAlert() {

		AlertDialog.Builder alert = new AlertDialog.Builder(SharingToro.this);
		alert.setMessage("You have successfully posted on Facebook");
		alert.setPositiveButton("Dismiss", null);
		alert.show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnTwitter:

			String tweet = URLEncoder.encode("Use my " + APP_NAME + " code \""
					+ mSharedPrefer.getReferal() + "\" and get " + REFFERAL_BONUS
					+ " off your first ride with " + APP_NAME + ". Redeem it at "
					+ URL.SITE_LINK + "invite/" + mSharedPrefer.getReferal());

			String tweetUrl = "https://twitter.com/intent/tweet?text=" + tweet;

			Uri uri = Uri.parse(tweetUrl);
			startActivity(new Intent(Intent.ACTION_VIEW, uri));

			break;

		case R.id.btnSms:

			Intent sendIntent = new Intent(Intent.ACTION_VIEW);

			sendIntent.putExtra("sms_body", "Use my " + APP_NAME + " code \""
					+ mSharedPrefer.getReferal() + "\" and get " + REFFERAL_BONUS
					+ " off your first ride with " + APP_NAME + ". Redeem it at "
					+ URL.SITE_LINK + "invite/" + mSharedPrefer.getReferal());
			sendIntent.setType("vnd.android-dir/mms-sms");
			startActivity(sendIntent);

			break;
		case R.id.btnEmail:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");

			i.putExtra(Intent.EXTRA_SUBJECT, "Invitation for "
					+ getResources().getString(R.string.aplication_name));

			i.putExtra(
					Intent.EXTRA_TEXT,
					"Use my "
							+ APP_NAME
							+ " code \""
							+ mSharedPrefer.getReferal()
							+ "\" and get "
							+ REFFERAL_BONUS
							+ " off your first ride with "
							+ APP_NAME
							+ ". Redeem it at "
							+ Html.fromHtml(URL.SITE_LINK + "/invite/"
									+ mSharedPrefer.getReferal()));

			

			startActivity(i);

			break;

		case R.id.btnBack:

			finish();
			break;

		}

	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
		if (CheckInternetConnectivity.checkinternetconnection(SharingToro.this)) {
			new WebServiceAsynk(URL.BASE_URL + URL.REFFERAL_BONUS,
					SharingToro.this, SharingToro.this, true, "first")
					.execute();

		} else {

			showDialog("Alert!", "No Internet Connection.");

		}

	}

	@Override
	public void response(String strresponse, String webservice) {
		// TODO Auto-generated method stub

		Log.i("", strresponse);

		/* {"getReferralBonus":"15%"} */

		try {
			JSONObject jsonObj = new JSONObject(strresponse);

			REFFERAL_BONUS = jsonObj.getString("getReferralBonus");

			((TextView) findViewById(R.id.textPromoCode_4))
					.setText(REFFERAL_BONUS);
			((TextView) findViewById(R.id.textPromoCode_7))
					.setText(REFFERAL_BONUS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * mSharedPrefer.setReferal(strresponse);
		 * txtViewPromoCode.setText(mSharedPrefer.getReferal());
		 */

	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	
	/**
	 * this method is used print key hashes in log cat
	 */

	public void printHashKey() {

		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.yourtaxi", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("TEMP TAG HASH KEY:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
				generateNoteOnSD("FBtoro.txt",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}

	}

	public void generateNoteOnSD(String sFileName, String sBody) {
		try {
			File root = new File(Environment.getExternalStorageDirectory(),
					"CIP_LOGS");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, sFileName);
			FileWriter writer = new FileWriter(gpxfile, true);
			writer.append("\n  \t :" + sBody);
			writer.flush();
			writer.close();
			// Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", "Facebook SDK for Android");
		params.putString("caption",
				"Build great social apps and get more installs.");
		params.putString(
				"description",
				"The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
		params.putString("link", "https://developers.facebook.com/android");
		params.putString("picture",
				"https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				SharingToro.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(SharingToro.this,
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(SharingToro.this,
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(getApplicationContext(),
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							// Generic, ex: network error
							Toast.makeText(SharingToro.this,
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();
	}
	
	
	//alert message

	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(SharingToro.this,
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

}
