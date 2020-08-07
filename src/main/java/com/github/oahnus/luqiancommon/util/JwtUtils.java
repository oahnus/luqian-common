package com.github.oahnus.luqiancommon.util;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2020-05-28
 * 13:33.
 */
public class JwtUtils {
    public static final long DEFAULT_EXPIRE = 24 * 3600 * 1000; // 1 day
    private static final String PAYLOAD_NAME = "payload";
    private static String secret = "7fh#(4k732v;skc2";

    public static void init(String initSecret) {
        secret = initSecret;
    }

    private static SecretKey getKey() {
        byte[] encodedKey = Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    public static String sign(Map<String, Object> payload, long delta, TimeUnit timeUnit) {
        JwtBuilder builder = Jwts.builder();
        if (payload != null) {
            builder.addClaims(payload);
        }

        Date expire = new Date(System.currentTimeMillis() + timeUnit.toMillis(delta));
        return builder.setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, getKey())
                .compact();
    }

    public static String sign(Map<String, Object> payload) {
        return sign(payload, DEFAULT_EXPIRE, TimeUnit.MILLISECONDS);
    }

    public static String signSinglePayload(String payload, long delta, TimeUnit timeUnit) {
        JwtBuilder builder = Jwts.builder();
        if (payload != null && !payload.equals("")) {
            builder.claim(PAYLOAD_NAME, payload);
        }

        Date expire = new Date(System.currentTimeMillis() + timeUnit.toMillis(delta));
        return builder.setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, getKey())
                .compact();
    }

    public static String signSinglePayload(String payload) {
        return signSinglePayload(payload, DEFAULT_EXPIRE, TimeUnit.MILLISECONDS);
    }

    public static String parseSinglePayload(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getKey())
                    .parseClaimsJws(token)
                    .getBody();
            return (String) claims.get(PAYLOAD_NAME);
        } catch (JwtException e) {
            return null;
        }
    }

    public static boolean valid(String token) {
        Map<String, Object> map = parse(token);
        return map != null && map.get("exp") != null;
    }

    public static Map<String, Object> parse(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return null;
        }
    }
}
