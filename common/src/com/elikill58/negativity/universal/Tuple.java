package com.elikill58.negativity.universal;

public class Tuple<R, T> {

	private final R a;
	private final T b;
	
	public Tuple(R a, T b) {
		this.a = a;
		this.b = b;
	}
	
	public R getA() {
		return a;
	}
	
	public T getB() {
		return b;
	}
	
	@Override
	public String toString() {
		return "Tuple{" + a + "," + b + "}";
	}
}
