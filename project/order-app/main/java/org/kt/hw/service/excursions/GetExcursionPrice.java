package org.kt.hw.service.excursions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
public class GetExcursionPrice {

    public static Integer getExcursionPrice(int locationId, int excursionId, Instant startSlot) {
//        String endpoint = System.getenv("INVENTORY_HOST") + "/reserveGoods";
        String endpoint = String.format("%s/excursions/price", System.getenv("EXCURSION_HOST"));

        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("location_id", locationId);
        jsonData.addProperty("excursion_id", excursionId);
        jsonData.addProperty("start", startSlot.toString());

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.setEntity(new StringEntity(jsonData.toString()));
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

            CloseableHttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.error("status code: " + statusCode);
                throw new RuntimeException("Internal service while get excursion price");
            }
            String json = EntityUtils.toString(response.getEntity());
            JsonObject result = JsonParser.parseString(json).getAsJsonObject();
            return result.get("price").getAsInt();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
