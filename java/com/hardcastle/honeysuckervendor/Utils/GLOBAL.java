package com.hardcastle.honeysuckervendor.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.hardcastle.honeysuckervendor.R;
import com.hardcastle.honeysuckervendor.Receiver.ConnectivityReceiver;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by abhijeet on 8/29/2017.
 */

public class GLOBAL extends MultiDexApplication {

    public static GLOBAL globalInstance = null;
    private SharedPreferences sharedPreferences;
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    public HashMap<String, String> hashmapKeyValue = new HashMap<String, String>();

    @Override
    public void onCreate() {
        super.onCreate();
        globalInstance = this;

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/myriad.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }


    public GLOBAL() {
    }


    public static synchronized GLOBAL getInstance() {

        if (globalInstance == null) {

            globalInstance = new GLOBAL();
        }

        return globalInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public static void showMessage(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public JSONObject getInput(String TAG, HashMap<String, String> hashMap) {

        JSONObject paramObject = new JSONObject();

        Set<String> keys = hashMap.keySet();

        try {

            for (String key : keys) {
                paramObject.put(key, hashMap.get(key));
            }

            Log.i(TAG, "INPUT JSON : " + paramObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paramObject;
    }

    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     */
    public String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            response = "TimeOUT";
            e.printStackTrace();
        }

        return response;

    }

}
