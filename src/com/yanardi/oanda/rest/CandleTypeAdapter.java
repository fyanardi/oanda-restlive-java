package com.yanardi.oanda.rest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.yanardi.oanda.data.BidAskCandle;
import com.yanardi.oanda.data.Candle;
import com.yanardi.oanda.data.MidPointCandle;

/**
 * A type adapter to automatically deduce the candle type based on the JSON's attributes
 * and then instantiates the correct class type (MidPointCandle or BidAskCandle) 
 *
 * @author Fredy Yanardi
 *
 */
public class CandleTypeAdapter extends TypeAdapter<Candle> {

    private static Map<String, Field> midPointFields = new HashMap<String, Field>();
    private static Map<String, Field> bidAskFields = new HashMap<String, Field>();

    static {
        for (Field field : MidPointCandle.class.getDeclaredFields()) {
            midPointFields.put(field.getName(), field);
        }
        for (Field field : BidAskCandle.class.getDeclaredFields()) {
            bidAskFields.put(field.getName(), field);
        }
    }

    private TypeAdapter<Candle> delegate;

    public CandleTypeAdapter(TypeAdapter<Candle> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Candle read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        Map<String, Double> midPointValues = new HashMap<String, Double>();
        Map<String, Double> bidAskValues = new HashMap<String, Double>();

        Date time = null;
        int volume = 0;
        boolean complete = false;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (midPointFields.keySet().contains(name)) {
                midPointValues.put(name, reader.nextDouble());
            }
            else if (bidAskFields.keySet().contains(name)) {
                bidAskValues.put(name, reader.nextDouble());
            }
            else { // common Candle attributes
                switch (name) {
                case "time":
                    try {
                        time = OANDARestAPI.RFC3339_SDF.parse(reader.nextString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "volume":
                    volume = reader.nextInt();
                    break;
                case "complete":
                    complete = reader.nextBoolean();
                    break;
                }
            }
        }
        reader.endObject();

        Candle candle = null;
        if (!midPointValues.isEmpty()) {
            candle = new MidPointCandle();
            injectFields(candle, midPointFields, midPointValues);
        }
        else {
            candle = new BidAskCandle();
            injectFields(candle, bidAskFields, bidAskValues);
        }

        candle.setTime(time);
        candle.setVolume(volume);
        candle.setComplete(complete);

        return candle;
    }

    @Override
    public void write(JsonWriter writer, Candle value) throws IOException {
        try {
            delegate.write(writer, value);
        }
        catch (IOException e) {
            delegate.write(writer, null);
        }
    }

    private void injectFields(Candle candle, Map<String, Field> fields, Map<String, Double> values) {
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            Field field = fields.get(entry.getKey());
            field.setAccessible(true);
            try {
                field.set(candle, entry.getValue());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
