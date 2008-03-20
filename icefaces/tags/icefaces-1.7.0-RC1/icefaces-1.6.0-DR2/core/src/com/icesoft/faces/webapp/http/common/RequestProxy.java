package com.icesoft.faces.webapp.http.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

public class RequestProxy implements Request {
    protected Request request;

    public RequestProxy(Request request) {
        this.request = request;
    }

    public String getMethod() {
        return request.getMethod();
    }

    public URI getURI() {
        return request.getURI();
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public String[] getHeaderAsStrings(String name) {
        return request.getHeaderAsStrings(name);
    }

    public Date getHeaderAsDate(String name) {
        return request.getHeaderAsDate(name);
    }

    public int getHeaderAsInteger(String name) {
        return request.getHeaderAsInteger(name);
    }

    public String getParameter(String name) {
        return request.getParameter(name);
    }

    public String[] getParameterAsStrings(String name) {
        return request.getParameterAsStrings(name);
    }

    public int getParameterAsInteger(String name) {
        return request.getParameterAsInteger(name);
    }

    public boolean getParameterAsBoolean(String name) {
        return request.getParameterAsBoolean(name);
    }

    public String getParameter(String name, String defaultValue) {
        return request.getParameter(name, defaultValue);
    }

    public int getParameterAsInteger(String name, int defaultValue) {
        return request.getParameterAsInteger(name, defaultValue);
    }

    public boolean getParameterAsBoolean(String name, boolean defaultValue) {
        return request.getParameterAsBoolean(name, defaultValue);
    }

    public InputStream readBody() throws IOException {
        return request.readBody();
    }

    public void readBodyInto(OutputStream out) throws IOException {
        request.readBodyInto(out);
    }

    public void respondWith(ResponseHandler handler) throws Exception {
        request.respondWith(handler);
    }
}
