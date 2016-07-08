package com.stock.quota;

import com.stock.quota.utils.DBUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.*;

/**
 * Created by caodaoxi on 16-6-19.
 */
public class ENE {

    private Map<String, List<JSONObject>> quotes= new HashMap<String, List<JSONObject>>();


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


    public JSONObject getTodayENE(JSONObject quote) {
        String stockId = quote.getString("stockId");

        List<JSONObject> yestodayQuotes = quotes.get(stockId);
        String yesterday = new DateTime().plusDays(-1).toString("yyyy-MM-dd");
        if (yestodayQuotes == null) {

            yestodayQuotes = DBUtils.getLatestQuoteByStockId(stockId, 15, yesterday);
            quotes.put(stockId, yestodayQuotes);
        }
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        Collections.reverse(yestodayQuotes);
        yestodayQuotes.add(quote);
        getENE(yestodayQuotes, 10, 11, 9);
        return yestodayQuotes.remove(yestodayQuotes.size() - 1);
    }
}
