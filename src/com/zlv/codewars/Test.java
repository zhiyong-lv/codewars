package com.zlv.codewars;


import java.util.stream.IntStream;

public class Test {
	public static void main(String[] args) {
		IntStream.range(0, 10).filter(n -> n%2 == 0).forEach(System.out::println);
		
	}
}
