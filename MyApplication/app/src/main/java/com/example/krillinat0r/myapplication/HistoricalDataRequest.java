package com.example.krillinat0r.myapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by nikla on 19-04-2018.
 */

//reference: https://developer.android.com/training/volley/request-custom.html#java
public class HistoricalDataRequest<String> extends Request<java.lang.String> {
    private final Map<java.lang.String, java.lang.String> headers;
    private final Response.Listener<java.lang.String> listener;
    private final java.lang.String requestUrl;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param headers Map of request headers
     */
    public HistoricalDataRequest(java.lang.String url, Map<java.lang.String, java.lang.String> headers,
                       Response.Listener<java.lang.String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.requestUrl = url;
        this.headers = headers;
        this.listener = listener;
    }

    @Override
    public Map<java.lang.String, java.lang.String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(java.lang.String response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<java.lang.String> parseNetworkResponse(NetworkResponse response) {
        try {
            java.lang.String request = "\"RequestUrl\":" + "\"" + requestUrl + "\"" + ",";
            byte[] byteRequest = request.getBytes();
            byte[] combinedResponse = new byte[response.data.length + byteRequest.length];
            System.arraycopy(response.data, 0, combinedResponse, 0, 1);
            System.arraycopy(byteRequest, 0, combinedResponse, 1, byteRequest.length);
            System.arraycopy(response.data, 1, combinedResponse, byteRequest.length + 1, response.data.length - 1);
            java.lang.String json = new java.lang.String(
                    combinedResponse,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    json,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
