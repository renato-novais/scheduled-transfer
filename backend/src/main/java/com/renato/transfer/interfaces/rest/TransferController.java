package com.renato.transfer.interfaces.rest;

import com.renato.transfer.application.dto.CreateTransferRequest;
import com.renato.transfer.application.dto.TransferResponse;
import com.renato.transfer.application.service.ListTransfersService;
import com.renato.transfer.application.service.ScheduleTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Transfers", description = "Schedule and list financial transfer schedules")
@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final ScheduleTransferService scheduleTransferService;
    private final ListTransfersService listTransfersService;

    public TransferController(ScheduleTransferService scheduleTransferService,
                               ListTransfersService listTransfersService) {
        this.scheduleTransferService = scheduleTransferService;
        this.listTransfersService = listTransfersService;
    }

    @Operation(summary = "Schedule a new transfer",
        description = "Validates the request, calculates the fee based on the transfer date and persists the schedule")
    @PostMapping
    public ResponseEntity<TransferResponse> create(@Valid @RequestBody CreateTransferRequest request) {
        TransferResponse response = scheduleTransferService.schedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List scheduled transfers",
        description = "Returns every scheduled transfer, ordered by scheduling date and id descending")
    @GetMapping
    public ResponseEntity<List<TransferResponse>> list() {
        return ResponseEntity.ok(listTransfersService.listAll());
    }
}
