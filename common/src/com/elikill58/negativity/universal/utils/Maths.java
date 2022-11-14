package com.elikill58.negativity.universal.utils;

public class Maths {
	
	public static boolean isOnGround(final double y) {
		return (y % 0.015625 == 0.0);
	}
	
	public static int floor(double num) {
		int floor = (int) num;
		return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
	}

	public static int round(double num) {
		return floor(num + 0.5d);
	}

	public static int roundLoc(double num) {
		return (int) Math.round(num - 0.5d);
	}

	public static double square(double num) {
		return num * num;
	}
    
    public static double getGcd(double a, double b) {
    	return getGcd(a, b, 5);
    }
    
    public static double getGcd(double a, double b, int internalAmount) {
    	if(internalAmount <= 0)
    		return a;
    	if(a < b)
    		return getGcd(b, a, internalAmount - 1);
    	return Math.abs(b) < 0.001 ? a : getGcd(b, a - Math.floor(a / b) * b, internalAmount - 1);
    }
}
