package com.tms.activity;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tms.R;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Activity to create profile .Its the second step of Registration. User can set
 * up First name ,last name and profile pic.
 * 
 * @author arvind.agarwal
 * 
 */
public class CreateProfile extends Activity implements OnClickListener,
		XmlListener ,ImageChooserListener{
	Button btnNext, btnPrevious, btnBack;

	private EditText edtFName, edtLName;
	ImageView imgUser;

	Uri imageUri;

	public static Activity activity;

	MediaPlayer mp = new MediaPlayer();
	ToroSharedPrefrnce mPrefrnce;
	String base64str;
	RelativeLayout photoLayout;
	ProgressBar progressBarImageDownloading;
	
	String base64 = "";
	String filePath = "";
	
	ImageChooserManager imageChooserManager;
	
	Typeface typeFace,typeFace2;
	
	private ImageView selectorFName,selectorLName;

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_profile);
		activity = this;
		typeFace2=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE_FORGOT);
		typeFace=Typeface.createFromAsset(getAssets(),Utility.TYPE_FACE);
		((TextView)findViewById(R.id.imgText)).setTypeface(typeFace2);
		((TextView)findViewById(R.id.txtMessage)).setTypeface(typeFace2);
		((TextView)findViewById(R.id.txtHeader)).setTypeface(typeFace);
		

		initialize();
		edtFName.requestFocus();
		mPrefrnce = new ToroSharedPrefrnce(CreateProfile.this);
	}

	/**
	 * initialize views
	 */
	public void initialize() {
		
		selectorFName=(ImageView)findViewById(R.id.selector_f_name);
		selectorLName=(ImageView)findViewById(R.id.selector_l_name);
		
		photoLayout = (RelativeLayout) findViewById(R.id.layoutProfilePic);
		photoLayout.setOnClickListener(this);
		
		btnNext = (Button) findViewById(R.id.btnNextCreateProfile);
		btnNext.setOnClickListener(this);
		btnPrevious = (Button) findViewById(R.id.btnBackCreateProfile);
		btnPrevious.setOnClickListener(this);
		
		btnBack = (Button) findViewById(R.id.btnBack_cp);
		btnBack.setOnClickListener(this);
		
		imgUser = (ImageView) findViewById(R.id.imgUser);
		imgUser.setOnClickListener(this);
		progressBarImageDownloading = (ProgressBar) findViewById(R.id.progressBar1);
		edtFName = (EditText) findViewById(R.id.edtFname);
		edtFName.setTypeface(typeFace);
		edtLName = (EditText) findViewById(R.id.edtLName);
		edtLName.setTypeface(typeFace);
		edtLName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					selectorLName.setBackgroundResource(R.drawable.background_text_field_selected);
				} else {
					selectorLName.setBackgroundResource(R.drawable.background_text_field);
				}
			}
		});

		edtFName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					selectorFName.setBackgroundResource(R.drawable.background_text_field_selected);

				} else {
					selectorFName.setBackgroundResource(R.drawable.background_text_field);
				}
			}
		});

		edtLName.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				
				//saving first and last name in shared preferences

				mPrefrnce.setFirstName(edtFName.getText().toString().trim());
				mPrefrnce.setLastname(edtLName.getText().toString().trim());
				/*
				 * String updateInfoXml =
				 * "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><updateBasicInfo><userId><![CDATA["
				 * + mPrefrnce.getUser() + "]]></userId><firstName><![CDATA[" +
				 * mPrefrnce.getFirstName() +
				 * "]]></firstName><lastName><![CDATA[" +
				 * mPrefrnce.getLastname() + "]]></lastName><imgBytes><![CDATA["
				 * + base64str + "]]></imgBytes></updateBasicInfo>"; new
				 * SendXmlAsync(URL.BASE_URL + URL.UPDATE_INFO, updateInfoXml,
				 * CreateProfile.this, CreateProfile.this, true).execute();
				 */
				if (edtFName.getText().toString().trim().equalsIgnoreCase("")) {
					edtFName.setError("Enter first name");
					edtFName.requestFocus();
				} else if (edtLName.getText().toString().trim().equalsIgnoreCase("")) {
					edtLName.requestFocus();
					edtLName.setError("Enter last name");
				} else {
					edtFName.clearFocus();
					edtLName.clearFocus();
					

					
					// if payment gateway is on than it will take to Creditcard link screen else take to Term  and conditions screen
					if(Utility.isPaymentGatewayOn){
					startActivity(new Intent(CreateProfile.this,
							CreditCardLink.class));
					
					}else{
						startActivity(new Intent(CreateProfile.this,
								TermsCondition.class));
						
						
					}
				}

				return false;
			}
		});
	}
	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//===================================================
		
		//control goes in this under written condition when an image is picked from gallery or camera
		if (resultCode == RESULT_OK
				&& (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
			imageChooserManager.submit(requestCode, data);

		}
		
		
		
		//===================================================
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btnBack_cp:
			showDialog();
			break;
	
		case R.id.btnBackCreateProfile:
			showDialog();
			break;
		case R.id.btnNextCreateProfile:
			mPrefrnce.setFirstName(edtFName.getText().toString().trim());
			mPrefrnce.setLastname(edtLName.getText().toString().trim());
			/*
			 * String updateInfoXml =
			 * "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><updateBasicInfo><userId><![CDATA["
			 * + mPrefrnce.getUser() + "]]></userId><firstName><![CDATA[" +
			 * mPrefrnce.getFirstName() + "]]></firstName><lastName><![CDATA[" +
			 * mPrefrnce.getLastname() + "]]></lastName><imgBytes><![CDATA[" +
			 * base64str + "]]></imgBytes></updateBasicInfo>"; new
			 * SendXmlAsync(URL.BASE_URL + URL.UPDATE_INFO, updateInfoXml,
			 * CreateProfile.this, CreateProfile.this, true).execute();
			 */
			if (edtFName.getText().toString().trim().equalsIgnoreCase("")) {
				showDialogg("Alert !", "Enter First name");

				edtFName.requestFocus();
			} else if (edtLName.getText().toString().trim().equalsIgnoreCase("")) {
				showDialogg("Alert !", "Enter Last name");
				edtLName.requestFocus();
			} else {
				
				// if payment gateway is on than it will take to Creditcard link screen else take to Term  and conditions screen
				if(Utility.isPaymentGatewayOn){
				startActivity(new Intent(CreateProfile.this,
						CreditCardLink.class));
				
				}else{
					startActivity(new Intent(CreateProfile.this,
							TermsCondition.class));
					
					
				}
			}

			break;
		case R.id.layoutProfilePic:
		
			break;

		case R.id.imgUser:





			final Dialog mDialogg = new Dialog(CreateProfile.this,
					style.Theme_Translucent_NoTitleBar);
			mDialogg.setContentView(R.layout.dialog_photo);
			mDialogg.setTitle("Take photo from");
			Button btnCAmm = (Button) mDialogg.findViewById(R.id.btnCamera);
			btnCAmm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					
					
					imageChooserManager = new ImageChooserManager(
							CreateProfile.this, ChooserType.REQUEST_CAPTURE_PICTURE);
					imageChooserManager.setImageChooserListener(CreateProfile.this);
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
							CreateProfile.this, ChooserType.REQUEST_PICK_PICTURE);
					imageChooserManager.setImageChooserListener(CreateProfile.this);
			
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
		default:
			break;
		}
	}



	@Override
	public void onResponse(String respose) {
		// TODO Auto-generated method stub
		Log.i("Update Info Resp", respose);
		try {
			JSONObject mJsonObject = new JSONObject(respose);
			String resp = mJsonObject.getString("updateBasicInfo");
			if (resp.equalsIgnoreCase("-1")) {
				Toast.makeText(CreateProfile.this, "In-valid User",
						Toast.LENGTH_LONG).show();
			} else if (resp.equalsIgnoreCase("-2")) {
				Toast.makeText(CreateProfile.this, "Server Error",
						Toast.LENGTH_LONG).show();
			} else if (resp.equalsIgnoreCase("1")) {
				Toast.makeText(CreateProfile.this, "Successfully updated",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(CreateProfile.this, Login.class));
				finish();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * dialog show to exit registration
	 */
	private void showDialog() {

		final Dialog dialog = new Dialog(CreateProfile.this,
				style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_information_gonr);

		Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
		Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
		btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				com.tms.activity.Register.activity.finish();
				CreateProfile.this.finish();
				PreferenceManager
						.getDefaultSharedPreferences(CreateProfile.this).edit()
						.clear().commit();
				startActivity(new Intent(CreateProfile.this, Login.class));

			}
		});

		btnNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});

		dialog.show();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		showDialog();
	}

	/**
	 * show dialog on different errors
	 * 
	 * @param title
	 *            title of dialog
	 * @param message
	 *            message of dialog
	 */
	private void showDialogg(String title, String message) {

		final Dialog dialog = new Dialog(CreateProfile.this,
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

	

	


		

	

	@Override
	public void onError(String arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * this interface is called when an image is selected or captured
	 */

	@Override
	public void onImageChosen(final ChosenImage arg0) {
		// TODO Auto-generated method stub
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				int rotation = 0;
				
				//checking orientation of selected or captured image

				try {
					ExifInterface exifInterface = new ExifInterface(
							arg0.getFilePathOriginal());
					int exifRotation = exifInterface.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_UNDEFINED);
					
					// changing the orientation of image if not exact

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
				
				//creating new bitmap
				Bitmap mBitmap = decodeFileNew(new File(
						arg0.getFilePathOriginal()));
				Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0,
						mBitmap.getWidth(), mBitmap.getHeight(), matrix,
						true);

			
				// setting selected image on ImageView
				Picasso.with(CreateProfile.this).load(new File(arg0.getFileThumbnail())).resize(imgUser.getWidth(), imgUser.getHeight()).into(imgUser);
				
			/*	imgUser.setImageBitmap(BitmapFactory.decodeFile(arg0
						.getFileThumbnailSmall()));*/
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
				byte[] byteArray = stream.toByteArray();
				base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

				filePath = base64;
				
				mPrefrnce.setPhoto(filePath);
				
				filePath = "";
				
				// recycling bitmap
				bitmap.recycle();
				mBitmap.recycle();
			
				
			}

		});
		
	}
	
	
	/**
	 * 
	 * @param f
	 * @return
	 * 
	 * this method returns bitmap from provided file (path)
	 */
	private Bitmap decodeFileNew(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 50;

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
}
