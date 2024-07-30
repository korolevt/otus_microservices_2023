package org.kt.hw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReserveGoodsRequest {

    @JsonProperty("order_id")
    int orderId;

    @JsonProperty("good_ids")
    int[] goodIds;
}
