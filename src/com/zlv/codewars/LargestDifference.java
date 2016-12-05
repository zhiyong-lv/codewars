package com.zlv.codewars;

import java.util.*;

public class LargestDifference {
	
	public static int largestDifference(int[] data) {
		int max = 0;
		
		for(int i=0,tmpMax=0; i<data.length-1; i++) {
			for(int j=data.length-1; j>i; j--) {
				if(data[i] <= data[j] && j-i>max) {
					max = j - i;
					if(tmpMax >= data.length-i-2) 
						return max;
					else 
						break;
				}
			}
		}
		
        return max;
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(largestDifference(new int[] {9,4,1,10,3,4,0,-1,-2}));
	}

}
