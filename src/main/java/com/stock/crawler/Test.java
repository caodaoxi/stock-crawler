package com.stock.crawler;

/**
 * Created by caodaoxi on 16-7-2.
 */
public class Test {
    public static void main(String[] args) {
        double ema12 = (39.97183531727647*11)/13 + (42.20*2)/13;
        double ema26 = (39.54019019844329*25)/27 + (42.20*2)/27;
        double dif = ema12 - ema26;
        double dea = (0.09060517970244679*8)/10 + (dif*2)/10;
        double bar = 2*(dif - dea);
        System.out.println(bar);
    }
}
