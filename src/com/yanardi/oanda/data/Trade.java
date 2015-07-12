package com.yanardi.oanda.data;

import java.util.Date;

public class Trade {

    private long id;
    private int units;
    private OrderSide side;
    private String instrument;
    private Date time;
    private double price;
    private double takeProfit;
    private double stopLoss;
    private double trailingStop;
    private double trailingAmount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

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

    public double getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(double takeProfit) {
        this.takeProfit = takeProfit;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public double getTrailingStop() {
        return trailingStop;
    }

    public void setTrailingStop(double trailingStop) {
        this.trailingStop = trailingStop;
    }

    public double getTrailingAmount() {
        return trailingAmount;
    }

    public void setTrailingAmount(double trailingAmount) {
        this.trailingAmount = trailingAmount;
    }
}
