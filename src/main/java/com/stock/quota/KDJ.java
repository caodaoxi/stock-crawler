package com.stock.quota;


import com.stock.quota.utils.DBUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.*;

/**
 * Created by caodaoxi on 16-6-18.
 */
public class KDJ {

    private Connection con = null;


    private Map<String, List<JSONObject>> quotes= new HashMap<String, List<JSONObject>>();

    public KDJ() {

    }

    public KDJ(Connection con) {
        this.con = con;
    }
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

    public JSONObject getTodayKDJ(JSONObject quote) {
        String stockId = quote.getString("stockId");

        List<JSONObject> yestodayQuotes = quotes.get(stockId);
        String yesterday = new DateTime().plusDays(-1).toString("yyyy-MM-dd");
        if (yestodayQuotes == null) {

            yestodayQuotes = DBUtils.getLatestQuoteByStockId(stockId, 90, yesterday);
            quotes.put(stockId, yestodayQuotes);
        }
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        Collections.reverse(yestodayQuotes);
        yestodayQuotes.add(quote);
        getKDJ(yestodayQuotes);
        return yestodayQuotes.remove(yestodayQuotes.size() - 1);

//
//        double ema12 = (yestodayQuote.getDouble("ema12")*11)/13 + (quote.getDouble("closePrice")*2)/13;
//        double ema26 = (yestodayQuote.getDouble("ema26")*25)/27 + (quote.getDouble("closePrice")*2)/27;
//        double dif = ema12 - ema26;
//        double dea = (yestodayQuote.getDouble("dea")*8)/10 + (dif*2)/10;
//        double macd = 2 * (dif - dea);
//        quote.put("DIF", dif);
//        quote.put("DEA", dea);
//        quote.put("EMA12", ema12);
//        quote.put("EMA26", ema26);
//        quote.put("MACD", macd);
    }
}
