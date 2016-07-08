package com.stock.quota;

import com.stock.quota.utils.DBUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.*;

/**
 * Created by caodaoxi on 16-7-6.
 */
public class MA {

    private Map<String, List<JSONObject>> quotes= new HashMap<String, List<JSONObject>>();

    public MA() {

    }

    public void getALLMA(List<JSONObject> arrays) {
        List<Double> last5Array = new ArrayList<Double>();
        List<Double>last10Array  = new ArrayList<Double>();
        List<Double>last20Array  = new ArrayList<Double>();
        List<Double>last30Array  = new ArrayList<Double>();
        List<Double>last60Array  = new ArrayList<Double>();
        List<Double>last120Array  = new ArrayList<Double>();
        for(int i = 0; i < arrays.size(); i++) {
            JSONObject quote = arrays.get(i);
            last5Array.add(quote.getDouble("closePrice"));
            last10Array.add(quote.getDouble("closePrice"));
            last20Array.add(quote.getDouble("closePrice"));
            last30Array.add(quote.getDouble("closePrice"));
            last60Array.add(quote.getDouble("closePrice"));
            last120Array.add(quote.getDouble("closePrice"));

            quote.put("MA_5", -1000);
            quote.put("MA_10", -1000);
            quote.put("MA_20", -1000);
            quote.put("MA_30", -1000);
            quote.put("MA_60", -1000);
            quote.put("MA_120", -1000);
            if(i < 4) continue;
            if(last5Array.size() == 5) {
                quote.put("MA_5", getMa(last5Array));
                last5Array.remove(0);
            }

            if(i < 9) continue;
            if(last10Array.size() == 10) {
                quote.put("MA_10", getMa(last10Array));
                last10Array.remove(0);
            }

            if(i < 19) continue;
            if(last20Array.size() == 20) {
                quote.put("MA_20", getMa(last20Array));
                last20Array.remove(0);
            }

            if(i < 29) continue;
            if(last30Array.size() == 30) {
                quote.put("MA_30", getMa(last30Array));
                last30Array.remove(0);
            }

            if(i < 59) continue;
            if(last60Array.size() == 60) {
                quote.put("MA_60", getMa(last60Array));
                last60Array.remove(0);
            }

            if(i < 119) continue;
            if(last120Array.size() == 120) {
                quote.put("MA_120", getMa(last120Array));
                last120Array.remove(0);
            }
        }


    }

    public static Double getProfitRate(double price1, double price2) {
        if (price1 == 0) return null;
        return Math.rint(((price2-price1)/price1)*100000)/100000;
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


    public JSONObject getTodayMA(JSONObject quote) {
        String stockId = quote.getString("stockId");

        List<JSONObject> yestodayQuotes = quotes.get(stockId);

        String yesterday = new DateTime().plusDays(-1).toString("yyyy-MM-dd");

        if (yestodayQuotes == null) {
            yestodayQuotes = DBUtils.getLatestQuoteByStockId(stockId, 120, yesterday);
            quotes.put(stockId, yestodayQuotes);
        }
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        Collections.reverse(yestodayQuotes);
        yestodayQuotes.add(quote);
        getALLMA(yestodayQuotes);
        return yestodayQuotes.remove(yestodayQuotes.size() - 1);
    }

}
