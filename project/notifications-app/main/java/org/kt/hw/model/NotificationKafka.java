package org.kt.hw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationKafka {
    Integer userId;
    Integer locationId;
    Integer excursionId;
    Instant start;
    Integer count;
    Integer amount;
    String text;

    @Override
    public String toString() {
        return "{" +
                "locationId=" + locationId +
                ", excursionId=" + excursionId +
                ", start=" + start +
                ", count=" + count +
                ", amount=" + amount +
                ", text='" + text + '\'' +
                '}';
    }
}
