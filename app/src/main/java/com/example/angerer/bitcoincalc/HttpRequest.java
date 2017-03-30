package com.example.angerer.bitcoincalc;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    public HttpRequest() {
        Log.i("INFO", "New HTTP request.");
    }

    private static final String LOGGIN_TAG = HttpRequest.class.getSimpleName();

    //Send HTTP Request to specified URL
    public String callURL(String myURL) {
        String req_response = null;
        try {
            URL url = new URL(myURL);

            // Connect to Webserver
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("GET");

            // String response
            InputStream in = new BufferedInputStream(httpCon.getInputStream());
            req_response = getStream(in);
        }
        catch (IOException e) {
            Log.e(LOGGIN_TAG, e.getMessage());
        }
        catch (Exception e) {
            Log.e(LOGGIN_TAG, e.getMessage());
        }
        return req_response;
    }

    // Get JSON response to STRING
    private String getStream(InputStream in) {

        String line = "";

        // New Buffered Reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder myBuilder = new StringBuilder();

        try {
            // Read response until last line
            while ((line = reader.readLine()) != null) {
                myBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myBuilder.toString();
    }
}
