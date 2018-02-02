package com.tms.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.CircleTransform;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;

/**
 * Activity to edit user profile
 * 
 * @author arvind.agarwal
 * 
 */
public class EditProfile extends Activity implements OnClickListener,
		XmlListener, ImageChooserListener {

	private ImageChooserManager imageChooserManager;

	ImageButton btnCancel, btnSave;
	EditText edtFname, edtLname, edtEmail, edtPhone, edtCountryCode;
	ImageView imgMsg, imgUserPic;
	ToroSharedPrefrnce mPrefrnce;
	Button btnPassword;
	Dialog mDialogPass;
	Uri imageUri;
	private Bitmap bitmap;
	public static Activity activity;

	MediaPlayer mp = new MediaPlayer();

	String base64str;

	RelativeLayout mPhotoLayout;
	boolean isPicEdit = false;
	String base64 = "";
	private EditText edtNewPass;
	String filePath = "";
	float Rotation;
	RelativeLayout rel_1, rel_2, rel_3;
	Typeface typeFace, typeFaceHelveticaLight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);
		typeFaceHelveticaLight = Typeface.createFromAsset(getAssets(),
				Utility.TYPE_FACE_HELVATICA_NEUE_LIGHT);
		((TextView) findViewById(R.id.headerText)).setTypeface(typeFace);

		mPrefrnce = new ToroSharedPrefrnce(EditProfile.this);
		rel_1 = (RelativeLayout) findViewById(R.id.rel_1);
		rel_2 = (RelativeLayout) findViewById(R.id.rel_2);
		rel_3 = (RelativeLayout) findViewById(R.id.rel_3);
		initialize();
		GetCountryZipCode();
	}

	/**
	 * initializing views
	 */
	public void initialize() {
		imgUserPic = (ImageView) findViewById(R.id.imgUserPic);
		imgUserPic.setOnClickListener(this);
		mPhotoLayout = (RelativeLayout) findViewById(R.id.layoutuserPicEdit);
		mPhotoLayout.setOnClickListener(this);

		btnCancel = (ImageButton) findViewById(R.id.btnCancelEdit);
		btnCancel.setOnClickListener(this);
		btnSave = (ImageButton) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		edtEmail = (EditText) findViewById(R.id.edtUserEmail);
		edtEmail.setTypeface(typeFaceHelveticaLight);
		edtFname = (EditText) findViewById(R.id.edtFNameEdit);
		edtFname.setTypeface(typeFaceHelveticaLight);
		edtLname = (EditText) findViewById(R.id.edtLNameEdit);
		edtLname.setTypeface(typeFaceHelveticaLight);
		edtCountryCode = (EditText) findViewById(R.id.edtCountryCode);
		edtCountryCode.setTypeface(typeFaceHelveticaLight);
		edtFname.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {

					edtFname.setSelection(edtFname.getText().length());
					// edtFname.setBackgroundResource(R.drawable.text_field_slctd);
				} else {
					// edtFname.setBackgroundResource(R.drawable.text_field);
				}
			}
		});
		edtLname.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					edtLname.setSelection(edtLname.getText().length());
					// edtLname.setBackgroundResource(R.drawable.text_field_slctd);
				} else {
					// edtLname.setBackgroundResource(R.drawable.text_field);
				}
			}
		});
		btnPassword = (Button) findViewById(R.id.edtUserPassword);
		btnPassword.setTypeface(typeFaceHelveticaLight);

		StringBuffer changePassword = new StringBuffer();
		for (int j = 0; j < mPrefrnce.getPassword().length(); j++) {
			changePassword.append("*");
		}

		btnPassword.setText(changePassword.toString());

		btnPassword.setOnClickListener(this);

		edtPhone = (EditText) findViewById(R.id.edtUserPhone);
		edtPhone.setTypeface(typeFaceHelveticaLight);
		imgMsg = (ImageView) findViewById(R.id.imgUserProfileMSgEdit);
		imgMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog photoDialog = new Dialog(EditProfile.this,
						style.Theme_Translucent_NoTitleBar);
				photoDialog.setContentView(R.layout.dialog_photo);
				Button btnCAm = (Button) photoDialog
						.findViewById(R.id.btnCamera);
				btnCAm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						imageChooserManager = new ImageChooserManager(
								EditProfile.this,
								ChooserType.REQUEST_CAPTURE_PICTURE);
						imageChooserManager
								.setImageChooserListener(EditProfile.this);
						try {
							imageChooserManager.choose();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						photoDialog.dismiss();
					}
				});
				Button btnGallery = (Button) photoDialog
						.findViewById(R.id.btngallery);
				btnGallery.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						imageChooserManager = new ImageChooserManager(
								EditProfile.this,
								ChooserType.REQUEST_PICK_PICTURE);
						imageChooserManager
								.setImageChooserListener(EditProfile.this);

						try {
							imageChooserManager.choose();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						photoDialog.dismiss();
					}
				});
				photoDialog.show();

			}
		});

		edtEmail.setText(mPrefrnce.getEmail());
		edtFname.setText(mPrefrnce.getFirstName());
		edtLname.setText(mPrefrnce.getLastname());
		edtPhone.setText(mPrefrnce.getPhone());

		edtCountryCode.setText(mPrefrnce.getCountryCode());

		/*
		 * ImageUtils.getInstance(EditProfile.this).setImageUrlToView(
		 * mPrefrnce.getImageUrl(), imgUserPic, null, 0, false, true,
		 * mPhotoLayout.getHeight(), mPhotoLayout.getWidth(), false);
		 */

		if (!mPrefrnce.getImageUrl().equals("")) {
			Picasso.with(EditProfile.this).load(mPrefrnce.getImageUrl())
					.resize(200, 200).transform(new CircleTransform())
					.into(imgUserPic);

		}

		imgMsg.bringToFront();
		edtEmail.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					edtEmail.setSelection(edtEmail.getText().length());
					// rel_1.setBackgroundResource(R.drawable.text_field_slctd);
				} else {
					// rel_1.setBackgroundResource(R.drawable.text_field);
				}
			}
		});

		edtCountryCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				if (s.length() == 0) {
					edtCountryCode.setText("+");
				}

				if (!edtCountryCode.getText().toString().contains("+")) {
					edtCountryCode.setText("+"
							+ edtCountryCode.getText().toString());

				}

				if (!edtCountryCode.getText().toString().substring(0, 1)
						.equals("+")) {
					edtCountryCode.setText(edtCountryCode.getText().toString()
							.substring(1));

				}

				if (s.length() == 1
						&& edtCountryCode.getText().toString().equals("+")) {
					edtCountryCode.setSelection(1);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		edtCountryCode.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (hasFocus) {

					edtCountryCode.setSelection(edtCountryCode.getText()
							.length());

					// rel_2.setBackgroundResource(R.drawable.text_field_slctd);

				} else {

					// rel_2.setBackgroundResource(R.drawable.text_field);
				}

			}
		});
		edtPhone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					edtPhone.setSelection(edtPhone.getText().length());
					// rel_2.setBackgroundResource(R.drawable.text_field_slctd);

				} else {

					// rel_2.setBackgroundResource(R.drawable.text_field);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String) this
	 * method is useful for tackling server responses
	 */
	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub

		// changecard response
		if (respose.contains("changePassword")) {
			try {
				JSONObject mJsonObject = new JSONObject(respose);
				String resp = mJsonObject.getString("changePassword");
				String message = mJsonObject.getString("message");
				if (resp.equalsIgnoreCase("-1")) {
					showDialog("Alert !", message);
				} else if (resp.equalsIgnoreCase("-2")) {
					showDialog("Alert !", message);
				} else if (resp.equalsIgnoreCase("-3")) {
					showDialog("Alert !", message);
				} else if (resp.equalsIgnoreCase("-4")) {
					showDialog("Alert !", message);
				} else if (resp.equalsIgnoreCase("1")) {
					mDialogPass.dismiss();
					Toast.makeText(EditProfile.this, message, Toast.LENGTH_LONG)
							.show();

					StringBuffer changePassword = new StringBuffer();
					for (int j = 0; j < edtNewPass.getText().length(); j++) {
						changePassword.append("*");
					}

					mPrefrnce.setPassword(edtNewPass.getText().toString());
					btnPassword.setText(changePassword.toString());

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(edtFname,
							InputMethodManager.HIDE_IMPLICIT_ONLY);
					imm.showSoftInput(edtLname,
							InputMethodManager.HIDE_IMPLICIT_ONLY);
					imm.showSoftInput(edtEmail,
							InputMethodManager.HIDE_IMPLICIT_ONLY);
					imm.showSoftInput(edtPhone,
							InputMethodManager.HIDE_IMPLICIT_ONLY);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				JSONObject mJsonObject = new JSONObject(respose);
				String mresp = mJsonObject.getString("editProfile");
				String message = mJsonObject.getString("message");
				if (mresp.equalsIgnoreCase("-1")) {
					showDialog("Alert !", message);
				} else if (mresp.equalsIgnoreCase("-2")) {
					showDialog("Alert !", message);
				} else if (mresp.equalsIgnoreCase("-3")) {
					showDialog("Alert !", message);
				} else if (mresp.equalsIgnoreCase("-4")) {
					showDialog("Alert !", message);

				} else if (mresp.equalsIgnoreCase("1")) {

					if (mJsonObject.getString("phone").equals("1")) {

						mPrefrnce.setPhone(edtPhone.getText().toString());
						mPrefrnce.setCountryCode(edtCountryCode.getText()
								.toString());

						if (!base64.equals("")) {
							base64 = "";
						}

						if (base64str != null) {
							base64str = "";
						}
						if (bitmap != null) {
							bitmap.recycle();
							bitmap = null;
						}
						startActivity(new Intent(EditProfile.this,
								VarifyMobile.class).putExtra("via",
								"EditProfile"));
						com.tms.activity.MyProfile.activity.finish();
						//Home.activity.finish();

					}

					finish();

					Toast.makeText(EditProfile.this, message, Toast.LENGTH_LONG)
							.show();

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layoutuserPicEdit:

			// showing dialog for selecting an option between gallery and camera
			final Dialog mDialog = new Dialog(EditProfile.this,
					style.Theme_Translucent_NoTitleBar);
			mDialog.setContentView(R.layout.dialog_photo);
			mDialog.setTitle("Take photo from");
			Button btnCAm = (Button) mDialog.findViewById(R.id.btnCamera);
			btnCAm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// calling image camera option
					imageChooserManager = new ImageChooserManager(
							EditProfile.this,
							ChooserType.REQUEST_CAPTURE_PICTURE);
					imageChooserManager
							.setImageChooserListener(EditProfile.this);
					try {
						imageChooserManager.choose();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mDialog.dismiss();

				}
			});
			Button btnGallery = (Button) mDialog.findViewById(R.id.btngallery);
			btnGallery.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					//calling gallery
					imageChooserManager = new ImageChooserManager(
							EditProfile.this, ChooserType.REQUEST_PICK_PICTURE);
					imageChooserManager
							.setImageChooserListener(EditProfile.this);

					try {
						imageChooserManager.choose();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mDialog.dismiss();
				}
			});
			mDialog.show();
			break;
		case R.id.imgUserPic:
			final Dialog mDialogg = new Dialog(EditProfile.this,
					style.Theme_Translucent_NoTitleBar);
			mDialogg.setContentView(R.layout.dialog_photo);
			mDialogg.setTitle("Take photo from");
			Button btnCAmm = (Button) mDialogg.findViewById(R.id.btnCamera);
			btnCAmm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					imageChooserManager = new ImageChooserManager(
							EditProfile.this,
							ChooserType.REQUEST_CAPTURE_PICTURE);
					imageChooserManager
							.setImageChooserListener(EditProfile.this);
					try {
						imageChooserManager.choose();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mDialogg.dismiss();
				}
			});
			Button btnGalleryy = (Button) mDialogg
					.findViewById(R.id.btngallery);
			btnGalleryy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					imageChooserManager = new ImageChooserManager(
							EditProfile.this, ChooserType.REQUEST_PICK_PICTURE);
					imageChooserManager
							.setImageChooserListener(EditProfile.this);

					try {
						imageChooserManager.choose();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mDialogg.dismiss();
				}
			});
			mDialogg.show();
			break;
		case R.id.btnCancelEdit:
			finish();
			break;
		case R.id.btnSave:
			
			// saving profile
			if (CheckInternetConnectivity
					.checkinternetconnection(EditProfile.this)) {
				if (edtFname.getText().toString().trim().equalsIgnoreCase("")) {
					showDialog("Alert !", "Enter First name");
					edtFname.requestFocus();
				} else if (edtLname.getText().toString().trim()
						.equalsIgnoreCase("")) {
					showDialog("Alert !", "Enter Last name");
					edtLname.requestFocus();

				} else if (edtEmail.getText().toString().trim()
						.equalsIgnoreCase("")) {
					edtEmail.requestFocus();
					showDialog("Alert !", "Enter Email address");
				} else if (edtPhone.getText().toString().trim()
						.equalsIgnoreCase("")) {
					edtPhone.requestFocus();
					showDialog("Alert !", "Enter Phone number");
				}
				/*else if (edtPhone.getText().toString().trim().length()<10
                        ) {
					// edtPhone.setError("Enter Phone Number");
					showDialog("Alert !", "Please enter valid Phone Number");
					edtPhone.requestFocus();
				}*/

				else if (edtCountryCode.getText().length() == 1) {
					edtCountryCode.setError("Enter valid country code");
				} else if (!isEmailValid(edtEmail.getText().toString().trim())) {
					edtEmail.requestFocus();
					showDialog("Alert !", "Enter valid Email Id");
				}

				else {
					String pic = "";
					if (!filePath.equals("")) {
						pic = mPrefrnce.getPhoto();
					} else {
						pic = mPrefrnce.getimageName();
					}
					String xmlEditProfile = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><editProfile><userId><![CDATA["
							+ mPrefrnce.getUserId()
							+ "]]></userId><firstName><![CDATA["
							+ edtFname.getText().toString().trim()
							+ "]]></firstName><lastName><![CDATA["
							+ edtLname.getText().toString()
							+ "]]></lastName><emailAddress><![CDATA["
							+ edtEmail.getText().toString().trim()
							+ "]]></emailAddress><phone><![CDATA["
							+ edtPhone.getText().toString().trim()
							+ "]]></phone><userImage><![CDATA["
							+ pic
							+ "]]></userImage><CountryCode><![CDATA["
							+ (edtCountryCode.getText().toString())
							+ "]]></CountryCode></editProfile>";
					new SendXmlAsync(URL.BASE_URL + URL.EDIT_PROFILE,
							xmlEditProfile, EditProfile.this, EditProfile.this,
							true).execute();

				}
			} else {


				Toast.makeText(EditProfile.this, "No Internet",
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.edtUserPassword:
			
			// showing change user password screen
			mDialogPass = new Dialog(EditProfile.this,
					style.Theme_Black_NoTitleBar);
			mDialogPass.setContentView(R.layout.dialog_change_password);
			mDialogPass
					.getWindow()
					.setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
									| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

			final ImageView selectorNewPassword,
			selectorOldPassword,
			selectorConfirmPassword;
			selectorOldPassword = (ImageView) mDialogPass
					.findViewById(R.id.selector_old_password);
			selectorNewPassword = (ImageView) mDialogPass
					.findViewById(R.id.selector_new_password);
			selectorConfirmPassword = (ImageView) mDialogPass
					.findViewById(R.id.selector_confirm_password);
			mDialogPass.show();
			final EditText edtOld = (EditText) mDialogPass
					.findViewById(R.id.edtOldPass);
			edtOld.setTypeface(typeFace);
			edtNewPass = (EditText) mDialogPass.findViewById(R.id.edtNewPass);
			edtNewPass.setTypeface(typeFace);
			final EditText edtConfirmPass = (EditText) mDialogPass
					.findViewById(R.id.edtConfirmPass);
			edtConfirmPass.setTypeface(typeFace);
			Button btnChangepass = (Button) mDialogPass
					.findViewById(R.id.btnChangePass);

			edtOld.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus) {
						selectorOldPassword
								.setBackgroundResource(R.drawable.background_text_field_selected);

					} else {
						selectorOldPassword
								.setBackgroundResource(R.drawable.background_text_field);

					}
				}
			});
			edtNewPass.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus) {
						selectorNewPassword
								.setBackgroundResource(R.drawable.background_text_field_selected);

					} else {
						selectorNewPassword
								.setBackgroundResource(R.drawable.background_text_field);

					}
				}
			});
			edtConfirmPass
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							// TODO Auto-generated method stub
							if (hasFocus) {
								selectorConfirmPassword
										.setBackgroundResource(R.drawable.background_text_field_selected);

							} else {
								selectorConfirmPassword
										.setBackgroundResource(R.drawable.background_text_field);

							}
						}
					});

			btnChangepass.setTypeface(typeFace);
			btnChangepass.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					
					//web service hit to change password
					if (edtOld.getText().toString().trim().equalsIgnoreCase("")) {
						edtOld.setError("Enter Old password");
						edtOld.requestFocus();
					} else if (edtNewPass.getText().toString().trim()
							.equalsIgnoreCase("")) {
						edtNewPass.setError("Enter new password");
						edtNewPass.requestFocus();
					} else if (edtConfirmPass.getText().toString().trim()
							.equalsIgnoreCase("")) {
						edtConfirmPass.setError("Confirm your password");
						edtConfirmPass.requestFocus();
					} else if (edtNewPass.getText().length() < 5
							|| edtNewPass.getText().length() > 20) {
						// edtPassword
						// .setError("Enter password between 5 to 8 characters");

						edtNewPass
								.setError("Enter password between 5 to 20 characters");
						edtNewPass.requestFocus();

					}

					else {
						if (!edtNewPass
								.getText()
								.toString()
								.trim()
								.equals(edtConfirmPass.getText().toString()
										.trim())) {
							// edtNewPass.setText("");
							edtConfirmPass.setText("");
							edtConfirmPass.setError("Password doesn't match");

						} else {

							if (CheckInternetConnectivity
									.checkinternetconnection(EditProfile.this)) {
								String xmlPassword = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><changePassword><userId><![CDATA["
										+ mPrefrnce.getUserId()
										+ "]]></userId><oldPassword><![CDATA["
										+ edtOld.getText().toString().trim()
										+ "]]></oldPassword><newPassword><![CDATA["
										+ edtNewPass.getText().toString()
												.trim()
										+ "]]></newPassword></changePassword>";
								new SendXmlAsync(URL.BASE_URL
										+ URL.CHANGE_PASSWORD, xmlPassword,
										EditProfile.this, EditProfile.this,
										true).execute();
							} else {
								Toast.makeText(EditProfile.this, "No Internet",
										Toast.LENGTH_LONG).show();
							}
						}
					}

				}
			});
			ImageView btnCross = (ImageView) mDialogPass.findViewById(R.id.btnCross);
			btnCross.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mDialogPass.dismiss();
				}
			});

			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		// ===================================================
		if (resultCode == RESULT_OK
				&& (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
			imageChooserManager.submit(requestCode, data);

		}

		// =================================================
	}

	/**
	 * Dialog to show alerts
	 * 
	 * @param title
	 *            setting title of dialog
	 * @param message
	 *            setting message
	 */

	private void showDialog(String title, String message) {

		final Dialog dialog = new Dialog(EditProfile.this,
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

	public String GetCountryZipCode() {

		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		mPrefrnce.setCounrty(CountryID);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;

	}

	@Override
	public void onError(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onImageChosen(final ChosenImage arg0) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				int rotation = 0;

				try {
					ExifInterface exifInterface = new ExifInterface(
							arg0.getFilePathOriginal());
					int exifRotation = exifInterface.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_UNDEFINED);

					if (exifRotation != ExifInterface.ORIENTATION_UNDEFINED) {
						switch (exifRotation) {
						case ExifInterface.ORIENTATION_ROTATE_180:
							rotation = 180;
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							rotation = 270;
							break;
						case ExifInterface.ORIENTATION_ROTATE_90:
							rotation = 90;
							break;
						}
					}
				} catch (IOException e) {
				}
				Matrix matrix = null;
				if (rotation != 0) {
					matrix = new Matrix();
					matrix.setRotate(rotation);

				}
				Bitmap mBitmap = decodeFileNew(new File(
						arg0.getFilePathOriginal()));
				Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0,
						mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

				Picasso.with(EditProfile.this)
						.load(new File(arg0.getFilePathOriginal()))
						.transform(new CircleTransform()).into(imgUserPic);

				/*
				 * imgUserPic.setImageBitmap(BitmapFactory.decodeFile(arg0
				 * .getFileThumbnailSmall()));
				 */

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

				filePath = base64;

				mPrefrnce.setPhoto(filePath);

				bitmap.recycle();
				mBitmap.recycle();

			}

		});

	}
	
	
	
	/*
	 * this method fetches bitmap from file 
	 */

	private Bitmap decodeFileNew(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 100;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	
	/*
	 * this method is useful to validate email id
	 */
	public boolean isEmailValid(String email) {

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			return true;
		} else {

			Log.i(".....Email", "Not valid");
			// Toast.makeText(RegistrationScreen.this, "enter valid e-mail id",
			// Toast.LENGTH_SHORT).show();
			return false;

		}
	}

}
