package com.github.oahnus.luqiancommon.config.cdn;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.oahnus.luqiancommon.util.SpringAppContextUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.Wrapper;

/**
 * Created by oahnus on 2020-04-01
 * 7:16.
 * 七牛云私有空间url Json反序列化
 */
public class QiniuDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String jsonStr = jsonParser.getValueAsString();

        if (StringUtils.isEmpty(jsonStr)) {
            return jsonStr;
        }

        QiniuClient qiniuClient = SpringAppContextUtils.getBean(QiniuClient.class);
        String urlPrefix = qiniuClient.getUrlPrefix();
        if (StringUtils.isEmpty(urlPrefix)) {
            return jsonStr;
        }

        if (jsonStr.startsWith("[") && jsonStr.endsWith("]")) {
            String wrappedStr = jsonStr
                    .replace("[", "")
                    .replace("]", "");
            String[] urls = wrappedStr.split(",");
            for (int i = 0; i < urls.length; i++) {
                urls[i] = clearUrlHostAndToken(urlPrefix, urls[i]);
            }
            return "[" + String.join(",", urls) + "]";
        }
        return clearUrlHostAndToken(urlPrefix, jsonStr);
    }

    private String clearUrlHostAndToken(String urlPrefix, String originUrl) {
        String url = originUrl.replace(urlPrefix, "");
        url = url.substring(0, url.indexOf("?"));
        return url;
    }
}
