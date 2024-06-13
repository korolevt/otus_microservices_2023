package org.kt.hw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ControllerHelper {

    public static void successResponse(HttpServletResponse response, Object data) {
        response.setStatus(HttpServletResponse.SC_OK);
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("code", HttpServletResponse.SC_OK);
        responseJson.add("data", new Gson().toJsonTree(data));
        try {
            response.getWriter().write(responseJson.toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void errorResponse(HttpServletResponse response, String message, int status) {
        response.setStatus(status);
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("code", status);
        responseJson.addProperty("message", message);
        try {
            response.getWriter().write(responseJson.toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}

