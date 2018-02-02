package com.tms.newui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.tms.R;
import com.tms.activity.Reservation;
import com.tms.utility.XmlListener;

import org.json.JSONObject;

/**
 * Created by viren on 26/12/17.
 *
 */

public class BookFragment extends Fragment {

    private ImageView tringle1, tringle2, tringle3;
    private ImageView car1, car2, car3;
    private TextView fare, time,person;
    private Button btnRequest;
    private TextView time1,time2,time3;
    private int taxyType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_view, container, false);

        ImageView backPress = (ImageView) view.findViewById(R.id.backBtn);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity4) getActivity()).backstackFragment();
            }
        });
        TextView backText = (TextView) view.findViewById(R.id.backText);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity4) getActivity()).backstackFragment();
            }
        });
        ImageView advanceBooking = (ImageView) view.findViewById(R.id.btnAdvancebook);
        advanceBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Reservation.class));
            }
        });

        tringle1 = (ImageView) view.findViewById(R.id.imageView17);
        tringle2 = (ImageView) view.findViewById(R.id.mini_i1);
        tringle3 = (ImageView) view.findViewById(R.id.full_i1);

        car1 = (ImageView) view.findViewById(R.id.imageView16);
        car2 = (ImageView) view.findViewById(R.id.mini_i2);
        car3 = (ImageView) view.findViewById(R.id.full_i2);


        time = (TextView)view.findViewById(R.id.cab1);
        fare = (TextView)view.findViewById(R.id.cab2);
        person = (TextView)view.findViewById(R.id.cab3);


        time1 = (TextView)view.findViewById(R.id.sedan1);
        time2 = (TextView)view.findViewById(R.id.mini_t1);
        time3 = (TextView)view.findViewById(R.id.full_t1);
        taxyType =1;
        LinearLayout sedan = (LinearLayout) view.findViewById(R.id.sedan);
        sedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTriangle(true, false, false);
                taxyType =1;
                showDetail(taxyType);
            }
        });

        LinearLayout minivan = (LinearLayout) view.findViewById(R.id.minivan);
        minivan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTriangle(false, true, false);
                taxyType =2 ;
               showDetail(taxyType);
            }
        });

        LinearLayout fullvan = (LinearLayout) view.findViewById(R.id.fullvan);
        fullvan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taxyType =3 ;
                showTriangle(false, false, true);
                showDetail(taxyType);

            }
        });

        sedan.performClick();


        btnRequest = (Button)view.findViewById(R.id.bookRideBtn);

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity4)getActivity()).bookCab(taxyType);
            }
        });

        ((MainActivity4)getActivity()).setMapHeight(getResources().getDimensionPixelOffset(R.dimen.book_menu_size));
        return view;
    }

    private void showDetail(int taxyType) {
        try {
            int eta = ((MainActivity4)getActivity()).ETA.get(taxyType).equals("null")?0:((MainActivity4)getActivity()).ETA.get(taxyType);
            time.setText(eta+"");
            float fareStr =Math.round(((MainActivity4) getActivity()).sharedPreference.getDistanceInMeter()*taxyType/1000);
            fare.setText("$" + fareStr);
        }catch (Exception e){
            time.setText("NA");
            e.printStackTrace();
        }

    }

    private void setBookButtonCaption(int i) {
        i=5;
        if(i >0 ){
            btnRequest.setEnabled(true);
            btnRequest.setText("Confirm your ride");
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity4)getActivity()).bookCab(1);
                }
            });
        }else{
            btnRequest.setText("Not Available");
            //btnRequest.setEnabled(false);
        }
    }

    private void setFareResponse(String response) {
        try {
            JSONObject mJsonObject = new JSONObject(response);
            if (mJsonObject.getString("fareQuote").equalsIgnoreCase(
                    "-1")) {
                Toast.makeText(getActivity(),
                        mJsonObject.getString("message"),
                        Toast.LENGTH_LONG).show();
            } else if (mJsonObject.getString("fareQuote")
                    .equalsIgnoreCase("-2")) {
                Toast.makeText(getActivity(),
                        mJsonObject.getString("message"),
                        Toast.LENGTH_LONG).show();
            } else if (mJsonObject.getString("fareQuote")
                    .equalsIgnoreCase("-3")) {
            }else {
                fare.setText("$ "+mJsonObject.getString("fareQuote"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showTriangle(boolean b1, boolean b2, boolean b3) {
        tringle1.setVisibility(b1 ? View.VISIBLE : View.INVISIBLE);
        tringle2.setVisibility(b2 ? View.VISIBLE : View.INVISIBLE);
        tringle3.setVisibility(b3 ? View.VISIBLE : View.INVISIBLE);
        car1.setBackgroundResource(b1 ? R.drawable.ic_cab : R.drawable.ic_cab_deactive);
        car2.setBackgroundResource(b2 ? R.drawable.ic_minivan : R.drawable.ic_minivan_deactive);
        car3.setBackgroundResource(b3 ? R.drawable.ic_bigcar : R.drawable.ic_bigcar_deactive);
    }

}
