package com.elikill58.negativity.universal.utils;

public class Maths {
	
	public static int floor(double num) {
		int floor = (int) num;
		return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
	}

	public static int round(double num) {
		return floor(num + 0.5D);
	}

	public static double square(double num) {
		return num * num;
	}
}
