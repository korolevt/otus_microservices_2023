package org.kt.hw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelResponse {
    @JsonProperty("order_id")
    int orderId;

    Integer amount;
}
