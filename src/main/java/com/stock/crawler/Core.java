package com.stock.crawler;

import com.stock.crawler.utils.DBUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caodaoxi on 16-7-6.
 */
public class Core {

    private Connection con = null;
    private Map<String, List<JSONObject>> quotes= new HashMap<String, List<JSONObject>>();
    private MACD macd;
    private KDJ kdj;
    private MA ma;
    private CCI cci;
    private ENE ene;

    public Core() {
    }

    public Core(Connection con) {
        this.con = con;
        macd = new MACD();
        kdj = new KDJ();
        ma = new MA();
        cci = new CCI();
        ene = new ENE();
    }

    public JSONObject getKDJ(JSONObject quote) {
        String stockId = quote.getString("stockId");
        List<JSONObject> yestodayQuotes = getQuoteCache(stockId);
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        yestodayQuotes.add(quote);
        kdj.getKDJ(yestodayQuotes);
        return yestodayQuotes.remove(yestodayQuotes.size() - 1);
    }

    public JSONObject getMACD(JSONObject quote) {
        String stockId = quote.getString("stockId");
        JSONObject yestodayQuote = null;
        List<JSONObject> yestodayQuotes = getQuoteCache(stockId);
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        yestodayQuote = yestodayQuotes.get(yestodayQuotes.size() - 1);
        Collections.reverse(yestodayQuotes);
        if (yestodayQuote == null) return null;

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
        return quote;
    }

    public JSONObject getENE(JSONObject quote) {
        String stockId = quote.getString("stockId");
        List<JSONObject> yestodayQuotes = getQuoteCache(stockId);
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        yestodayQuotes.add(quote);
        ene.getENE(yestodayQuotes, 10, 11, 9);
        return yestodayQuotes.remove(yestodayQuotes.size() - 1);
    }

    public JSONObject getCCI(JSONObject quote) {
        String stockId = quote.getString("stockId");
        List<JSONObject> yestodayQuotes = getQuoteCache(stockId);
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        yestodayQuotes.add(quote);
        cci.getCCIS(yestodayQuotes, 14);
        return yestodayQuotes.remove(yestodayQuotes.size() - 1);
    }

    public JSONObject getMA(JSONObject quote) {
        String stockId = quote.getString("stockId");
        List<JSONObject> yestodayQuotes = getQuoteCache(stockId);
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        yestodayQuotes.add(quote);
        ma.getALLMA(yestodayQuotes);
        return yestodayQuotes.remove(yestodayQuotes.size() - 1);
    }

    public List<JSONObject> getQuoteCache(String stockId) {

        List<JSONObject> yestodayQuotes = quotes.get(stockId);

        String yesterday = new DateTime().plusDays(-1).toString("yyyy-MM-dd");

        if (yestodayQuotes != null) return yestodayQuotes;
        yestodayQuotes = DBUtils.getLatestQuoteByStockId(stockId, 120, yesterday, con);
        quotes.put(stockId, yestodayQuotes);
        if (yestodayQuotes == null || yestodayQuotes.size() == 0) return null;
        Collections.reverse(yestodayQuotes);
        return yestodayQuotes;
    }

    public JSONObject getAllQuota(JSONObject quote) {
        getCCI(quote);
        getENE(quote);
        getKDJ(quote);
        getMA(quote);
        getMACD(quote);
        return quote;
    }
}
