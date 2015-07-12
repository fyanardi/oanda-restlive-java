package com.yanardi.oanda.data;

import java.util.Map;

public class Instrument {

    public static class InterestRate {
        double bid;
        double ask;
    }

    private String instrument;
    private String displayName;
    private String pip;
    private long maxTradeUnits;
    private String precision;
    private int maxTrailingStop;
    private double marginRate;
    private boolean halted;
    private Map<String, InterestRate> interestRate;

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPip() {
        return pip;
    }

    public void setPip(String pip) {
        this.pip = pip;
    }

    public long getMaxTradeUnits() {
        return maxTradeUnits;
    }

    public void setMaxTradeUnits(long maxTradeUnits) {
        this.maxTradeUnits = maxTradeUnits;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public int getMaxTrailingStop() {
        return maxTrailingStop;
    }

    public void setMaxTrailingStop(int maxTrailingStop) {
        this.maxTrailingStop = maxTrailingStop;
    }

    public double getMarginRate() {
        return marginRate;
    }

    public void setMarginRate(double marginRate) {
        this.marginRate = marginRate;
    }

    public boolean isHalted() {
        return halted;
    }

    public void setHalted(boolean halted) {
        this.halted = halted;
    }

    public Map<String, InterestRate> getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Map<String, InterestRate> interestRate) {
        this.interestRate = interestRate;
    }
}
