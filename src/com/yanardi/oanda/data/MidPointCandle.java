package com.yanardi.oanda.data;

public class MidPointCandle extends Candle {

    private double openMid;
    private double highMid;
    private double lowMid;
    private double closeMid;

    public double getOpenMid() {
        return openMid;
    }

    public void setOpenMid(double openMid) {
        this.openMid = openMid;
    }

    public double getHighMid() {
        return highMid;
    }

    public void setHighMid(double highMid) {
        this.highMid = highMid;
    }

    public double getLowMid() {
        return lowMid;
    }

    public void setLowMid(double lowMid) {
        this.lowMid = lowMid;
    }

    public double getCloseMid() {
        return closeMid;
    }

    public void setCloseMid(double closeMid) {
        this.closeMid = closeMid;
    }

}
