package com.renato.transfer.infrastructure.persistence;

import com.renato.transfer.domain.model.TransferSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferScheduleRepository extends JpaRepository<TransferSchedule, Long> {

    List<TransferSchedule> findAllByOrderBySchedulingDateDescIdDesc();
}
