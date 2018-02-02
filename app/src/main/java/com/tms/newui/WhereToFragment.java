package com.tms.newui;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tms.R;
import com.tms.activity.Reservation;

/**
 * A simple {@link Fragment} subclass.
 */
public class WhereToFragment extends Fragment {


    private TextView whereToButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whereto, container, false);
        whereToButton = (TextView) view.findViewById(R.id.txt_dest);
        whereToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity4) getActivity()).showFragment(new AddressSearchFragment());
            }
        });

        ImageView btnDirection = (ImageView) view.findViewById(R.id.directionButton);
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Reservation.class));
            }
        });

        ImageView myLocation = (ImageView) view.findViewById(R.id.imageView12);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity4) getActivity()).btnMyLocation.performClick();
            }
        });
        ((MainActivity4) getActivity()).clearMap();
        return view;
    }

}
