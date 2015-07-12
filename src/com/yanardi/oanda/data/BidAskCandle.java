package com.yanardi.oanda.data;

public class BidAskCandle extends Candle {
    private double openBid;
    private double openAsk;
    private double highBid;
    private double highAsk;
    private double lowBid;
    private double lowAsk;
    private double closeBid;
    private double closeAsk;

    public double getOpenBid() {
        return openBid;
    }

    public void setOpenBid(double openBid) {
        this.openBid = openBid;
    }

    public double getOpenAsk() {
        return openAsk;
    }

    public void setOpenAsk(double openAsk) {
        this.openAsk = openAsk;
    }

    public double getHighBid() {
        return highBid;
    }

    public void setHighBid(double highBid) {
        this.highBid = highBid;
    }

    public double getHighAsk() {
        return highAsk;
    }

    public void setHighAsk(double highAsk) {
        this.highAsk = highAsk;
    }

    public double getLowBid() {
        return lowBid;
    }

    public void setLowBid(double lowBid) {
        this.lowBid = lowBid;
    }

    public double getLowAsk() {
        return lowAsk;
    }

    public void setLowAsk(double lowAsk) {
        this.lowAsk = lowAsk;
    }

    public double getCloseBid() {
        return closeBid;
    }

    public void setCloseBid(double closeBid) {
        this.closeBid = closeBid;
    }

    public double getCloseAsk() {
        return closeAsk;
    }

    public void setCloseAsk(double closeAsk) {
        this.closeAsk = closeAsk;
    }
}
