package com.stock.quota;

/**
 * Created by caodaoxi on 16-6-8.
 */
public class Quote {
    private String tradeDate;
    private String stockId;
    private String stockName;
    private double openPrice;
    private double closePrice;
    private double addPrice;
    private double addRate;
    private double minPrice;
    private double maxPrice;
    private double volume;
    private double turnover;
    private double turnoverRate;

    public Quote() {
    }

    public Quote(String tradeDate, String stockId, String stockName, double openPrice, double closePrice, double addPrice, double addRate, double minPrice, double maxPrice, double volume, double turnover, double turnoverRate) {
        this.tradeDate = tradeDate;
        this.stockId = stockId;
        this.stockName = stockName;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.addPrice = addPrice;
        this.addRate = addRate;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.volume = volume;
        this.turnover = turnover;
        this.turnoverRate = turnoverRate;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getAddPrice() {
        return addPrice;
    }

    public void setAddPrice(double addPrice) {
        this.addPrice = addPrice;
    }

    public double getAddRate() {
        return addRate;
    }

    public void setAddRate(double addRate) {
        this.addRate = addRate;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getTurnover() {
        return turnover;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public double getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(double turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "tradeDate='" + tradeDate + '\'' +
                ", stockId='" + stockId + '\'' +
                ", openPrice=" + openPrice +
                ", closePrice=" + closePrice +
                ", addPrice=" + addPrice +
                ", addRate=" + addRate +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", volume=" + volume +
                ", turnover=" + turnover +
                ", turnoverRate=" + turnoverRate +
                '}';
    }
}
