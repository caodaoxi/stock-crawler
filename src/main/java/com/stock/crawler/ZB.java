package com.stock.crawler;

import com.stock.crawler.config.Configuration;
import com.stock.crawler.utils.DBPoolContext;
import com.stock.crawler.utils.DBUtils;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by caodaoxi on 16-6-16.
 */
public class ZB {

    public static void main(String[] args) {
        Connection con = null;
        try {
            Configuration.config();
            DBPoolContext.getInstance().init(Configuration.JDBCURL, Configuration.JDBCPASSWORD, Configuration.JDBCUSERNAME);
            con = DBPoolContext.getInstance().open();
            List<Stock> stocks = StockList.crawlerStockList();
            for(Stock stock : stocks) {
                if (stock.getStockId().startsWith("30") || stock.getStockId().startsWith("60")  || stock.getStockId().startsWith("00")) {
                    List<JSONObject> quotes = DBUtils.getQuoteByStockId(stock.getStockId(), 100000, con);
                    ENE ene = new ENE();
                    ene.getENE(quotes, 10, 11, 9);
                    for (JSONObject quote : quotes) {
                        if("2016-06-17".equals(quote.getString("tradeDate")) && quote.getDouble("closePrice") <= quote.getDouble("LOWER")) {
                            System.out.println(quote.toString());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.close(con);
        }
    }


}
