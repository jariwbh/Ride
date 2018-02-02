package com.tms.Gps;

public interface IGPSActivity {
	public void locationChanged(double longitude, double latitude);

	public void displayGPSSettingsDialog();
}