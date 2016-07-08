package com.jzsec.crawler.job;

import com.espertech.esper.client.EPRuntime;
import com.stock.quota.Core;
import com.stock.quota.utils.DBUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.quartz.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by caodaoxi on 16-7-5.
 */
public class CrawlerJob implements Job {

    private EPRuntime epRuntime;

    public void crawlerStockDetailList() throws InterruptedException {
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
            Core core = new Core();
            while (iter.hasNext()) {

                line = iter.next().toString().replace(",", "\t");
                fields = line.split("\t");
                String currenDate = new DateTime().toString("yyyy-MM-dd");
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime crawlerTime = DateTime.parse(fields[28], formatter);

                if(crawlerTime.isAfter(DateTime.parse(currenDate + " 09:25:00", formatter))
                        && crawlerTime.isBefore(DateTime.parse(currenDate + " 20:00:00", formatter))
                        && crawlerTime.getDayOfWeek() > 0 && crawlerTime.getDayOfWeek() < 6) {
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
                    core.getAllQuota(jsonObject);
                    Map<String, Object> quoteMap = jsonToMap(jsonObject);
                    epRuntime.sendEvent(quoteMap, "Quote");
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


    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            if(epRuntime == null) {
                epRuntime = (EPRuntime) context.getJobDetail().getJobDataMap().get("epRuntime");
            }
            crawlerStockDetailList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> jsonToMap(JSONObject jsonObject ) throws JSONException {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Iterator<String> iter = jsonObject.keys();
        String key=null;
        Object value=null;
        while (iter.hasNext()) {
            key=iter.next();
            value=jsonObject.get(key);
            resultMap.put(key, value);
        }
        return resultMap;
    }
}
