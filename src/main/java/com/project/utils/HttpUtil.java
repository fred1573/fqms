package com.project.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/12.
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    public static final String ASSIGN_SIGN = "=";
    public static final String JOIN_SIGN = "&";

    /**
     * @param url
     * @param hasConnTimeout
     * @return
     */
    public String get(String url, boolean hasConnTimeout) {
        logger.info("----------SEND HTTP GET REQUEST [URL]:" + url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);
            //是否使用超时
            if (hasConnTimeout) {
                httpget.setConfig(getRequestConfig(10000));
            }
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    JSONObject jsonObject = JSON.parseObject(result);
                    String status = jsonObject.getString("status");
                    if (StringUtils.isNotBlank(status) && !"200".equals(status)) {
                        // 只有接口请求失败时才打印日志
                        logger.error("==========SEND HTTP POST REQUEST [RESULT]:" + result);
                    }
                    return result;
                }
                return null;
            } finally {
                response.close();
            }
        } catch (ConnectTimeoutException e) {
            throw new RuntimeException("接口：" + url + "请求超时");
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    /**
     * 重载get方法定时
     *
     * @param url
     * @return
     */
    public String get(String url) {
        return get(url, true);
    }

    public String postForm(String url, Map<String, Object> nameValuePairs, boolean hasConnectTime) {
        logger.info("----------SEND HTTP POST REQUEST [URL]:" + url);
        logger.info("----------SEND HTTP POST REQUEST [PARAM]:" + nameValuePairs);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        if (hasConnectTime) {
            httppost.setConfig(getRequestConfig(10000));
        }
        List<NameValuePair> formParams = new ArrayList<>();
        for (String key : nameValuePairs.keySet()) {
            formParams.add(new BasicNameValuePair(key, nameValuePairs.get(key).toString()));
        }
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            httppost.setEntity(uefEntity);
            CloseableHttpResponse execute = httpclient.execute(httppost);
            String result = EntityUtils.toString(execute.getEntity());
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if (StringUtils.isNotBlank(status) && !"200".equals(status)) {
                // 只有接口请求失败时才打印日志
                logger.error("==========SEND HTTP POST REQUEST [RESULT]:" + result);
            }
            return result;
        } catch (ConnectTimeoutException e) {
            throw new RuntimeException("接口：" + url + "请求超时");
        } catch (ClientProtocolException e) {
            if (nameValuePairs.get("companyCode") != null) {
                logger.error("clientProtocalException,innId=%d, companyCode=%s, sj=" + nameValuePairs.get("sj"), nameValuePairs.get("innId"), nameValuePairs.get("companyCode").toString());
            }
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            if (nameValuePairs.get("companyCode") != null) {
                logger.error("UnsupportedEncodingException,innId=%d, companyCode=%s, sj=" + nameValuePairs.get("sj"), nameValuePairs.get("innId"), nameValuePairs.get("companyCode").toString());
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            if (nameValuePairs.get("companyCode") != null) {
                logger.error("请求超时,innId=%d, companyCode=%s, sj=" + nameValuePairs.get("sj"), nameValuePairs.get("innId"), nameValuePairs.get("companyCode").toString());
            }
            throw new RuntimeException(e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 重载postForm方法
     *
     * @param url
     * @param nameValuePairs
     * @return
     */
    public String postForm(String url, Map<String, Object> nameValuePairs) {
        return postForm(url, nameValuePairs, true);
    }

    /**
     * Build get url   host+params
     *
     * @param host
     * @param params
     * @return
     */
    public String buildUrl(String host, String file, Map<String, String> params) {
        StringBuilder sb = new StringBuilder().append(host);
        if (StringUtils.isBlank(file)) {
            return sb.toString();
        }
        sb.append(file);
        if (params == null || params.size() <= 0) {
            return sb.toString();
        }
        sb.append("?");
        for (String key : params.keySet()) {
            sb.append(key).append(ASSIGN_SIGN).append(params.get(key)).append(JOIN_SIGN);
        }
        return sb.substring(0, sb.lastIndexOf(JOIN_SIGN));
    }

    /**
     * 通过HTTP方式请求指定URL的HTTP接口
     *
     * @param url    HTTP接口的访问路径
     * @param params
     * @return
     */
    public static String httpPost(String url, Map<String, Object> params, boolean hasConnectTime) {
        logger.info("----------SEND HTTP POST REQUEST [URL]:" + url);
        logger.info("----------SEND HTTP POST REQUEST [PARAM]:" + params);
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value = "";
                Object object = entry.getValue();
                if (object != null) {
                    value = object.toString();
                }
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }
        HttpPost httpPost;
        try {
            httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            // 设置请求和传输超时时间
            if (hasConnectTime) {
                httpPost.setConfig(getRequestConfig(10000));
            }
            HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
            //获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            //响应状态
            //判断响应实体是否为空
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                JSONObject jsonObject = JSON.parseObject(result);
                String status = jsonObject.getString("status");
                if (StringUtils.isNotBlank(status) && !"200".equals(status)) {
                    // 只有接口请求失败时才打印日志
                    logger.error("==========SEND HTTP POST REQUEST [RESULT]:" + result);
                }
                return result;
            }
        } catch (ConnectTimeoutException e) {
            throw new RuntimeException("接口：" + url + "请求超时");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }




    /**
     * 重载httpPost
     *
     * @param url
     * @param params
     * @return
     */
    public static String httpPost(String url, Map<String, Object> params) {
        return httpPost(url, params, true);
    }

    /**
     * 获取requestConfig
     *
     * @param i
     * @return
     */
    private static RequestConfig getRequestConfig(int i) {
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(i).
                setConnectTimeout(i).
                setConnectionRequestTimeout(i).
                setStaleConnectionCheckEnabled(true).
                build();
        return requestConfig;
    }
}
