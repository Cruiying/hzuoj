package com.hqz.hzuoj.util.util;

import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Houjie
 * Http请求工具类
 */
public class HttpUtil {

    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);
    private static RequestConfig requestConfig;
    private static PoolingHttpClientConnectionManager cm;

    static {
        SSLContext sslcontext = null;
        try {
            sslcontext = createIgnoreVerifySSL();
        } catch (Exception e) {
            log.error("error", e);
        }
        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext)).build();

        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 将最大连接数增加
        cm.setMaxTotal(1000);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(200);
        requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000)
                .setConnectionRequestTimeout(30000).build();
    }

    /**
     * 绕过验证
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    public static CloseableHttpClient getHttpClient() throws IOException {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }

    /**
     * 请求参数为Map
     */
    public static JSONObject httpPost(String url, Map<String, String> params, String encode, Map<String, String> headers) {
        JSONObject json = new JSONObject();
        // set encode
        if (StringUtils.isEmpty(encode)) {
            encode = "UTF-8";
        }
        HttpPost httpPost = new HttpPost(url);
        long currentTimeMillis = System.currentTimeMillis();
        try {
            // add headers
            if (headers != null) {
                boolean hasContentType = false;
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                    if ("Content-Type".equals(entry.getKey())) {
                        hasContentType = true;
                    }
                }
                if (!hasContentType) {
                    httpPost.setHeader("Content-Type", "application/json");
                }
            }
            // add parameters
            if (params != null) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for (Entry<String, String> entry : params.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encode);
                httpPost.setEntity(entity);
            }
            log.info("HttpUtil_first_try_{}", currentTimeMillis);
            CloseableHttpResponse response = getHttpClient().execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String str = null;
            if (httpEntity != null) {
                str = EntityUtils.toString(httpEntity, encode);
            }
            json.put("result", str);
            json.put("resultCode", 1);
        } catch (IOException ex1) {
            try {
                Thread.sleep(1000);
                log.info("HttpUtil_second_try_{}", currentTimeMillis);
                CloseableHttpResponse response = getHttpClient().execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                String str = null;
                if (httpEntity != null) {
                    str = EntityUtils.toString(httpEntity, encode);
                }
                json.put("result", str);
                json.put("resultCode", 1);
            } catch (IOException ex2) {
                try {
                    Thread.sleep(1000);
                    log.info("HttpUtil_third_try_{}", currentTimeMillis);
                    CloseableHttpResponse response = getHttpClient().execute(httpPost);
                    HttpEntity httpEntity = response.getEntity();
                    String str = null;
                    if (httpEntity != null) {
                        str = EntityUtils.toString(httpEntity, encode);
                    }
                    json.put("result", str);
                    json.put("resultCode", 1);
                } catch (Exception e1) {
                    json.put("resultCode", 0);
                    json.put("result", null);
                    json.put("errorMsg", e1.getMessage());
                    log.error("error", e1);
                }
            } catch (Exception e2) {
                json.put("resultCode", 0);
                json.put("result", null);
                json.put("errorMsg", e2.getMessage());
                log.error("error", e2);
            }
        } catch (Exception e3) {
            json.put("resultCode", 0);
            json.put("result", null);
            json.put("errorMsg", e3.getMessage());
            log.error("error", e3);
        }
        log.info("HttpResponse_{}_result={}", currentTimeMillis, json);
        return json;
    }

    /**
     * 请求参数为String
     */
    public static JSONObject httpClientPost(String url, String params, String encode, Map<String, String> headers) {
        JSONObject json = new JSONObject();
        // set encode
        if (StringUtils.isEmpty(encode)) {
            encode = "UTF-8";
        }
        HttpPost httpPost = new HttpPost(url);
        long currentTimeMillis = System.currentTimeMillis();
        log.info("HttpRequest_{}_url={}_param={}", currentTimeMillis, url, params);
        try {
            // add headers
            if (headers != null) {
                httpPost.setHeader("Content-Type", "application/json");
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // add parameters
            if (!StringUtils.isEmpty(params)) {
                httpPost.setEntity(new StringEntity(params, encode));
            }
            log.info("HttpUtil_first_try_{}", currentTimeMillis);
            CloseableHttpResponse response = getHttpClient().execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            String str = null;
            if (httpEntity != null) {
                str = EntityUtils.toString(httpEntity, encode);
            }
            json.put("result", str);
            json.put("resultCode", 1);
        } catch (IOException ex1) {
            try {
                Thread.sleep(1000);
                log.info("HttpUtil_second_try_{}", currentTimeMillis);
                CloseableHttpResponse response = getHttpClient().execute(httpPost);
                HttpEntity httpEntity = response.getEntity();
                String str = null;
                if (httpEntity != null) {
                    str = EntityUtils.toString(httpEntity, encode);
                }
                json.put("result", str);
                json.put("resultCode", 1);
            } catch (IOException ex2) {
                try {
                    Thread.sleep(1000);
                    log.info("HttpUtil_third_try_{}", currentTimeMillis);
                    CloseableHttpResponse response = getHttpClient().execute(httpPost);
                    HttpEntity httpEntity = response.getEntity();
                    String str = null;
                    if (httpEntity != null) {
                        str = EntityUtils.toString(httpEntity, encode);
                    }
                    json.put("result", str);
                    json.put("resultCode", 1);
                } catch (Exception e1) {
                    json.put("resultCode", 0);
                    json.put("result", null);
                    json.put("errorMsg", e1.getMessage());
                    log.error("error", e1);
                }
            } catch (Exception e2) {
                json.put("resultCode", 0);
                json.put("result", null);
                json.put("errorMsg", e2.getMessage());
                log.error("error", e2);
            }
        } catch (Exception e3) {
            json.put("resultCode", 0);
            json.put("result", null);
            json.put("errorMsg", e3.getMessage());
            log.error("error", e3);
        }
        log.info("HttpResponse_{}_result={}", currentTimeMillis, json);
        return json;
    }

    /**
     * 请求参数为String
     */
    public static JSONObject httpPost(String reqUrl, String reqParam, String encode, Map<String, String> headers) {
        JSONObject json = new JSONObject();
        String result = "";
        if (StringUtils.isEmpty(encode)) {
            encode = "UTF-8";
        }
        HttpURLConnection httpPost = null;
        long currentTimeMillis = System.currentTimeMillis();
        log.info("HttpRequest_{}_url={}_param={}", currentTimeMillis, reqUrl, reqParam);
        try {
            URL url = new URL(reqUrl);
            httpPost = (HttpURLConnection) url.openConnection();
            httpPost.setDoInput(true);
            httpPost.setDoOutput(true);
            httpPost.setRequestMethod("POST");
            httpPost.setRequestProperty("Content-Type", "application/json");
            // add headers
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpPost.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            log.info("HttpUtil_first_try_{}", currentTimeMillis);
            httpPost.connect();

            OutputStreamWriter wr = new OutputStreamWriter(httpPost.getOutputStream(), encode);
            wr.write(reqParam);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(httpPost.getInputStream(), "UTF-8"));
            String rs = "";
            while ((rs = rd.readLine()) != null) {
                result += rs;
            }
            wr.close();
            rd.close();

            json.put("result", result);
            json.put("resultCode", 1);
        } catch (IOException ex1) {
            try {
                Thread.sleep(1000);
                log.info("HttpUtil_second_try_{}", currentTimeMillis);
                httpPost.connect();
                OutputStreamWriter wr = new OutputStreamWriter(httpPost.getOutputStream(), encode);
                wr.write(reqParam);
                wr.flush();
                BufferedReader rd = new BufferedReader(new InputStreamReader(httpPost.getInputStream(), "UTF-8"));
                String rs = "";
                while ((rs = rd.readLine()) != null) {
                    result += rs;
                }
                wr.close();
                rd.close();
                json.put("result", result);
                json.put("resultCode", 1);
            } catch (IOException ex2) {
                try {
                    Thread.sleep(1000);
                    log.info("HttpUtil_third_try_{}", currentTimeMillis);
                    httpPost.connect();
                    OutputStreamWriter wr = new OutputStreamWriter(httpPost.getOutputStream(), encode);
                    wr.write(reqParam);
                    wr.flush();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(httpPost.getInputStream(), "UTF-8"));
                    String rs = "";
                    while ((rs = rd.readLine()) != null) {
                        result += rs;
                    }
                    wr.close();
                    rd.close();
                    json.put("result", result);
                    json.put("resultCode", 1);
                } catch (Exception e1) {
                    json.put("resultCode", 0);
                    json.put("result", null);
                    json.put("errorMsg", e1.getMessage());
                    log.error("error", e1);
                }
            } catch (Exception e2) {
                json.put("resultCode", 0);
                json.put("result", null);
                json.put("errorMsg", e2.getMessage());
                log.error("error", e2);
            }
        } catch (Exception e) {
            json.put("resultCode", 0);
            json.put("result", null);
            json.put("errorMsg", e.getMessage());
            log.error("error", e);
        }
        log.info("result_{}_{}", currentTimeMillis, json);
        return json;
    }

    /**
     * 请求参数为String
     */
    public static JSONObject httpPosts(String reqUrl, String reqParam, String encode, Map<String, String> headers) {
        JSONObject json = new JSONObject();
        String result = "";
        if (StringUtils.isEmpty(encode)) {
            encode = "UTF-8";
        }
        HttpURLConnection httpPost = null;
        long currentTimeMillis = System.currentTimeMillis();
        log.info("HttpRequest_{}_url={}_param={}", currentTimeMillis, reqUrl, reqParam);
        try {
            URL url = new URL(reqUrl);
            httpPost = (HttpURLConnection) url.openConnection();
            httpPost.setDoInput(true);
            httpPost.setDoOutput(true);
            httpPost.setRequestMethod("POST");
            httpPost.setRequestProperty("Content-Type", "application/json");
            // add headers
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpPost.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            log.info("HttpUtil_first_try_{}", currentTimeMillis);
            httpPost.connect();

            OutputStreamWriter wr = new OutputStreamWriter(httpPost.getOutputStream(), encode);
            wr.write(reqParam);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(httpPost.getInputStream(), "UTF-8"));
            String rs = "";
            while ((rs = rd.readLine()) != null) {
                result += rs;
            }
            wr.close();
            rd.close();

            json.put("result", result);
            json.put("resultCode", 1);
        } catch (IOException ex1) {
            try {
                Thread.sleep(1000);
                log.info("HttpUtil_second_try_{}", currentTimeMillis);
                httpPost.connect();
                OutputStreamWriter wr = new OutputStreamWriter(httpPost.getOutputStream(), encode);
                wr.write(reqParam);
                wr.flush();
                BufferedReader rd = new BufferedReader(new InputStreamReader(httpPost.getInputStream(), "UTF-8"));
                String rs = "";
                while ((rs = rd.readLine()) != null) {
                    result += rs;
                }
                wr.close();
                rd.close();
                json.put("result", result);
                json.put("resultCode", 1);
            } catch (IOException ex2) {
                try {
                    Thread.sleep(1000);
                    log.info("HttpUtil_third_try_{}", currentTimeMillis);
                    httpPost.connect();
                    OutputStreamWriter wr = new OutputStreamWriter(httpPost.getOutputStream(), encode);
                    wr.write(reqParam);
                    wr.flush();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(httpPost.getInputStream(), "UTF-8"));
                    String rs = "";
                    while ((rs = rd.readLine()) != null) {
                        result += rs;
                    }
                    wr.close();
                    rd.close();
                    json.put("result", result);
                    json.put("resultCode", 1);
                } catch (Exception e1) {
                    json.put("resultCode", 0);
                    json.put("result", null);
                    json.put("errorMsg", e1.getMessage());
                    log.error("error", e1);
                }
            } catch (Exception e2) {
                json.put("resultCode", 0);
                json.put("result", null);
                json.put("errorMsg", e2.getMessage());
                log.error("error", e2);
            }
        } catch (Exception e) {
            json.put("resultCode", 0);
            json.put("result", null);
            json.put("errorMsg", e.getMessage());
            log.error("error", e);
        }
        log.info("result_{}_{}", currentTimeMillis, json);
        return json;
    }
    /**
     * 携带证书的请求
     */
    public static JSONObject httpPost(String reqUrl, String reqParam, String encode, String keyStoreType, String keyStorePath, String password) {
        JSONObject json = new JSONObject();
        if (StringUtils.isEmpty(encode)) {
            encode = "UTF-8";
        }
        String target = null;
        HttpPost httpPost = new HttpPost(reqUrl);
        CloseableHttpClient httpclient = null;
        long currentTimeMillis = System.currentTimeMillis();
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(new FileInputStream(new File(keyStorePath)), password.toCharArray());
            SSLContext sslcontext = SSLContexts.custom()
                    //忽略掉对服务器端证书的校验
                    .loadTrustMaterial(new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).loadKeyMaterial(keyStore, password.toCharArray()).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            httpclient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();

            if (!StringUtils.isEmpty(reqParam)) {
                HttpEntity entity = new StringEntity(reqParam, encode);
                httpPost.setEntity(entity);
            }
            log.info("HttpUtil_first_try_{}", currentTimeMillis);
            CloseableHttpResponse response = httpclient.execute(httpPost);

            HttpEntity result = response.getEntity();
            target = EntityUtils.toString(result);
            EntityUtils.consume(result);
            response.close();
            httpclient.close();

            json.put("result", target);
            json.put("resultCode", 1);
        } catch (IOException ex1) {
            try {
                Thread.sleep(1000);
                log.info("HttpUtil_second_try_{}", currentTimeMillis);
                CloseableHttpResponse response = httpclient.execute(httpPost);
                HttpEntity result = response.getEntity();
                target = EntityUtils.toString(result);
                EntityUtils.consume(result);
                response.close();
                httpclient.close();
                json.put("result", target);
                json.put("resultCode", 1);
            } catch (IOException ex2) {
                try {
                    Thread.sleep(1000);
                    log.info("HttpUtil_third_try_{}", currentTimeMillis);
                    CloseableHttpResponse response = httpclient.execute(httpPost);
                    HttpEntity result = response.getEntity();
                    target = EntityUtils.toString(result);
                    EntityUtils.consume(result);
                    response.close();
                    httpclient.close();
                    json.put("result", target);
                    json.put("resultCode", 1);
                } catch (Exception e1) {
                    json.put("resultCode", 0);
                    json.put("result", null);
                    json.put("errorMsg", e1.getMessage());
                    log.error("error", e1);
                }
            } catch (Exception e2) {
                json.put("resultCode", 0);
                json.put("result", null);
                json.put("errorMsg", e2.getMessage());
                log.error("error", e2);
            }
        } catch (Exception e) {
            json.put("resultCode", 0);
            json.put("result", null);
            json.put("errorMsg", e.getMessage());
            log.error("error", e);
        }

        return json;
    }

    public static JSONObject httpGet(String reqUrl, Map<String, String> params, String responseEncoding) {
        if (StringUtils.isEmpty(responseEncoding)) {
            responseEncoding = "UTF-8";
        }
        JSONObject json = new JSONObject();
        String result = "";
        HttpURLConnection con1 = null;
        long currentTimeMillis = System.currentTimeMillis();
        try {
            if (params != null) {
                String param = "";
                for (String key : params.keySet()) {
                    if (StringUtils.isEmpty(params.get(key))) {
                        continue;
                    }
                    param += key + "=" + params.get(key) + "&";
                }
                if (!StringUtils.isEmpty(param)) {
                    param = param.substring(0, param.length() - 1);
                    if (reqUrl.indexOf("?") > 0) {
                        reqUrl += "&" + param;
                    } else {
                        reqUrl += "?" + param;
                    }
                }
            }

            URL urll = new URL(reqUrl);
            con1 = (HttpURLConnection) urll.openConnection();
            con1.setDoOutput(true);
            con1.setRequestMethod("GET");
            log.info("HttpUtil_first_try_{}", currentTimeMillis);
            con1.connect();

            BufferedReader rd = new BufferedReader(new InputStreamReader(con1.getInputStream(), responseEncoding));
            String rs = "";
            while ((rs = rd.readLine()) != null) {
                result += rs;
            }
            rd.close();

            json.put("result", result);
            json.put("resultCode", 1);
        } catch (IOException ex1) {
            try {
                Thread.sleep(1000);
                log.info("HttpUtil_second_try_{}", currentTimeMillis);
                con1.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(con1.getInputStream(), responseEncoding));
                String rs = "";
                while ((rs = rd.readLine()) != null) {
                    result += rs;
                }
                rd.close();
                json.put("result", result);
                json.put("resultCode", 1);
            } catch (IOException ex2) {
                try {
                    Thread.sleep(1000);
                    log.info("HttpUtil_third_try_{}", currentTimeMillis);
                    con1.connect();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(con1.getInputStream(), responseEncoding));
                    String rs = "";
                    while ((rs = rd.readLine()) != null) {
                        result += rs;
                    }
                    rd.close();
                    json.put("result", result);
                    json.put("resultCode", 1);
                } catch (Exception e1) {
                    json.put("resultCode", 0);
                    json.put("result", null);
                    json.put("errorMsg", e1.getMessage());
                    log.error("error", e1);
                }
            } catch (Exception e2) {
                json.put("resultCode", 0);
                json.put("result", null);
                json.put("errorMsg", e2.getMessage());
                log.error("error", e2);
            }
        } catch (Exception e) {
            json.put("resultCode", 0);
            json.put("result", null);
            json.put("errorMsg", e.getMessage());
            log.error("error", e);
        }

        return json;
    }

    public static JSONObject httpClientGet(String url, Map<String, String> params, Map<String, String> headers) {
        JSONObject result = new JSONObject();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        long currentTimeMillis = System.currentTimeMillis();
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            // 添加参数
            if (params != null) {
                for (Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            // 根据带参数的URI对象构建GET请求对象
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            // 添加请求头信息
            // 浏览器表示
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
            // 传输的类型
            httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            log.info("HttpGet_{}_url==>{}", currentTimeMillis, uriBuilder.toString());
            // 执行请求
            response = httpClient.execute(httpGet);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();

            result.put("result", EntityUtils.toString(entity, "UTF-8"));
            result.put("resultCode", 1);
        } catch (Exception e) {
            result.put("resultCode", 0);
            result.put("errorMsg", e.getMessage());
            log.error("error", e);
        } finally {
            // 释放连接
            if (response != null) {
                try {
                    response.close();
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static String postFormData(String url, Map<String, Object> parameter) throws Exception {
        URL urls = new URL(url);
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        String rs = "";
        try {
            connection = (HttpURLConnection) urls.openConnection();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----footfoodapplicationrequestnetwork");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Range", "bytes=" + "");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(20000);
            connection.setRequestMethod("POST");

            StringBuffer buffer = new StringBuffer();
            for (Entry<String, Object> entiy : parameter.entrySet()) {
                buffer.append("------footfoodapplicationrequestnetwork\r\n");
                buffer.append("Content-Disposition: form-data; name=\"");
                buffer.append(entiy.getKey());
                buffer.append("\"\r\n\r\n");
                buffer.append(entiy.getValue());
                buffer.append("\r\n");
            }
            if (parameter != null) {
                buffer.append("------footfoodapplicationrequestnetwork--\r\n");
            }
            outputStream = connection.getOutputStream();
            outputStream.write(buffer.toString().getBytes());
            try {
                connection.connect();
                if (connection.getResponseCode() == 200) {
//	                rs = getWebSource(connection.getInputStream());
                    // 定义 BufferedReader输入流来读取URL的响应
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), "UTF-8"));
                    String line;
                    while ((line = in.readLine()) != null) {
                        rs += line;
                    }
                }
            } catch (Exception e) {
                rs = null;
            }

            return rs;
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
            }
            outputStream = null;

            if (connection != null) {
                connection.disconnect();
            }
            connection = null;
        }
    }

    /**
     * 模拟post请求
     * @param uriHttp
     * @param param
     * @return
     */
    public static String doPost(String uriHttp, String param) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse resHttp = null;
        try {
            HttpPost post = new HttpPost(uriHttp);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(3000).setSocketTimeout(18000).build();
            post.setConfig(requestConfig);
            StringEntity paramJsonEncode = new StringEntity(param, "UTF-8");// 解决中文乱码问题
            post.setEntity(paramJsonEncode);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            resHttp = httpClient.execute(post);
            int statusHttp = resHttp.getStatusLine().getStatusCode();
            String entity = EntityUtils.toString(resHttp.getEntity());
            System.out.println("post http[" + uriHttp + "], response status:" + statusHttp + ",content:" + entity);
            if (HttpStatus.SC_OK == statusHttp) {// 正常返回
                return entity;
            }
        } catch (Exception e) {
            System.out.println("get http[" + uriHttp + "] error:"+e);
        } finally {
            try {
                if (null != resHttp) {
                    resHttp.close();
                }
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                System.out.println("get http[" + uriHttp + "] error:"+e);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpGet("http://182.61.37.169/", null, "");
                }
            }).start();
        }

    }
}
