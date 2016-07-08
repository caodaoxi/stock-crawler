package com.stock.quota;

/**
 * Created by IntelliJ IDEA.
 * User: caodaoxi
 * Date: 15-12-9
 * Time: 下午5:42
 * To DO: do nothing
 **/
public class Stock {
    private long id;
    private String stockId;
    private String stockName;
    private String stockUrl;

    public Stock() {
    }

    public Stock(String stockId, String stockName, String stockUrl) {
        this.stockId = stockId;
        this.stockName = stockName;
        this.stockUrl = stockUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockUrl() {
        return stockUrl;
    }

    public void setStockUrl(String stockUrl) {
        this.stockUrl = stockUrl;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", stockId='" + stockId + '\'' +
                ", stockName='" + stockName + '\'' +
                ", stockUrl='" + stockUrl + '\'' +
                '}';
    }
}
