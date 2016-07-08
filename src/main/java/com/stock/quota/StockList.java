package com.stock.quota;

import com.stock.quota.config.Configuration;
import com.stock.quota.utils.DBPoolContext;
import com.stock.quota.utils.DBUtils;
import com.stock.quota.utils.NumberValidationUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: caodaoxi
 * Date: 15-12-9
 * Time: 下午5:36
 * To DO: do nothing
 **/
public class StockList {
    private static final Logger logger = Logger.getLogger(StockList.class);
    private static List<Stock> stockList = null;


    public static void main(String[] args) {
        crawlerStockList();
//        for (Stock stock : stockList) {
//            crawlerStockDetail(stock);
//        }
        String currenDate = new DateTime().toString("yyyy-MM-dd");
        Connection con = null;
        try {
            Configuration.config();
            DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
            con = DBPoolContext.getInstance().open();
            List<JSONObject> quotes = null;
            List<Double> closePrices = null;
//            MACD macd = new MACD(con);
////
//            JSONObject quote = new JSONObject();
//            quote.put("stockId", "600030");
//            quote.put("closePrice", 16.13);
//            macd.getTodayMACD(quote);
//            System.out.println(quote.toString());
            for(Stock stock : stockList) {
                if (stock.getStockId().startsWith("30") || stock.getStockId().startsWith("60")  || stock.getStockId().startsWith("00")) {
//                    closePrices = DBUtils.getClosePrice(stock.getStockId(), 10000, con);
//                    if(closePrices.size() < 26) continue;
//
//                    Map<String, Double> macd = MACD.getMACD(closePrices, 12, 26, 9);
//                    System.out.println(stock.getStockId() + "\tema12: " + macd.get("EMA12") + "\tema26: " + macd.get("EMA26") + "\tdea: " + macd.get("DEA") + "\tdif: " + macd.get("DIF"));
//                    DBUtils.updateMACD(macd, stock.getStockId(), currenDate);
//                    if((macd.get("DIF") >= 0 || macd.get("DEA") >= 0) && macd.get("DIF") >= macd.get("DEA")){
//                        System.out.println("stockId : " + stock.getStockId() + ", DIF : " + macd.get("DIF") + ", DEA : " + macd.get("DEA") + ", MACD : " + macd.get("MACD"));
//                    }
                    quotes = DBUtils.getQuoteByStockId(stock.getStockId(), 100000);
//                    CCI cci = new CCI();
//                    cci.getCCIS(quotes, 14);
//                    DBUtils.updateCCI(quotes, con);
//                    System.out.println(stock.getStockId());
//                    KDJ kdj = new KDJ();
//                    kdj.getKDJ(quotes);
//                    DBUtils.updateKDJ(quotes);
//                    quotes = DBUtils.getQuoteByStockId(stock.getStockId(), 100000, con);
//                      MA quotaUtils = new MA();
//                      quotaUtils.getALLMA(quotes);
//                      DBUtils.updateMA(quotes);
//                    quotes = DBUtils.getQuoteByStockId(stock.getStockId(), 100000, con);
                    ENE ene = new ENE();
                    ene.getENE(quotes, 10, 11, 9);
                    DBUtils.updateENE(quotes);


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtils.close(con);
        }
    }

//    public static void main(String[] args) {
//        crawlerStockList();
//        if(stockList != null && stockList.size() > 0) {
//            stockList.add(new Stock());
//            for (Stock stock : stockList) {
////                crawlerStockDetail(stock);
//                Connection con = null;
//                try {
//                    Configuration.config();
//                    DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
//                    con = DBPoolContext.getInstance().open();
//                    List<JSONObject> quotes = DBUtils.getQuoteByStockId(stock.getStockId(), 100, con);
//                    List<Double> closePrices = DBUtils.getClosePrice(stock.getStockId(), 100, con);
//                    KDJ kdj = new KDJ();
//                    kdj.getKDJ(quotes);
//                    QuotaUtils quotaUtils = new QuotaUtils();
//                    quotaUtils.getALLMA(quotes);
//                    for (JSONObject quote : quotes) {
////                        if("2016-06-16".equals(quote.getString("tradeDate")) && quote.has("KDJ_K") && quote.getDouble("KDJ_K") < 20 && quote.getDouble("KDJ_K") > 10) {
////                            System.out.println(quote.toString());
////                        }
//
////                        if("600030".equals(quote.getString("stockId"))) {
////                            System.out.println(quote.toString());
////                        }
//                        if("2016-06-16".equals(quote.getString("tradeDate")) && quote.getDouble("closePrice") >= quote.getDouble("MA_20")
//                                && quote.getDouble("MA_5") >= quote.getDouble("MA_10")
//                                && quote.getDouble("MA_10") >= quote.getDouble("MA_20")
//                                && quote.getDouble("MA_20") >= quote.getDouble("MA_30")
//                                && quote.getDouble("MA_30") >= quote.getDouble("MA_60")
//                                && quote.getDouble("MA_120") > 0 && quote.getDouble("MA_60") >= quote.getDouble("MA_120")
//                                && quote.has("KDJ_K") && quote.getDouble("KDJ_K") < 60) {
//                            System.out.println(quote.toString());
//                        }
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    DBUtils.close(con);
//                }
//            }
//        }


//        String methodName = args.length > 0 ? args[0] : "crawlerStockTradeList";
//        try {
//            Method method = StockList.class.getMethod(methodName);
//            DateTime current = DateTime.now();
//            if(current.getDayOfWeek() > 0 && current.getDayOfWeek() < 6
//                    && current.getHourOfDay() >= 9 && current.getHourOfDay() < 15) {
//                method.invoke(null);
//                StockList.crawlerStockDetailList();
//            }
//            System.exit(0);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }



//    public static List<Stock> crawlerStockList() {
//
//        Document doc = null;
//        stockList = new ArrayList<Stock>();
//        try {
//            doc = Jsoup.connect("http://quote.eastmoney.com/stocklist.html").get();
//            Elements elements = doc.select(".qox .quotebody #quotesearch ul li a[target=\"_blank\"]");
//            for (Element element : elements) {
//                String[] content = element.text().replace("(", "\t").replace(")", "").split("\t");
//                String stockUrl = element.attr("href");
//                Stock stock = new Stock(content[1], content[0], stockUrl);
//                stockList.add(stock);
//            }
//            saveStockList(stockList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return stockList;
//    }



    public static List<Stock> crawlerStockList() {

        Document doc = null;
        stockList = new ArrayList<Stock>();
        try {
            doc = Jsoup.connect("http://quote.eastmoney.com/stocklist.html").get();
            Elements elements = doc.select(".qox .quotebody #quotesearch ul li a[target=\"_blank\"]");
            for (Element element : elements) {
                String[] content = element.text().replace("(", "\t").replace(")", "").split("\t");
                String stockUrl = element.attr("href");
                Stock stock = new Stock(content[1], content[0], stockUrl);
                stockList.add(stock);
            }
            saveStockList(stockList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockList;
    }






    public static void crawlerStockDetailList() throws InterruptedException {
        Document doc = null;
        String[] fields = null;
        try {
            doc = Jsoup.connect("http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/index.aspx?type=s&sortType=C&sortRule=-1&pageSize=2000&page=1&jsName=quote_123&style=10&token=44c9d251add88e27b65ed86506f6e5da&_g=0.10250614244953815").timeout(10000).get();
            Elements elements = doc.select("body");
            String content = elements.get(0).text();
            JSONObject body = new JSONObject(content.split("=")[1].trim());
            JSONArray detailLsit = body.getJSONArray("rank");
            Iterator<Object> iter = detailLsit.iterator();
            String line = null;
            while (iter.hasNext()) {
                line = iter.next().toString().replace(",", "\t");
                fields = line.split("\t");
                String currenDate = new DateTime().toString("yyyy-MM-dd");
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime crawlerTime = DateTime.parse(fields[28], formatter);

                if(crawlerTime.isAfter(DateTime.parse(currenDate + " 09:25:00", formatter))
                        && crawlerTime.isBefore(DateTime.parse(currenDate + " 15:00:00", formatter))
                        && crawlerTime.getDayOfWeek() > 0 && crawlerTime.getDayOfWeek() < 6) {
                    logger.info(line + "\tstock");
                    System.out.println(line);
                    Thread.sleep(10);
                } else {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void crawlerStockTradeList() {
        Document doc = null;
        String[] fields = null;
        try {
            doc = Jsoup.connect("http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKHY&type=ct&st=(BalFlowMain)&sr=-1&p=1&ps=70&js=var icZikhtg={pages:(pc),data:[(x)]}&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK&rt=48326838").timeout(10000).get();
            Elements elements = doc.select("body");
            String content = elements.get(0).text();
            JSONObject body = new JSONObject(content.split("=")[1].trim());
            JSONArray detailLsit = body.getJSONArray("data");
            Iterator<Object> iter = detailLsit.iterator();
            String line = null;
            while (iter.hasNext()) {
                line = iter.next().toString().replace(",", "\t");
                fields = line.split("\t");
                DateTime crawlerTime = DateTime.now();
                String currenDate = crawlerTime.toString("yyyy-MM-dd");
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                if(crawlerTime.isAfter(DateTime.parse(currenDate + " 09:25:00", formatter))
                        && crawlerTime.isBefore(DateTime.parse(currenDate + " 15:00:00", formatter))
                        && crawlerTime.getDayOfWeek() > 0 && crawlerTime.getDayOfWeek() < 6) {
                    logger.info(line + "\t" + crawlerTime.toString(formatter) + "\ttrade");
                    System.out.println(line);
                } else {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveStockList(List<Stock> stockList) {
        Connection con = null;
        try {
            Configuration.config();
            DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
            con = DBPoolContext.getInstance().open();
            con.setAutoCommit(false);
            DBUtils.saveStockList(stockList, con);
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.close(con);
        }
    }

    public static void crawlerStockDetail(Stock stock) {
        Document doc = null;
        String[] fields = null;
        String content = null;
        JSONArray detailLsit = null;
        String currenDate = new DateTime().toString("yyyyMMdd");
        try {
            doc = Jsoup.connect("http://q.stock.sohu.com/hisHq?code=cn_" + stock.getStockId() + "&start=" + currenDate + "&end=" + currenDate + "&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.8663577115432256&0.12397609584687685").ignoreContentType(true).timeout(10000).get();
//            doc = Jsoup.connect("http://q.stock.sohu.com/hisHq?code=cn_" + stock.getStockId() + "&start=20160620&end=20160620&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp&r=0.8663577115432256&0.12397609584687685").ignoreContentType(true).timeout(10000).get();
            Elements elements = doc.select("body");
            content = elements.get(0).text();
            if(!content.contains("hq")) return;
            content = content.substring(content.indexOf("(") + 1, content.lastIndexOf(")"));
            JSONArray body = new JSONArray(content.trim());
            JSONObject json = (JSONObject)body.get(0);
            if(json == null || json.getInt("status") != 0 || json.get("hq") == null) return;
            detailLsit = (JSONArray) json.getJSONArray("hq");
            Iterator<Object> iter = detailLsit.iterator();
            List<Quote> quotes = new ArrayList<Quote>();
            while (iter.hasNext()) {
                JSONArray list = (JSONArray) iter.next();
                String tradeDate = list.getString(0);
                String stockId = stock.getStockId();
                String stockName = stock.getStockName();
                double openPrice = NumberValidationUtils.isRealNumber(list.getString(1).replace("%", "")) ? Double.parseDouble(list.getString(1).replace("%", "")) : 0.00;
                double closePrice = NumberValidationUtils.isRealNumber(list.getString(2).replace("%", "")) ? Double.parseDouble(list.getString(2).replace("%", "")) : 0.00;
                double addPrice = NumberValidationUtils.isRealNumber(list.getString(3).replace("%", "")) ? Double.parseDouble(list.getString(3).replace("%", "")) : 0.00;
                double addRate = NumberValidationUtils.isRealNumber(list.getString(4).replace("%", "")) ? Double.parseDouble(list.getString(4).replace("%", "")) : 0.00;
                double minPrice = NumberValidationUtils.isRealNumber(list.getString(5).replace("%", "")) ? Double.parseDouble(list.getString(5).replace("%", "")) : 0.00;
                double maxPrice = NumberValidationUtils.isRealNumber(list.getString(6).replace("%", "")) ? Double.parseDouble(list.getString(6).replace("%", "")) : 0.00;
                double volume = NumberValidationUtils.isRealNumber(list.getString(7).replace("%", "")) ? Double.parseDouble(list.getString(7).replace("%", "")) : 0.00;
                double turnover = NumberValidationUtils.isRealNumber(list.getString(8).replace("%", "")) ? Double.parseDouble(list.getString(8).replace("%", "")) : 0.00;
                double turnoverRate = NumberValidationUtils.isRealNumber(list.getString(9).replace("%", "")) ? Double.parseDouble(list.getString(9).replace("%", "")) : 0.00;

                Quote quote = new Quote(tradeDate, stockId, stockName, openPrice, closePrice, addPrice, addRate, minPrice, maxPrice, volume, turnover, turnoverRate);
                quotes.add(quote);
                System.out.println(quote);
            }
            saveQuoteList(quotes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void saveQuoteList(List<Quote> quotes) {
        Connection con = null;
        try {
            Configuration.config();
            DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
            con = DBPoolContext.getInstance().open();
            con.setAutoCommit(false);
            DBUtils.saveQuoteList(quotes, con);
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.close(con);
        }
    }

}
