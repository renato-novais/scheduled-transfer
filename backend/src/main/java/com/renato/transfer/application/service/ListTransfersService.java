package com.renato.transfer.application.service;

import com.renato.transfer.application.dto.TransferResponse;
import com.renato.transfer.infrastructure.persistence.TransferScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListTransfersService {

    private final TransferScheduleRepository repository;

    public ListTransfersService(TransferScheduleRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TransferResponse> listAll() {
        return repository.findAllByOrderBySchedulingDateDescIdDesc().stream()
            .map(TransferResponse::from)
            .collect(Collectors.toList());
    }
}
