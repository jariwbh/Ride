package com.tms;

import com.google.android.gms.maps.model.LatLng;

public interface LatLngResponse {
	
		void onResponseLatLng(LatLng response);
		void onErrorLatLng();
void onNoLAtLongFound(String string);
		}


