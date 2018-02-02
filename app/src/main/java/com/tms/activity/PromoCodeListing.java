package com.tms.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import com.tms.model.GetPromoCodeListing;
import com.tms.utility.CheckInternetConnectivity;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.Utility;
import com.tms.utility.XmlListener;

/**
 * @author arvind.agarwal
 *         this class is used to show promo listing
 */
public class PromoCodeListing extends Activity implements XmlListener {

    /**
     * @author arvind.agarwal
     */

    ListView promoCodeListing;

    ToroSharedPrefrnce mPrefrnce;

    ArrayList<GetPromoCodeListing> arrayListPromoCode;

    GetPromoCodeListing getPromoCodeListingObj;

    ImageView btnBack;
    Button btnAdd1, btnAdd2;

    Bundle bundle;

    Adapter adapter;

    public static Activity promoCodeInstance;
    private Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.promo_code_listing);

        //fetching typeFace
        typeFace = Typeface.createFromAsset(getAssets(), Utility.TYPE_FACE);

        //setting typeFace
        ((TextView) findViewById(R.id.header_text)).setTypeface(typeFace);
        promoCodeInstance = this;
        promoCodeListing = (ListView) findViewById(R.id.promo_code_listing);
        btnBack = (ImageView) findViewById(R.id.btnCancelPromo);
        btnAdd1 = (Button) findViewById(R.id.btn1);
        btnAdd2 = (Button) findViewById(R.id.btn2);
        btnAdd2.setTypeface(typeFace);
        btnAdd2.setTypeface(typeFace);


        mPrefrnce = new ToroSharedPrefrnce(PromoCodeListing.this);

        arrayListPromoCode = new ArrayList<GetPromoCodeListing>();
        bundle = getIntent().getExtras();

        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                finish();

            }
        });
        btnAdd1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // user will navigated to add promo code screen

                startActivity(new Intent(PromoCodeListing.this, com.tms.activity.PromoCode.class));

            }
        });

        btnAdd2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                startActivity(new Intent(PromoCodeListing.this, com.tms.activity.PromoCode.class));

            }
        });

        adapter = new Adapter();

        promoCodeListing.setAdapter(adapter);


        // fetching promocode listing

        if (!CheckInternetConnectivity.checkinternetconnection(this)) {
            showDialog("Alert!", "No Internet Connection.", 0);
        } else {
            new SendXmlAsync(URL.BASE_URL + URL.GETPROMOCODELISTING
                    + mPrefrnce.getUserId(), "", PromoCodeListing.this,
                    PromoCodeListing.this, false).execute();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ontimecab.utility.XmlListener#onResponse(java.lang.String) this
     * method is used to tackle webservice response
     */
    @Override
    public void onResponse(String respose) {
        // TODO Auto-generated method stub

        Log.i("response", respose);
        // {"getPromoCodeListing":[{"id":"1","promoCodeId":"4","promoCode":"o782i","amount":"12%"}],"message":"You have promo codes."}

        if (respose.contains("getPromoCodeListing")) {

            try {
                JSONObject mJsonObject = new JSONObject(respose);

                JSONArray mJsonArray = mJsonObject
                        .getJSONArray("getPromoCodeListing");

                arrayListPromoCode.clear();

                if (mJsonArray.length() != 0) {

					/*
                     * getPromoCodeListingObj = new GetPromoCodeListing();
					 * 
					 * getPromoCodeListingObj.setId("");
					 * 
					 * getPromoCodeListingObj.setPromoCodeId("");
					 * getPromoCodeListingObj.setPromoCode("Promo Code");
					 * getPromoCodeListingObj.setAmount("Discount");
					 * 
					 * arrayListPromoCode.add(getPromoCodeListingObj);
					 */

                    for (int i = 0; i < mJsonArray.length(); i++) {
                        getPromoCodeListingObj = new GetPromoCodeListing();

                        getPromoCodeListingObj.setId(mJsonArray
                                .getJSONObject(i).getString("id"));

                        getPromoCodeListingObj.setPromoCodeId(mJsonArray
                                .getJSONObject(i).getString("promoCodeId"));
                        getPromoCodeListingObj.setPromoCode(mJsonArray
                                .getJSONObject(i).getString("promoCode"));

                        getPromoCodeListingObj.setAmount(mJsonArray
                                .getJSONObject(i).getString("amount"));
						/*
						 * if(mJsonArray.getJSONObject(i).getString("amountType")
						 * .equals("0")){
						 * getPromoCodeListingObj.setAmount("$ "+mJsonArray
						 * .getJSONObject(i).getString("amount")); } else{
						 * getPromoCodeListingObj.setAmount(mJsonArray
						 * .getJSONObject(i).getString("amount"));
						 * 
						 * }
						 */

                        if (mJsonArray.getJSONObject(i)
                                .getString("activePromo").equals("1")) {

                            getPromoCodeListingObj.setChk(true);
                        } else {
                            getPromoCodeListingObj.setChk(false);
                        }

                        arrayListPromoCode.add(getPromoCodeListingObj);

                    }

                    adapter.notifyDataSetChanged();

                } else {

                    if (PromoCodeListing.this != null) {
                        startActivity(new Intent(PromoCodeListing.this,
                                com.tms.activity.PromoCode.class));
                        // showDialog("Message",
                        // mJsonObject.getString("message"), 2);
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (respose.contains("selectPromoCode")) {

            try {
                JSONObject mJsonObject = new JSONObject(respose);

                if (mJsonObject.getString("selectPromoCode").equals("1")) {
                    showDialog("Message", mJsonObject.getString("message"), 1);

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /**
     * @author arvind.agarwal
     *         Adapter for promo code listing
     */

    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayListPromoCode.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arrayListPromoCode.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub

            ViewHolder holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater
                    .from(PromoCodeListing.this);

            if (convertView == null) {

                convertView = inflater.inflate(
                        R.layout.adapter_promocode_listing, null);

                holder.txtPromoCode = (TextView) convertView
                        .findViewById(R.id.txtPromo);
                holder.txtPromoCode.setTypeface(typeFace);
                holder.txtAmount = (TextView) convertView
                        .findViewById(R.id.txtAmount);
                holder.txtAmount.setTypeface(typeFace);
                holder.imgChk = (ImageView) convertView
                        .findViewById(R.id.imgChk);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtAmount.setText(arrayListPromoCode.get(position)
                    .getAmount());
            holder.txtPromoCode.setText(arrayListPromoCode.get(position)
                    .getPromoCode());

            if (arrayListPromoCode.get(position).isChk() == false) {
                holder.imgChk.setImageBitmap(null);

            } else {
                holder.imgChk.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.tick));

            }

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    for (int i = 0; i < arrayListPromoCode.size(); i++) {

                        arrayListPromoCode.get(i).setChk(false);

                    }

                    arrayListPromoCode.get(position).setChk(true);

                    adapter.notifyDataSetChanged();

                    new SendXmlAsync(URL.BASE_URL + URL.SELECTPROMOCODE
                            + mPrefrnce.getUserId() + "&promoCode="
                            + arrayListPromoCode.get(position).getPromoCode(),
                            "", PromoCodeListing.this, PromoCodeListing.this,
                            true).execute();

                    Log.i("xml",
                            URL.BASE_URL
                                    + URL.SELECTPROMOCODE
                                    + mPrefrnce.getUserId()
                                    + mPrefrnce.getUserId()
                                    + "&promoCode="
                                    + arrayListPromoCode.get(position)
                                    .getPromoCode());

                    // mPrefrnce.setReferal(arrayListPromoCode.get(position).getPromoCode());

                }
            });

            return convertView;
        }

    }


    /**
     * @author arvind.agarwal
     *         this class holds view objects of listview
     */
    class ViewHolder {
        TextView txtPromoCode, txtAmount;

        ImageView imgChk;
    }
	
	
	/*
	 * this method is use to show dialog
	 */

    private void showDialog(String title, String message, final int type) {

        final Dialog dialog = new Dialog(PromoCodeListing.this,
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
                if (type == 1) {

                    finish();
                }

            }
        });

        txtTitle.setText(title);
        txtMessage.setText(message);

        dialog.show();

    }

}
