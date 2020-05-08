package com.github.oahnus.luqiancommon.generate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Created by oahnus on 2020-05-08
 * 13:56.
 */
public class SnowIdJsonDeserializer extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String idStr = jsonParser.getValueAsString();
        try {
            return Long.valueOf(idStr);
        } catch (Exception e) {
            return null;
        }
    }
}
