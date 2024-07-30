package org.kt.hw.service.inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ReserveGoods {

    public static List<Integer> reserveGoods(int orderId, List<Integer> goodIds) {
//        String endpoint = System.getenv("INVENTORY_HOST") + "/reserveGoods";
        String endpoint = String.format("%s/reserveGoods", System.getenv("INVENTORY_HOST"));

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("order_id", orderId);
        JsonArray jsonGoodArray = new JsonArray();
        for (int goodId : goodIds) {
            jsonGoodArray.add(goodId);
        }
        jsonData.add("good_ids", jsonGoodArray);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setEntity(new StringEntity(jsonData.toString()));
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

//            CloseableHttpResponse response = httpClient.execute(httpPost);

            int statusCode = httpClient.execute(httpPost).getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.error("status code: " + statusCode);
                throw new RuntimeException("Internal service while reserving goods");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return goodIds;
    }
}
