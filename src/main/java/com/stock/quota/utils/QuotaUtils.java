package com.stock.quota.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caodaoxi on 16-6-18.
 */
public class QuotaUtils {

    private String allQuotesUrl = "http://money.finance.sina.com.cn/d/api/openapi_proxy.php";

    private String yqlUrl = "http://query.yahooapis.com/v1/public/yql";

    private String[] indexArray = {"000001.SS", "399001.SZ", "000300.SS"};

    public void dataLoad(String startDate, String endDate) {

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
    public List<JSONObject> loadAllQuoteSymbol() {

        Document doc = null;
        List<JSONObject> allQuotes = new ArrayList<JSONObject>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Symbol", "000001.SS");
        jsonObject.put("name", "上证指数");
        allQuotes.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("Symbol", "399001.SZ");
        jsonObject.put("name", "深证成指");
        allQuotes.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("Symbol", "000300.SS");
        jsonObject.put("name", "沪深300");
        allQuotes.add(jsonObject);
        int count = 1;
        while (count < 100) {
            String paraVal = "[[\"hq\",\"hs_a\",\"\",0," + count + ",500]]";
            try {
                Map<String, String> params = new HashMap<String, String>();
                params.put("__s", paraVal);
                doc = Jsoup.connect(allQuotesUrl).data(params).ignoreContentType(true).timeout(10000).get();
                Elements elements = doc.select("body");
                Element element = elements.get(0);
                String body = element.text();
                JSONArray jsonArray = new JSONArray(body);
                JSONObject json = (JSONObject) jsonArray.get(0);
                String day = json.get("day").toString();
                JSONArray fields = (JSONArray) json.get("fields");
                JSONArray items = (JSONArray) json.get("items");
                for(Object item : items) {
                    JSONArray itemArray = (JSONArray) item;
                    JSONObject js = new JSONObject();
                    for(int i = 0; i < fields.length(); i++) {
                        js.put(fields.get(i).toString(), itemArray.get(i));

                    }
                    String code = js.getString("symbol");
                    if(code.indexOf("sh") != -1) {
                        js.put("symbol", code.substring(2) + ".SS");
                    } else if(code.indexOf("sz") != -1) {
                        js.put("symbol", code.substring(2) + ".SZ");
                    }
                    allQuotes.add(js);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    public List<JSONObject> loadQuoteData(List<JSONObject> quotes, String startDate, String endDate) {
        Document doc = null;
        for(JSONObject quote : quotes) {
            String yquery = "select * from yahoo.finance.historicaldata where symbol = \"" + quote.getString("symbol").toLowerCase() + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"";
            Map<String, String> params = new HashMap<String, String>();
            params.put("q", yquery);
            params.put("format", "json");
            params.put("env", "http://datatables.org/alltables.env");
            try {
                doc = Jsoup.connect(yqlUrl).data(params).ignoreContentType(true).timeout(10000).get();
                Elements elements = doc.select("body");
                Element element = elements.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
    public static void main(String[] args) {
        QuotaUtils quotaUtils = new QuotaUtils();
//        Connection con = null;
//        try {
//            Configuration.config();
//            DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
//            con = DBPoolContext.getInstance().open();
//            List<JSONObject> quotes = DBUtils.getQuoteByStockId("600053", 100, con);
//            KDJ kdj = new KDJ();
//            kdj.getKDJ(quotes);
//            for (JSONObject quote : quotes) {
//                System.out.println(quote.toString());
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            DBUtils.close(con);
//        }
        List<JSONObject> quotes = quotaUtils.loadAllQuoteSymbol();
//        List<JSONObject> quotes = new ArrayList<JSONObject>();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("symbol", "600030.SS");
//        quotes.add(jsonObject);
//        quotaUtils.loadQuoteData(quotes, "2015-03-01", "2016-06-17");
    }
}
