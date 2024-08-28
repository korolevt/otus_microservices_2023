package org.kt.hw.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetReserveExcursionsResponse {
    List<Integer> orderIds;
}
