package com.yanardi.oanda.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Type Adapter factory for Enum to read values in lower camel case into Enum constants in upper underscore
 *
 * @author Fredy Yanardi
 *
 */
public class EnumTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!type.getRawType().isEnum()) return null;

        final Class<T> rawType = (Class<T>) type.getRawType();
        final Map<String, T> constantMap = new HashMap<String, T>();
        for (T constant : rawType.getEnumConstants()) {
            constantMap.put(constant.toString(), constant);
        }

        return new TypeAdapter<T>() {
            @Override
            public T read(JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                }
                else {
                    String value = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, reader.nextString());
                    return constantMap.get(value);
                }
            }

            @Override
            public void write(JsonWriter writer, T value) throws IOException {
                if (value == null) {
                    writer.nullValue();
                }
                else {
                    writer.value(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value.toString()));
                }
            }
        };
    }

}
