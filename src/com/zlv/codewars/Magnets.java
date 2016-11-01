package com.zlv.codewars;

public class Magnets {
	
	public static double doubles(int maxk, int maxn) {
		double sum = 0;
		for(int k=1; k<=maxk; k++) {
			for(int n=1; n<=maxn; n++) {
				sum += getVFuncVal(k,n);
			}
		}
		return sum;
	}
	
	private static double getVFuncVal(int k, int n) {
		return 1.0 / (k * Math.pow(n+1, k*2));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(doubles(1,10));
	}

}
