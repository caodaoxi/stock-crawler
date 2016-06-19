package com.stock.crawler;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caodaoxi on 16-6-18.
 */
public class KDJ {

    public void getKDJ(List<JSONObject> quoteData) {
        if(quoteData.size() > 12) {
            List<Double> rsv = getRSV(quoteData);
            List<Double> k = getMA(rsv, 3);
            List<Double> d = getMA(k, 3);
            List<Double> subListK = k.subList(2, k.size());
            List<Double> j = new ArrayList<Double>();

            for (int i = 0; i < subListK.size(); i++) {
                double v = Math.rint((3*subListK.get(i) - 2*d.get(i))*1000)/1000;
                j.add(v);
            }


            for(JSONObject item : quoteData.subList(0, 12)) {
                item.put("KDJ_K", -1000);
                item.put("KDJ_D", -1000);
                item.put("KDJ_J", -1000);
            }

            List<JSONObject> subListQuoteData = quoteData.subList(12, quoteData.size());
            try {
                for(int m = 0; m < subListQuoteData.size(); m++) {
                    JSONObject jsonObject = subListQuoteData.get(m);
                    jsonObject.put("KDJ_K", subListK.get(m) == null? -1000 : subListK.get(m));
                    jsonObject.put("KDJ_D", d.get(m) == null? -1000 : d.get(m));
                    if(j.get(m) > 100) {
                        jsonObject.put("KDJ_J", 100);
                    } else if(j.get(m) < 0) {
                        jsonObject.put("KDJ_J", 0);
                    } else {
                        jsonObject.put("KDJ_J", j.get(m));
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }

        }

    }

    public List<Double> getRSV(List<JSONObject> arrays) {
        List<Double> rsv = new ArrayList<Double>();
        int x = 9;
        while (x <= arrays.size()) {
            List<JSONObject> subList = arrays.subList(x-9, x);
            double high = max(subList, "maxPrice");
            double low = min(subList, "minPrice");
            double close = arrays.get(x-1).getDouble("closePrice");
            rsv.add((close-low)/(high-low)*100);
            x++;
        }
        return rsv;
    }

    public List<Double> getMA(List<Double> values, int window) {
        List<Double> array = new ArrayList<Double>();
        int x = window;
        while (x <= values.size()) {
            double curmb = 50;
            if((x - window) == 0) {
                curmb = avg(values.subList(x - window, x));
            } else{
                double last = array.get(array.size() - 1);
                curmb = (last*2 + values.get(x-1))/3;
            }
            array.add(Math.rint(curmb*1000)/1000);
            x++;
        }
        return array;
    }

    public double max(List<JSONObject> list, String fieldName) {
        double max = 0;
        for (JSONObject item : list) {
            if(item.getDouble(fieldName) >= max) max = item.getDouble(fieldName);
        }
        return max;
    }

    public double min(List<JSONObject> list, String fieldName) {
        double min = 1000000;
        for (JSONObject item : list) {
            if(item.getDouble(fieldName) <= min) min = item.getDouble(fieldName);
        }
        return min;
    }

    public double avg(List<Double> array) {

        int length = array.size();
        double total = 0;
        for (Double item : array) {
            total = total + item;
        }
        return total/length;
    }

}
