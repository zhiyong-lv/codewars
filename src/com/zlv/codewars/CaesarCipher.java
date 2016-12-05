package com.zlv.codewars;

import java.util.*;
import java.util.stream.IntStream;

public class CaesarCipher {
	private static final int PARTS_COUNT = 5;
	private static final int ALPHABETA_COUNT = 26;
	
	private static final char shiftChar(char c, int shift) {
		if(c >= 'a' && c <= 'z')
			return (char) ((c - 'a' + shift + (Math.abs(shift)/ALPHABETA_COUNT+1)*ALPHABETA_COUNT) % ALPHABETA_COUNT + 'a');
		else if(c >= 'A' && c <= 'Z')
			return (char) ((c - 'A' + shift + (Math.abs(shift)/ALPHABETA_COUNT+1)*ALPHABETA_COUNT) % ALPHABETA_COUNT + 'A');
		else 
			return c;
	}
	
	public static List<String>  movingShift(String s, int shift) {
		StringBuffer sb = null;
		int length = (s.length()%PARTS_COUNT == 0) ? s.length()/PARTS_COUNT : s.length()/PARTS_COUNT + 1;
		List<String> result = new LinkedList<String>();
		
		
		for(int i=0; i<PARTS_COUNT-1; i++) {
			sb = new StringBuffer("");
			for(int j=0; j<length; j++) { 
				sb.append(shiftChar(s.charAt(i*length + j), i*length + j + shift));
			}
			result.add(sb.toString());
		}
		
		sb = new StringBuffer("");
		String lastStr = s.substring((PARTS_COUNT-1)*length);
		for(int j=0; j<lastStr.length(); j++) {
			sb.append(shiftChar(s.charAt((PARTS_COUNT-1)*length + j), (PARTS_COUNT-1)*length + j + shift));
		}
		result.add(sb.toString());
		
		return result;
	}
	
	public static String  demovingShift(List<String> s, int shift) {
		StringBuffer sb = new StringBuffer("");
		
		int index = 0; 
		
		for(String str : s) {
			for(char c : str.toCharArray()) {
				sb.append(shiftChar(c,-1*((index++) + shift)));
			}
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String s = "I should have known that you would have a perfect answer for me!!!";
		List<String> v = Arrays.asList("J vltasl rlhr ", "zdfog odxr ypw", " atasl rlhr p ", "gwkzzyq zntyhv", " lvz wp!!!");
		System.out.println(movingShift(s,1));
		System.out.println(demovingShift(v,1));
	}
}
