package com.github.oahnus.luqiancommon.generate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by oahnus on 2020-05-08
 * 13:54.
 * 雪花算法生成的id 如果字符串存储Long类型
 * 在前台显示时如果id位数较大(接近20位), id会发生精度丢失
 */
public class SnowIdJsonSerializer extends JsonSerializer<Long> {
    @Override
    public void serialize(Long id, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(String.valueOf(id));
    }
}
