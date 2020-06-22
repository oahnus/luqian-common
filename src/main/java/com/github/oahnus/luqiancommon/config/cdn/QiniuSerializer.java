package com.github.oahnus.luqiancommon.config.cdn;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.oahnus.luqiancommon.util.QiniuUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created by oahnus on 2020-03-31
 * 18:22.
 * 七牛云私有空间url Json序列化
 */
public class QiniuSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (StringUtils.isEmpty(s)) {
            jsonGenerator.writeString(s);
            return;
        }
        // 没有配置私有空间域名，直接返回原始字符串
        String urlPrefix = QiniuUtils.urlPrefix();
        if (StringUtils.isEmpty(urlPrefix)) {
            jsonGenerator.writeString(s);
            return;
        }
        String wrappedStr;
        if (s.startsWith("[") && s.endsWith("]")) {
            // 处理json url数组
            wrappedStr = s.replace("[", "").replace("]", "");
            String[] urls = wrappedStr.split(",");
            for (int i = 0; i < urls.length; i++) {
                urls[i] = QiniuUtils.buildAccessSign(urls[i]);
            }
            wrappedStr = "[" + String.join(",", urls) + "]";
        } else {
            // 普通url
            wrappedStr = QiniuUtils.buildAccessSign(s);
        }
        jsonGenerator.writeString(wrappedStr);
    }
}
