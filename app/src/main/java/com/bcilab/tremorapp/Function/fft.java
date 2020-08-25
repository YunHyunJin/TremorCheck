package com.bcilab.tremorapp.Function;



/*
 * calculating frequency information
 * input  : raw data x, y
 * output : max amplitude and frequency, mean, std,
 *          standard point(h), check tremor value(flag)
 * */


import com.bcilab.tremorapp.Data.Complex;

public class fft {

    // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x) {
        int n = x.length;
        // base case
        if (n == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0 ) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            if (q[k] == null) break;
            else {
                double kth = -2 * k * Math.PI / n;
                Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
                y[k]       = q[k].plus(wk.times(r[k]));
                y[k + n/2] = q[k].minus(wk.times(r[k]));
            }
        }



        return y;
    }

    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int n = x.length;
        Complex[] y = new Complex[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }
        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].scale(1.0 / n);
        }

        return y;

    }

    // compute the circular convolution of x and y
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }

        int n = x.length;

        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply
        Complex[] c = new Complex[n];
        for (int i = 0; i < n; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }

    public static double[] analysis(double[] result, float[] index) {
        double mean = 0; double std = 0;  double amp = 0; double hz = 0;
        int filterS = 0; int filterE = 0;
        double[] session = new double[result.length];

        for (int i = 0; index[i] <= 3 ; i ++)
            filterS = i;
        for (int i = 0; index[i] <= 15 ; i ++)
            filterE = i;

        int maxIndex = filterS;
        for (int i = 0 ; i < result.length ; i++)
        {
            if ( i >= filterS & i <= filterE) {
                if (result[i] >= result[maxIndex])
                    maxIndex = i ;
                session[i] = result[i];
            }
            else session[i] = 0;
        }


        amp = result[maxIndex]; hz = index[maxIndex];

        Calculater cal = new Calculater();

        mean = cal.mean(session);
        std  = cal.sd(session);

        double[] ans = {mean, std, amp, hz};
        return ans;
    }

}
