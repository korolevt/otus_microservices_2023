package org.kt.hw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class OrderRequest {
    @JsonProperty("location_id")
    int locationId;

    @JsonProperty("excursion_id")
    int excursionId;

    Instant start;
    Integer count;
}
