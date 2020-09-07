package com.bcilab.tremorapp.Function;

import java.util.List;

/*
 * If the user removes the pen from the pad during the task, the x,y data has an arbitrary value(0,0).
 * These arbitrary values �뗢�땇re filled with predictive (virtual) data based on the data before and after.
 * input  : x,y,time data
 * output : virtual x,y, time data
 */
public class FillNull {

    public double[] FillNull(List<Double> input, int n) {
        int Nstart = 0; int Nend = 0;
        double[] Fdata = new double[n];
        int flag = 1;

        for(int i = 0; i < n ; i++) {
            if ( input.get(i) != 0 ) {
                if (flag == 0){
                    Nend = i;
                    double gap = (input.get(Nend) - input.get(Nstart)) / (double)(Nend - Nstart);
                    double start =  input.get(Nstart);
                    for (int j = Nstart; j < Nend ; j++) {
                        Fdata[j] = start+ gap*j;
                    }
                    flag = 1;
                }
                Fdata[i] = input.get(i);
                continue;}

            if( flag == 1 ) {
                Nstart = i;		flag = 0;
            }
            // if x is 0, y also 0
        }


        return Fdata;
    }

}
