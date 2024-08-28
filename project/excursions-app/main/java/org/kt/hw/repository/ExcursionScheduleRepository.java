package org.kt.hw.repository;

import org.kt.hw.entity.Excursion;
import org.kt.hw.entity.ExcursionSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface ExcursionScheduleRepository extends JpaRepository<ExcursionSchedule, Integer> {
    ExcursionSchedule findExcursionScheduleByLocationIdAndExcursionIdAndStartSlot(Integer locationId, Integer excursionId, Instant start);
    ExcursionSchedule findExcursionScheduleById(int id);
}
