package com.yanardi.oanda.data;

import java.util.Date;

public class NewTrade {
    public static class OrderOpened {
        long id;
        int units;
        OrderSide side;
        double takeProfit;
        double stopLoss;
        Date expiry;
        double upperBound;
        double lowerBound;
        double trailingStop;
    }

    public class TradeOpened {
        long id;
        int units;
        OrderSide side;
        double takeProfit;
        double stopLoss;
        double trailingStop;
    }

    private String instrument;
    private Date time;
    private double price;
    private OrderOpened orderOpened;
    private TradeOpened tradeOpened;

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderOpened getOrderOpened() {
        return orderOpened;
    }

    public void setOrderOpened(OrderOpened orderOpened) {
        this.orderOpened = orderOpened;
    }

    public TradeOpened getTradeOpened() {
        return tradeOpened;
    }

    public void setTradeOpened(TradeOpened tradeOpened) {
        this.tradeOpened = tradeOpened;
    }
}
