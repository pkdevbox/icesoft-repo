package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletServer {

    void service(HttpServletRequest request, HttpServletResponse response) throws Exception;

    void shutdown();
}
