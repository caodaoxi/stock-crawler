package com.stock.crawler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caodaoxi on 16-6-19.
 */
public class ENE {

    public void getENE(List<JSONObject> arrays, int n, int m1, int m2) {
        double upper = 0.00;
        double lower = 0.00;
        double ene = 0.00;
        List<Double> lastNArray = new ArrayList<Double>();

        for(int i = 0; i < arrays.size(); i++) {
            JSONObject quote = arrays.get(i);
            lastNArray.add(quote.getDouble("closePrice"));
            quote.put("UPPER", -1000);
            quote.put("LOWER", -1000);
            quote.put("ENE", -1000);

            if(i < n-1) continue;
            if(lastNArray.size() == n) {
                double maN = getMa(lastNArray);
                upper = (1 + m1/100.00)*maN;
                lower = (1 - m2/100.00)*maN;
                ene = (upper + lower)/2.00;
                quote.put("UPPER", upper);
                quote.put("LOWER", lower);
                quote.put("ENE", ene);
                lastNArray.remove(0);
            }
        }
    }

    public double getMa(List<Double> numberArray) {
        double total = 0;
        int n = 0;
        for(Double num : numberArray) {
            if(num != null) {
                n++;
                total += num;
            }
        }
        return Math.rint((total/n)*1000)/1000;
    }
}
