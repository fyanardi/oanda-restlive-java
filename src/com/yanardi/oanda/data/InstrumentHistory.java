package com.yanardi.oanda.data;

import java.util.List;

public class InstrumentHistory {

    private String instrument;
    private Granularity granularity;
    private List<Candle> candles;

    public String getInstrument() {
        return instrument;
    }
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
    public Granularity getGranularity() {
        return granularity;
    }
    public void setGranularity(Granularity granularity) {
        this.granularity = granularity;
    }
    public List<Candle> getCandles() {
        return candles;
    }
    public void setCandles(List<Candle> candles) {
        this.candles = candles;
    }
}
