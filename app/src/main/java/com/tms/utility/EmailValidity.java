package com.tms.utility;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidity {
	
	
	
	public static boolean isEmailValid(String email) {

		boolean isValid;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		} else {

			Log.i(".....Email", "Not valid");

			isValid = false;

		}
		return isValid;
	}

}
