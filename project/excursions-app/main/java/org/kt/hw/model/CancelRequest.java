package org.kt.hw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CancelRequest {
    @JsonProperty("order_id")
    int orderId;
}
