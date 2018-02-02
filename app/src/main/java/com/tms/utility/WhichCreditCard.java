package com.tms.utility;

public class WhichCreditCard {

	
	
	public static String whichTypeCreditCard(String creditCard) {

		String cardType = "";

		if (creditCard.substring(0, 1).equals("4")) {
			cardType = "Visa";

		} else if (creditCard.substring(0, 1).equals("5")) {
			cardType = "Master";

		} else if (creditCard.substring(0, 1).equals("6")) {
			cardType = "Discover";

		} else if (creditCard.substring(0, 2).equals("34")
				|| creditCard.substring(0, 2).equals("37")) {

			cardType = "AmEx";

		} else {

		}

		return cardType;

	}

}
