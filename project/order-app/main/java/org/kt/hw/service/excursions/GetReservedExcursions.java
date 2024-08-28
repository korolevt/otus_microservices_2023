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
import org.kt.hw.model.Payment;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GetReservedExcursions {

    public static List<Integer> getReservedExcursions(int locationId, int excursionId, Instant startSlot) {
        String endpoint = String.format("%s/excursions/reserved", System.getenv("EXCURSION_HOST"));

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
                throw new RuntimeException("Internal service while get reserved excursions");
            }
            String json = EntityUtils.toString(response.getEntity());
            JsonObject result = JsonParser.parseString(json).getAsJsonObject();
            List<Integer> list = new ArrayList<>();
            JsonArray jsonArray = result.get("orderIds").getAsJsonArray();
            if (jsonArray != null) {
                int len = jsonArray.size();
                for (int i = 0; i < len; i++){
                    list.add(jsonArray.get(i).getAsInt());
                }
            }
            return list;

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
