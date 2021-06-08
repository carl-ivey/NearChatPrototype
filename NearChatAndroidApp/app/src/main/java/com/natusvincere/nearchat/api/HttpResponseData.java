package com.natusvincere.nearchat.api;

public class HttpResponseData
{
    public int responseCode;
    public String responseStr;

    public HttpResponseData(int responseCode, String responseStr)
    {
        this.responseCode = responseCode;
        this.responseStr = responseStr;
    }
}
