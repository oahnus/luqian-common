package com.github.oahnus.luqiancommon.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by oahnus on 2017/4/16
 * 21:30.
 */
@Slf4j
public class ApacheHttp {

    private static ExecutorService threadPool = new ThreadPoolExecutor(10, 20, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));

    private static PoolingHttpClientConnectionManager poolManager = null;
    private static RequestConfig requestConfig;

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
        return HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(poolManager)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36  ")
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .setConnectionManagerShared(true)
                .build();
    }

    public static CloseableHttpClient getClient(String proxyUrl) {
        return HttpClients.custom()
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

    public static void download(String url, String savePath, String filename) throws IOException {
        String fullPath;
        if (filename == null || filename.trim().equals("")) {
            filename = getFilenameFromUrl(url);
        }
        if (savePath == null || savePath.trim().equals("")) {
            fullPath = filename;
        } else {
            File path = new File(savePath);
            if (!path.exists()) {
                boolean res = path.mkdirs();
            }
            if (savePath.endsWith("/")) {
                fullPath = savePath + filename;
            } else {
                fullPath = savePath + "/" + filename;
            }
        }
        FileOutputStream fos = new FileOutputStream(fullPath);
        download(url, fos);
    }

    public static void download(String url, String savePath) throws IOException {
        download(url, savePath, "");
    }

    public static void download(String url, OutputStream out) throws IOException {
        CloseableHttpClient client = getClient();
        download(client, url, out);
    }

    public static void download(CloseableHttpClient client, String url, OutputStream out) throws IOException {
        HttpHead httpHead = new HttpHead(url);
        CloseableHttpResponse headResp = client.execute(httpHead);

        Header[] headers = headResp.getAllHeaders();
        int byteLen = 0;
        String contentType = "";
        for (Header header : headers) {
            String name = header.getName();
            if (name.equals("Content-Length")) {
                String value = header.getValue();
                byteLen = Integer.valueOf(value);
                break;
            }
        }

        int sliceNum = 1;
        // 文件大小大于1M
        if (byteLen > 1024 * 1024) {
            sliceNum = 4;
        }

        int statusCode = headResp.getStatusLine().getStatusCode();
        if (statusCode == 302 || statusCode == 405) {
            // 如果重定向, 放弃切片
            sliceNum = 1;
            log.info("Cannot Get Total Bytes From Head Request");
        } else if (statusCode != 200) {
            throw new RuntimeException("Http Error With Code " + statusCode);
        } else {
            log.info("Resource Total Bytes {}", byteLen);
        }

        final String downloadUrl = url;
        if (sliceNum == 1) {
            downloadDirect(downloadUrl, out);
        } else {
            CountDownLatch cdl = new CountDownLatch(sliceNum);
            AtomicInteger idx = new AtomicInteger();

            final int unit = byteLen / sliceNum;
            final int max = byteLen;
            final int maxSliceIdx = sliceNum;

            log.info("Total Slice Num {}", sliceNum);
            ByteArrayOutputStream[] buffers = new ByteArrayOutputStream[sliceNum];
            AtomicBoolean isError = new AtomicBoolean();
            for (int i=0; i < sliceNum; i++) {
                Future<?> future = threadPool.submit(() -> {
                    int startIdx = idx.getAndIncrement();
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    log.info("Slice {} Start", startIdx);
                    int startByte = startIdx * unit;
                    int endByte = startByte + unit - 1;
                    if (startIdx == maxSliceIdx - 1) {
                        endByte = max - 1;
                    }
                    try {
                        downlaodRange(downloadUrl, startByte, endByte, bao);
                    } catch (IOException e) {
                        log.error("Slice {} Error With Message {}", startIdx, e.getMessage());
                        cdl.countDown();
                        isError.compareAndSet(false, true);
                        return;
                    }
                    buffers[startIdx] = bao;
                    cdl.countDown();
                    log.info("Slice {} Finish", startIdx);
                });
            }

            try {
                cdl.await();
            } catch (InterruptedException e) {
                return;
            }
            if (isError.get()) {
                throw new RuntimeException("Downlaod Error");
            }

            for (ByteArrayOutputStream buffer : buffers) {
                IOUtils.write(buffer.toByteArray(), out);
                buffer.close();
            }
            out.flush();
            out.close();
        }
    }

    private static void downlaodRange(String url, int start, int end, OutputStream out) throws IOException {
        CloseableHttpClient client = getClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Range", String.format("bytes=%d-%d", start, end));

        CloseableHttpResponse response = client.execute(httpGet);
        InputStream content = response.getEntity().getContent();
        IOUtils.copy(content, out);
    }

    private static void downloadDirect(String url, OutputStream out) throws IOException {
        CloseableHttpClient client = getClient();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = client.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("Http Error With Code " + statusCode);
        }
        Header contentLengthHeader = response.getFirstHeader("Content-Length");
        log.info("File Total Bytes {}", contentLengthHeader.getValue());

        InputStream content = response.getEntity().getContent();
        IOUtils.copy(content, out);
    }

    private static String getFilenameFromUrl(String url) {
        String filename = url;
        int quesMarkIdx = url.lastIndexOf("?");
        if (quesMarkIdx > 0) {
            filename = url.substring(0, quesMarkIdx);
        }
        int speIdx = url.lastIndexOf("/");
        if (speIdx > 0) {
            filename = filename.substring(speIdx + 1);
        }
        return filename;
    }

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("start");

        String url = "https://cdn.spacetelescope.org/archives/images/publicationjpg/heic1909a.jpg";
        try{
            download(url, "C:/D/download2/");
        } catch (Exception e) {

        } finally {
            System.out.println("Runtime " + (System.currentTimeMillis() - startTime));
            threadPool.shutdown();
        }
    }
}
