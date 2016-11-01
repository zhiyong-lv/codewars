package com.zlv.java.test.chapter3;

import java.math.BigInteger;
import java.util.*;

public class InputTest {
	
	public static String getStr(Scanner in, String message) {
		System.out.println(message);
		String str = in.nextLine();
		return str;
	}
	
	public static int getNum(Scanner in, String message) {
		int num = 0;
		boolean isDone = false;
		
		do {
			try {
				System.out.println(message);
				num = in.nextInt();
				isDone = false;
			} catch (InputMismatchException e) {
				System.err.println("Please input a number!");
				in.nextLine();
				isDone = true;
			}
		} while(isDone);
		
		return num;
	}
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		String name = getStr(in, "Please input your name:");
		int age = getNum(in, "Please input your age:");
		System.out.printf("%tc: Hello %s. Next year you will be %d\n",new Date(), name, (age+1));
	}
}
