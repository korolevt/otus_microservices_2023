package org.kt.hw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.Interval;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ReserveExcursionRequest {

    @JsonProperty("order_id")
    int orderId;

    @JsonProperty("location_id")
    int locationId;

    @JsonProperty("excursion_id")
    int excursionId;

    Instant start;

    // кол-во билетов
    int count;
}
