package com.zlv.java.test.chapter13;

import java.util.*;

public class LinkedListTest {
	public static void main(String[] args) {
		LinkedList<String> lList = new LinkedList<String>();
		
		for(int i : new int[] {1, 2, 3, 4, 5, 6, 7, 8}) {
			lList.add(String.valueOf(i));
		}
		
		for(String s : lList) {
			System.out.print(s + "\t");
		}
		System.out.print("\n");
		
		ListIterator<String> lIter = lList.listIterator();

		lIter.next();
		
		for(String s : new String[] {"a", "b", "c"}) {
			lIter.set(s);
		}
		
		for(String s : lList) {
			System.out.print(s + "\t");
		}
		System.out.print("\n");
		
		
	}
}
