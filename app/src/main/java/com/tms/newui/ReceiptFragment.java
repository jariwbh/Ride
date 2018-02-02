package com.tms.newui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tms.R;
import com.tms.activity.Receipt;
import com.tms.utility.CircleTransform;
import com.tms.utility.SendXmlAsync;
import com.tms.utility.ToroSharedPrefrnce;
import com.tms.utility.URL;
import com.tms.utility.XmlListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by viren on 7/1/18.
 */

public class ReceiptFragment extends Fragment implements XmlListener {

    TextView txtRating, txtUserName, txtPhone, txtTaxiModel, txtTaxiNo,
            txtDate, txtFair, imgReceipt_text2, txtRatingtType;
    ImageView driverImage, carIcon;
    RatingBar mRatingBar;
    Button btnSubmit;
    ToroSharedPrefrnce mPrefrnce;
    EditText edtComments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_your_reciept, container, false);
        initialize(view);

        return view;
    }

    public void initialize(View view) {
        txtRatingtType = (TextView) view.findViewById(R.id.txtRatingtType);

        txtRating = (TextView) view.findViewById(R.id.txtRating);
        txtUserName = (TextView) view.findViewById(R.id.txtDriverName);
        txtPhone = (TextView) view.findViewById(R.id.txtDriverPhone);
        txtRating = (TextView) view.findViewById(R.id.txtRating);

        txtTaxiModel = (TextView) view.findViewById(R.id.txtCompanyName);
        txtTaxiNo = (TextView) view.findViewById(R.id.txtTaxiNo);
        driverImage = (ImageView) view.findViewById(R.id.imgDriverPic);

        txtDate = (TextView) view.findViewById(R.id.txtDate);
        txtFair = (TextView) view.findViewById(R.id.txtFare);
        carIcon = (ImageView) view.findViewById(R.id.imgCarIcon);
        mRatingBar = (RatingBar) view.findViewById(R.id.ratingBarDriver);
        mRatingBar.setStepSize(1);
        mRatingBar.setRating(3.0f);
        txtRatingtType.setText("OK");

        edtComments = (EditText) view.findViewById(R.id.edtComments);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        mRatingBar
                .setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(RatingBar ratingBar,
                                                float rating, boolean fromUser) {
                        // TODO Auto-generated method stub

                        if (rating == 1.0f) {

                            txtRatingtType.setText("POOR");
                        } else if (rating == 2.0f) {

                            txtRatingtType.setText("FAIR");
                        } else if (rating == 3.0f) {

                            txtRatingtType.setText("OK");
                        } else if (rating == 4.0f) {

                            txtRatingtType.setText("GOOD");
                        } else if (rating == 5.0f) {

                            txtRatingtType.setText("GREAT");
                        }

                        edtComments.setEnabled(true);
                        btnSubmit.setEnabled(true);

                    }
                });
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                float valueRating = mRatingBar.getRating();
                if (valueRating < 1.0f) {
                    showDialogForRating("Alert !", getString(R.string.rating_alert));
                } else {
                    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><driverRating><bookingId><![CDATA["
                            + getArguments().getString("bookingId")
                            + "]]></bookingId><driverId><![CDATA["
                            + getArguments().getString("id")
                            + "]]></driverId><Id><![CDATA["
                            + getArguments().getString("userid")
                            + "]]></Id><rating><![CDATA["
                            + mRatingBar.getRating()
                            + "]]></rating><comment><![CDATA["
                            + ""
                            + "]]></comment></driverRating>";
                    new SendXmlAsync(URL.BASE_URL + URL.RATING_DRIVER, xml,
                            ReceiptFragment.this, getActivity(), true).execute();
                }


            }
        });

       /* if (getArguments().getString("payVia").equals("0")) {

            imgReceipt_text2.setText(getResources().getString(R.string.payvia0));

        } else {

            imgReceipt_text2.setText(getResources().getString(R.string.payvia1));

        }*/

        txtPhone.setText(getArguments().getString("driverPhone"));
        txtRating.setText(getArguments().getString("rating"));
        txtTaxiModel.setText(getArguments().getString("taxiModel"));
        txtTaxiNo.setText(getArguments().getString("taxiNumber"));
        txtUserName.setText(getArguments().getString("driverName"));
        txtDate.setText(getArguments().getString("completedDate"));
        if (!getArguments().getString("tripAmount")
                .equalsIgnoreCase("null")) {
            txtFair.setText("$ " + getArguments().getString("tripAmount"));
        } else {
            txtFair.setText("$ " + "N/A");
        }
        if (getArguments().getString("taxiType").equalsIgnoreCase("1")) {
            carIcon.setBackgroundResource(R.drawable.icon_blackcar_grey);
        } else if (getArguments().getString("taxiType")
                .equalsIgnoreCase("2")) {

            carIcon.setBackgroundResource(R.drawable.icon_mini_grey);
        } else if (getArguments().getString("taxiType")
                .equalsIgnoreCase("3")) {
            carIcon.setBackgroundResource(R.drawable.icon_mini_grey);
        } else if (getArguments().getString("taxiType")
                .equalsIgnoreCase("4")) {
            carIcon.setBackgroundResource(R.drawable.icon_suvcar_grey);
        }

        if (!getArguments().getString("driverImage").equalsIgnoreCase("")) {
            Picasso.with(getActivity())
                    .load(getArguments().getString("driverImage"))
                    .resize(90, 90).transform(new CircleTransform()).into(driverImage);
        }


    }

    @Override
    public void onResponse(String respose) {
        try {
            JSONObject mJsonObject = new JSONObject(respose);
            ((MainActivity4) getActivity()).clearMap();
            ((MainActivity4) getActivity()).showFragment(new WhereToFragment());
            Toast.makeText(getActivity(), mJsonObject.getString("message"),
                    Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialog(String title, String message) {

        final Dialog dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_warning);
        TextView txtTitle = (TextView) dialog
                .findViewById(R.id.dialog_title_text);
        TextView txtMessage = (TextView) dialog
                .findViewById(R.id.dialog_message_text);

        txtTitle.setText(title);
        txtMessage.setText(message);

        Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

        btnYes.setVisibility(View.GONE);
        btnNo.setVisibility(View.GONE);
        Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                ((MainActivity4) getActivity()).showFragment(new WhereToFragment());
            }
        });

        dialog.show();

    }


    // dialog for rating

    private void showDialogForRating(String title, String message) {

        final Dialog dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_warning);
        TextView txtTitle = (TextView) dialog
                .findViewById(R.id.dialog_title_text);
        TextView txtMessage = (TextView) dialog
                .findViewById(R.id.dialog_message_text);

        txtTitle.setText(title);
        txtMessage.setText(message);

        Button btnYes = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        Button btnNo = (Button) dialog.findViewById(R.id.dialog_cancel_btn);

        btnYes.setVisibility(View.GONE);
        btnNo.setVisibility(View.GONE);
        Button btnOk = (Button) dialog.findViewById(R.id.btnDone);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}
