package org.kt.hw.controller;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kt.hw.auth.Auth;
import org.kt.hw.entity.Order;
import org.kt.hw.entity.User;
import org.kt.hw.model.*;
import org.kt.hw.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() {
        JsonObject msg = new JsonObject();
        msg.addProperty("text", "order was not created");
        msg.addProperty("userId", 1111);
        kafkaTemplate.send("notifications", msg.toString());
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest data, HttpServletRequest request) {
        UserDTO user = Auth.preHandle(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is empty");
        }

        Order newOrder = OrderRepository.createOrder(user.getId(), data.getTitle(), data.getPrice());
        orderRepository.save(newOrder);

        String billingServiceUrl = System.getenv("BILLING_SERVICE") + "/billing/withdraw";
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("amount", newOrder.getPrice());

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(billingServiceUrl);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.setHeader("Authorization", request.getHeader("Authorization"));
            httpPost.setEntity(new StringEntity(jsonData.toString()));

            int statusCode = httpClient.execute(httpPost).getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.OK.value()) {
                JsonObject message = new JsonObject();
                message.addProperty("text", "order was not created");
                message.addProperty("userId", user.getId());
                kafkaTemplate.send("notifications", message.toString());
                throw new ResponseStatusException(HttpStatus.valueOf(statusCode), "Not enough money");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JsonObject message = new JsonObject();
        message.addProperty("text", "order was created");
        message.addProperty("userId", user.getId());
        kafkaTemplate.send("notifications", message.toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateOrderResponse(newOrder.getId()));

    }
}
