package com.yanardi.oanda.rest;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.yanardi.oanda.data.Candle;

/**
 * Type Adapter factory for CandleTypeAdapter
 *
 * @author Fredy Yanardi
 *
 */
public class CandleTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Candle.class.isAssignableFrom(type.getRawType())) return null;

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return (TypeAdapter<T>) new CandleTypeAdapter((TypeAdapter<Candle>) delegate);
    }

}
