package com.stock.quota;

import com.stock.quota.utils.DBUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caodaoxi on 16-6-28.
 */
public class CCI {


    private Map<String, List<JSONObject>> quotes= new HashMap<String, List<JSONObject>>();

    public CCI() {

    }


    public double getCCI(List<JSONObject> objectList , int n) {
        if(objectList == null || objectList.size() < n) return -1000;
        JSONObject object = objectList.get(objectList.size() - 1);
        double typ = (object.getDouble("closePrice") + object.getDouble("maxPrice") + object.getDouble("minPrice"))/3;
        List<JSONObject> subList = objectList.subList(objectList.size() - n, objectList.size());
        double maTyp = getMaTyp(subList);
        double avedevTyp = getAvedevTyp(subList, maTyp);
        return (typ - maTyp)/(avedevTyp * 0.015);
    }

    public void getCCIS(List<JSONObject> objectList , int n) {
        if(objectList == null || objectList.size() < n) return;
        for(int i = 0; i < objectList.size(); i++) {
            JSONObject object = objectList.get(objectList.size() - i - 1);
            object.put("cci", -1000);
            if((objectList.size() - i - n) >= 0) {
                double typ = (object.getDouble("closePrice") + object.getDouble("maxPrice") + object.getDouble("minPrice")) / 3;
                List<JSONObject> subList = objectList.subList(objectList.size() - i - n, objectList.size() - i);
                double maTyp = getMaTyp(subList);
                double avedevTyp = getAvedevTyp(subList, maTyp);
                double cci = (typ - maTyp) / (avedevTyp * 0.015);
                object.put("cci", cci);
            }
        }
    }

    public double getMaTyp(List<JSONObject> subList) {
        double sum = 0;
        int count = 0;
        for(JSONObject object : subList) {
            sum = sum + (object.getDouble("closePrice") + object.getDouble("maxPrice") + object.getDouble("minPrice"))/3;
            count++;
        }
        return sum/count;
    }

    public double getAvedevTyp(List<JSONObject> subList, double maTyp) {
        double sum = 0;
        int count = 0;
        for(JSONObject object : subList) {
            sum = sum + Math.abs((object.getDouble("closePrice") + object.getDouble("maxPrice") + object.getDouble("minPrice"))/3 - maTyp);
            count++;
        }
        return sum/count;
    }

    public JSONObject getTodayCCI(JSONObject quote) {
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
        getCCIS(yestodayQuotes, 14);
        return yestodayQuotes.remove(yestodayQuotes.size() - 1);
    }
}
