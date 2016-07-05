package com.stock.crawler;

import com.stock.crawler.config.Configuration;
import com.stock.crawler.utils.DBPoolContext;
import com.stock.crawler.utils.DBUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by caodaoxi on 16-7-5.
 */
public class Crawler {

    public static void main(String[] args) throws InterruptedException {
        crawlerStockDetailList();
    }
    public static void crawlerStockDetailList() throws InterruptedException {
        Document doc = null;
        String[] fields = null;
        try {
//            doc = Jsoup.connect("http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/index.aspx?type=s&sortType=C&sortRule=-1&pageSize=4000&page=1&jsName=quote_123&style=10&token=44c9d251add88e27b65ed86506f6e5da&_g=0.10250614244953815").timeout(10000).get();

            doc = Jsoup.connect("http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/index.aspx?type=s&sortType=C&sortRule=-1&pageSize=4000&page=1&jsName=quote_123&style=33&token=44c9d251add88e27b65ed86506f6e5da&_g=0.10250614244953815").timeout(10000).get();
            Elements elements = doc.select("body");
            String content = elements.get(0).text();
            JSONObject body = new JSONObject(content.split("=")[1].trim());
            JSONArray detailLsit = body.getJSONArray("rank");
            Iterator<Object> iter = detailLsit.iterator();
            String line = null;
            int i = 0;
            JSONObject jsonObject = null;
            Connection con = getConnection();
            MACD macd = new MACD(con);
            KDJ kdj = new KDJ(con);
            ENE ene = new ENE(con);
            CCI cci = new CCI(con);
            while (iter.hasNext()) {

                line = iter.next().toString().replace(",", "\t");
                fields = line.split("\t");
                String currenDate = new DateTime().toString("yyyy-MM-dd");
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime crawlerTime = DateTime.parse(fields[28], formatter);

                if(crawlerTime.isAfter(DateTime.parse(currenDate + " 09:25:00", formatter))
                        && crawlerTime.isBefore(DateTime.parse(currenDate + " 20:00:00", formatter))
                        && crawlerTime.getDayOfWeek() > 0 && crawlerTime.getDayOfWeek() < 6) {
//                    logger.info(line + "\tstock");
//                    if(!"600030".equals(fields[1])) continue;
                    jsonObject = new JSONObject();
                    jsonObject.put("stockId", fields[1]);
                    jsonObject.put("tradeDate", fields[28]);
                    jsonObject.put("stockName", fields[2]);
                    jsonObject.put("openPrice", Double.parseDouble(fields[4]));
                    jsonObject.put("closePrice", Double.parseDouble(fields[5]));
                    jsonObject.put("maxPrice", Double.parseDouble(fields[6]));
                    jsonObject.put("minPrice", Double.parseDouble(fields[7]));

                    jsonObject.put("addPrice", Double.parseDouble(fields[10]));
                    jsonObject.put("addRate", Double.parseDouble(fields[11].replace("%", "")));
                    jsonObject.put("volume", Double.parseDouble(fields[9]));
                    jsonObject.put("turnover", Double.parseDouble(fields[8]));
                    jsonObject.put("turnoverRate", Double.parseDouble(fields[23].replace("%", "")));
                    if(Double.parseDouble(fields[9]) == 0) continue;
                    i++;
                    macd.getTodayMACD(jsonObject);
                    kdj.getTodayKDJ(jsonObject);
                    ene.getTodayENE(jsonObject);
                    cci.getTodayCCI(jsonObject);
                    System.out.println(jsonObject.toString());
//                    Thread.sleep(10);
                } else {
                    System.exit(0);
                }
            }
            System.out.println(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Connection getConnection() {
        Connection con = null;
        try {
            Configuration.config();
            DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
            con = DBPoolContext.getInstance().open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
