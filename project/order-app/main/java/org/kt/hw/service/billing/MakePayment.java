package org.kt.hw.service.billing;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

@Slf4j
public class MakePayment {
    public static void makePayment(int orderId, int amount, String token) {
        String endpoint = String.format("%s/makePayment", System.getenv("BILLING_HOST"));

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("order_id", orderId);
        jsonData.addProperty("amount", amount);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setEntity(new StringEntity(jsonData.toString()));
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.setHeader("Authorization", token);


            int statusCode = httpClient.execute(httpPost).getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.error("status code: " + statusCode);
                throw new RuntimeException("Internal service error while make payment");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
