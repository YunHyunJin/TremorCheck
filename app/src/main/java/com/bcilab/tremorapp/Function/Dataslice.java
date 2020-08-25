package com.bcilab.tremorapp.Function;

import com.bcilab.tremorapp.Data.Complex;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;


/*
 * In order to perform the FFT, the length of the data must be raised to the power of n. 
 * Therefore, this class finds the largest n based on the data length. 
 * It also finds the value of m next to n based on the remaining alpha delta.
 * m is greater than 6 (assume srate is 50).
 * input length = 2^n + 2^m + ... + a
 * a discard to less than 6 powers of two.
 */

public class Dataslice {
	
	private int srate = 50;
	
	public List<Integer> Dataslice(int m) {	
		List<Integer> session = new ArrayList<Integer>();
		
		int n = m;
		while(m > srate*3) {// input should be over the 3 seconds 
			int k = n; int i;
			
			for (i = 0 ; k != 1; i++)
				k = k / 2;
			
			int value = (int)Math.pow(2, i);
		    if(value < srate*2) 
				break;
		    else session.add(i);
			n = n - value;
		}
		
		return session;
	}


	public Complex[] zeropadding(Complex[] input, int padlen) {
		Complex[] output = new Complex[padlen];
		Complex zero =  new Complex(0,0);
		for (int i = 0; i < padlen ; i++) {
			if(i < input.length)
				output[i] = input[i];
			else
				output[i] = zero;
		}
		return output;
	}

	public int calN(int len) {
		int i;
		for ( i = 0 ; len != 1; i++)
			len = len / 2;

		int size = (int)Math.pow(2, i+1);
		return size;
	}

}
