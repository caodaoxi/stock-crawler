package com.stock.crawler;

import com.stock.crawler.utils.DBUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caodaoxi on 16-6-19.
 */
public class MACD {

    private Connection con = null;


    private Map<String, JSONObject> quotes= new HashMap<String, JSONObject>();

    public MACD() {

    }

    public MACD(Connection con) {
        this.con = con;
    }


    /**
     * Calculate EMA,
     *
     * @param list
     *            :Price list to calculate，the first at head, the last at tail.
     * @return
     */
    public static final Double getEXPMA(final List<Double> list, final int number) {
        // 开始计算EMA值，
        Double k = 2.0 / (number + 1.0);// 计算出序数
        Double ema = list.get(0);// 第一天ema等于当天收盘价
        for (int i = 1; i < list.size(); i++) {
            // 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
            ema = list.get(i) * k + ema * (1 - k);
        }
        return ema;
    }

    /**
     * calculate MACD values
     *
     * @param list
     *            :Price list to calculate，the first at head, the last at tail.
     * @param shortPeriod
     *            :the short period value.
     * @param longPeriod
     *            :the long period value.
     * @param midPeriod
     *            :the mid period value.
     * @return
     */
    public static final HashMap<String, Double> getMACD(final List<Double> list, final int shortPeriod, final int longPeriod, int midPeriod) {
        HashMap<String, Double> macdData = new HashMap<String, Double>();
        List<Double> diffList = new ArrayList<Double>();
        Double shortEMA = 0.0;
        Double longEMA = 0.0;
        Double dif = 0.0;
        Double dea = 0.0;

        for (int i = list.size() - 1; i >= 0; i--) {
            List<Double> sublist = list.subList(0, list.size() - i);
            shortEMA = MACD.getEXPMA(sublist, shortPeriod);
            longEMA = MACD.getEXPMA(sublist, longPeriod);
            dif = shortEMA - longEMA;
            diffList.add(dif);
        }
        dea = MACD.getEXPMA(diffList, midPeriod);
        macdData.put("DIF", dif);
        macdData.put("DEA", dea);
        macdData.put("EMA12", shortEMA);
        macdData.put("EMA26", longEMA);
        macdData.put("MACD", (dif - dea) * 2);
        return macdData;
    }

    public void getTodayMACD(JSONObject quote) {
        String stockId = quote.getString("stockId");
        JSONObject yestodayQuote = quotes.get(stockId);
        if (yestodayQuote == null) {
            yestodayQuote = DBUtils.getLatestQuoteByStockId(stockId, 1, con);
            quotes.put(stockId, quote);
        }

        double ema12 = (yestodayQuote.getDouble("ema12")*11)/13 + (quote.getDouble("closePrice")*2)/13;
        double ema26 = (yestodayQuote.getDouble("ema26")*25)/27 + (quote.getDouble("closePrice")*2)/27;
        double dif = ema12 - ema26;
        double dea = (yestodayQuote.getDouble("dea")*8)/10 + (dif*2)/10;
        double macd = 2 * (dif - dea);
        quote.put("DIF", dif);
        quote.put("DEA", dea);
        quote.put("EMA12", ema12);
        quote.put("EMA26", ema26);
        quote.put("MACD", macd);
    }

}
