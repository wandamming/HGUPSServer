package com.hgups.express.business.ec.ebay;

import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.AccessToken;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class EbayOauth {

    static {
        try {
            CredentialUtil.load(new FileInputStream(ResourceUtils.getFile("classpath:ebay-config.yaml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getAuthorizaRestfulUrl(String shopName) {
        OAuth2Api oauth2Api = new OAuth2Api();
        List<String> scopeList = new ArrayList<>();
        scopeList.add("https://api.ebay.com/oauth/api_scope");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.finances");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment.readonly");

        String url = oauth2Api.generateUserAuthorizationUrl(Environment.PRODUCTION, scopeList, Optional.of(shopName));
        return url;
    }

    public String gettokenByCode(String code, String state) throws IOException {
        OAuth2Api oauth2Api = new OAuth2Api();
        OAuthResponse oAuthResponse = oauth2Api.exchangeCodeForAccessToken(Environment.PRODUCTION, code);
        AccessToken accessToken = oAuthResponse.getAccessToken().get();
        String token = accessToken.getToken();
        String refreshToken = oAuthResponse.getRefreshToken().get().getToken();
        Date expiresOn = oAuthResponse.getRefreshToken().get().getExpiresOn();
        return token;
    }

    public void reRefreshToken(String freshToken) throws IOException {
        OAuth2Api oauth2Api = new OAuth2Api();
        List<String> scopeList = new ArrayList<>();
        scopeList.add("https://api.ebay.com/oauth/api_scope");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.finances");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment");
        scopeList.add("https://api.ebay.com/oauth/api_scope/sell.fulfillment.readonly");
        OAuthResponse oAuthResponse = null;
        try {
            oAuthResponse = oauth2Api.getAccessToken(Environment.PRODUCTION, freshToken, scopeList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AccessToken accessToken = oAuthResponse.getAccessToken().get();
    }

    public void getOrders(String restfulToken) throws IOException {


        String url = "https://api.ebay.com/sell/fulfillment/v1/order?";
        Integer endNumber = 3;
        Integer beginNumber = 5;
        String endTime = getISO8601Timestamp(-endNumber);
        String beginTime = getISO8601Timestamp(-beginNumber);
        int pageIndex = 0;
        int pageSize = 10;

        final OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url + "?filter=creationdate:%5B" + beginTime + ".." + endTime + "%5D&limit=" + pageSize + "&offset=" + pageIndex)
                .get()
                .addHeader("Authorization", "Bearer " + restfulToken)
                .build();
        final Response response = okHttpClient.newCall(request).execute();
        log.info(" getOrders response: " + response.body().string());
    }

    /**
     * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
     *
     * @param
     * @return
     */
    public static String getISO8601Timestamp(Integer days) {

        Date now = new Date();
        Date startDate = DateUtils.addDays(now, days);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(startDate);
        return nowAsISO;
    }

}
