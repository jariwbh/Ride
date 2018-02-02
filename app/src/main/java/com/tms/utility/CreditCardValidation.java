/**
 * @author arvind.agarwal
 * This class checks for validity of credit card
 * 
 *  Credit card numbers follow certain patterns. A credit card number must have between 13 and 16 digits. It must start with:
 	4 for Visa cards
	5 for Master cards
 	37 for American Express cards
 	6 for Discover cards
 * 
 *  1. Double every second digit from right to left. If doubling of a digit results in a two-digit number, add up the two digits to get a single-digit number.

	2*2= 4
	2*2= 4
	4*2= 8
	1*2= 2
	6 * 2 = 12 (1 + 2 = 3) 

	5 * 2 = 10 (1 + 0 = 1) 

	8 * 2 = 16 (1 + 6 = 7) 

	4*2= 8

	2. Now add all single-digit numbers from Step 1. 4 + 4 + 8 + 2 + 3 + 1 + 7 + 8 = 37

	3. Add all digits in the odd places from right to left in the card number. 6 + 6 + 0 + 8 + 0 + 7 + 8 + 3 = 38

	4. Sum the results from Step 2 and Step 3. 37 + 38 = 75


	5. If the result from Step 4 is divisible by 10, the card number is valid; otherwise, it is invalid.
 */




package com.tms.utility;

public class CreditCardValidation {

	public static boolean isValid(long number) {

		int total = sumOfDoubleEvenPlace(number) + sumOfOddPlace(number);

		if ((total % 10 == 0) && (prefixMatched(number, 1) == true)) {
			return true;
		} else {
			return false;
		}
	}

	public static int getDigit(int number) {

		if (number <= 9) {
			return number;
		} else {
			int firstDigit = number % 10;
			int secondDigit = (int) (number / 10);

			return firstDigit + secondDigit;
		}
	}

	public static int sumOfOddPlace(long number) {
		int result = 0;

		while (number > 0) {
			result += (int) (number % 10);
			number = number / 100;
		}

		return result;
	}

	public static int sumOfDoubleEvenPlace(long number) {

		int result = 0;
		long temp = 0;

		while (number > 0) {
			temp = number % 100;
			result += getDigit((int) (temp / 10) * 2);
			number = number / 100;
		}

		return result;
	}

	public static boolean prefixMatched(long number, int d) {

		if ((getPrefix(number, d) == 3) || (getPrefix(number, d) == 4)
				|| (getPrefix(number, d) == 5) || (getPrefix(number, d) == 6)) {

			if (getPrefix(number, d) == 3) {
				System.out.println("\nAmerican Express Card ! ");
			} else if (getPrefix(number, d) == 4) {
				System.out.println("\nVisa Card ! ");
			} else if (getPrefix(number, d) == 5) {
				System.out.println("\nMaster Card !");
			} else if (getPrefix(number, d) == 6) {
				System.out.println("\nDiscover Card !");
			}

			return true;

		} else {

			return false;

		}
	}

	public static int getSize(long d) {

		int count = 0;

		while (d > 0) {
			d = d / 10;

			count++;
		}

		return count;

	}

	/**
	 * Return the first k number of digits from number. If the number of digits
	 * in number is less than k, return number.
	 */
	public static long getPrefix(long number, int k) {

		if (getSize(number) < k) {
			return number;
		} else {

			int size = (int) getSize(number);

			for (int i = 0; i < (size - k); i++) {
				number = number / 10;
			}

			return number;

		}

	}
	
	
	public static  boolean whichTypeCreditCard(String creditCard) {

		boolean bool;

		if (creditCard.substring(0, 1).equals("4")) {
			
			//Visa
			bool=true;

		} else if (creditCard.substring(0, 1).equals("5")) {
			
			///Master
			bool=true;

		} else if (creditCard.substring(0, 1).equals("6")) {
			
			//Discover
			bool=true;

		} else if (creditCard.substring(0, 2).equals("34")
				|| creditCard.substring(0, 2).equals("37")) {
			//American express

			bool=true;
		} else {
			bool=false;
		}

		return bool;

	}


}
