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

    /**
     * Determine if the requested {@code index} and {@code length} will fit within {@code capacity}.
     * @param index The starting index.
     * @param length The length which will be utilized (starting from {@code index}).
     * @param capacity The capacity that {@code index + length} is allowed to be within.
     * @return {@code false} if the requested {@code index} and {@code length} will fit within {@code capacity}.
     * {@code true} if this would result in an index out of bounds exception.
     */
    public static boolean isOutOfBounds(int index, int length, int capacity) {
        return (index | length | capacity | (index + length) | (capacity - (index + length))) < 0;
    }
}
