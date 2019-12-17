package com.hardcastle.honeysuckervendor.Utils;

/**
 * Created by abhijeet on 8/23/2017.
 */

public class APIUtils {

    public static final String BASE_URL = "http://hardcastle.co.in/PHP_WEB/HONEY_SUCKER/WS/"; // http://hardcastlegis.co.in/PHP_WEB/HONEY_SUCKER/WS/

    public static APIInterface getAPIInterface() {

        return RetrofitClient.getClient(BASE_URL).create(APIInterface.class);
    }
}
