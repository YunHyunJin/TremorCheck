package com.bcilab.tremorapp.Function;

import java.lang.Math;

public class Calculater {

    public double sum (double[] a) {
        if (a.length > 0) {
            double sum = 0;
 
            for (double i : a) {
                sum += i;
            }

            return sum;
        }
        return 0;
    }
    public double mean (double[] a){
        double sum = sum(a);
        double mean = 0;
        mean = sum / (a.length * 1.0);
        return mean;
    }

    public double sd (double[] a){
        double sum = 0;
        double mean = mean(a);
 
        for (double i : a)
            sum += Math.pow((i - mean), 2);
        return Math.sqrt( sum / ( a.length - 1 ) ); // sample
    }
	
}
