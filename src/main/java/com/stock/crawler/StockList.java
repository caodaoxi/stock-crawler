package com.stock.crawler;

import com.stock.crawler.config.Configuration;
import com.stock.crawler.utils.DBPoolContext;
import com.stock.crawler.utils.DBUtils;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caodaoxi
 * Date: 15-12-9
 * Time: 下午5:36
 * To DO: do nothing
 **/
public class StockList {
    private static final Logger logger = Logger.getLogger(StockList.class);
    public static void main(String[] args) {
        String methodName = args.length > 0 ? args[0] : "crawlerStockDetailList";
        try {
            Method method = StockList.class.getMethod(methodName);
            DateTime current = DateTime.now();
            if(current.getDayOfWeek() > 0 && current.getDayOfWeek() < 6
                    && current.getHourOfDay() >= 9 && current.getHourOfDay() < 15) {
                method.invoke(null);
            }
            System.exit(0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static List<Stock> crawlerStockList() {

        Document doc = null;
        List<Stock> stockList = new ArrayList<Stock>();
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
                    logger.info(line + "\ttrade");
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
}
