package com.stock.crawler;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by caodaoxi on 16-6-28.
 */
public class CCI {

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
}
