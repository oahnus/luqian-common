package com.github.oahnus.luqiancommon.util;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by oahnus on 2017/4/16
 * 21:30.
 */
public class ApacheHttp {

    static PoolingHttpClientConnectionManager poolManager = null;
    static CloseableHttpClient httpClient;
    static RequestConfig requestConfig;

    static {
        try {
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContextBuilder.build());
            // 配置 同时支持http https
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();
            // 初始化链接管理器
            poolManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 最大连接数
            poolManager.setMaxTotal(50);
            // 最大路由
            poolManager.setDefaultMaxPerRoute(poolManager.getMaxTotal());
//            int socketTimeout = 5000;
//            int connectTimeout = 5000;
//            int connectionRequestTimeout = 5000;
            int socketTimeout = 60000;
            int connectTimeout = 60000;
            int connectionRequestTimeout = 60000;
            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(poolManager)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36  ")
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
//        setConnectionManagerShared
                .setConnectionManagerShared(true)
                .build();

        if (poolManager != null && poolManager.getTotalStats() != null) {
//            System.out.println("now client pool "
//                    + poolManager.getTotalStats().toString());
        }

        return httpClient;
    }

    public static CloseableHttpClient getClient(String proxyUrl) {
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(poolManager)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                .setProxy(HttpHost.create(proxyUrl))
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36  ")
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
//        setConnectionManagerShared
                .setConnectionManagerShared(true)
                .build();

        if (poolManager != null && poolManager.getTotalStats() != null) {
//            System.out.println("now client pool "
//                    + poolManager.getTotalStats().toString());
        }

        return httpClient;
    }

    public static String requestWebPage(String url) throws IOException{
        CloseableHttpClient client = getClient();
        return requestWebPage(client, url, null);
    }

    public static String requestWebPage(String url, Map<String, String> headers) throws IOException{
        CloseableHttpClient client = getClient();
        return requestWebPage(client, url, headers);
    }

    public static String requestWebPage(String url, String proxyUrl) throws IOException{
        CloseableHttpClient client = getClient(proxyUrl);
        return requestWebPage(client, url, null);
    }

    public static String requestWebPage(String url, String proxyUrl, Map<String, String> headers) throws IOException{
        CloseableHttpClient client = getClient(proxyUrl);
        return requestWebPage(client, url, headers);
    }

    public static String requestWebPage(CloseableHttpClient httpClient, String url, Map<String, String> headers) throws IOException{
        HttpGet httpGet = new HttpGet(url);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        Header[] allHeaders = httpGet.getAllHeaders();
        System.out.println(Arrays.toString(allHeaders));
        CloseableHttpResponse response = httpClient.execute(httpGet);
        InputStream inputStream = response.getEntity().getContent();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String respBody = reader.lines().reduce("", (sum, line) -> sum += line);
        response.close();
        httpGet.releaseConnection();

        return respBody;
    }

    public static void saveImgUrl(CloseableHttpClient httpClient, String imgUrl, OutputStream out) throws IOException {
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(imgUrl);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                System.out.println("Img 请求失败");
                return;
            }

            InputStream inputStream = response.getEntity().getContent();
//            System.out.println(inputStream.available());

            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                out.write(buffer, 0, n);
            }
            out.flush();
            out.close();
            response.close();
        } catch (Exception e) {
            throw e;
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
    }

    public static void saveImgUrl(String imgUrl, OutputStream out) throws IOException {
        CloseableHttpClient httpClient = getClient();
        saveImgUrl(httpClient, imgUrl, out);
    }

    public static void saveImgUrl(String proxyUrl, String imgUrl, OutputStream out) throws IOException {
        CloseableHttpClient httpClient = getClient(proxyUrl);
        saveImgUrl(httpClient, imgUrl, out);
    }
}
