package com.renato.transfer.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renato.transfer.application.dto.TransferResponse;
import com.renato.transfer.application.service.ListTransfersService;
import com.renato.transfer.application.service.ScheduleTransferService;
import com.renato.transfer.exception.FeeNotApplicableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduleTransferService scheduleTransferService;

    @MockBean
    private ListTransfersService listTransfersService;

    @Test
    void shouldReturn201WhenTransferIsScheduled() throws Exception {
        TransferResponse response = TransferResponse.builder()
            .id(1L)
            .sourceAccount("1234567890")
            .destinationAccount("0987654321")
            .amount(new BigDecimal("1000.00"))
            .fee(new BigDecimal("82.00"))
            .totalAmount(new BigDecimal("1082.00"))
            .transferDate(LocalDate.now().plusDays(15))
            .schedulingDate(LocalDate.now())
            .status("SCHEDULED")
            .build();
        when(scheduleTransferService.schedule(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestBody())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.fee").value(82.00))
            .andExpect(jsonPath("$.totalAmount").value(1082.00));
    }

    @Test
    void shouldReturn400WhenAccountPatternIsInvalid() throws Exception {
        String invalidBody = objectMapper.writeValueAsString(validRequestBody())
            .replace("1234567890", "123");

        mockMvc.perform(post("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn400WhenFeeIsNotApplicable() throws Exception {
        when(scheduleTransferService.schedule(any())).thenThrow(new FeeNotApplicableException(60));

        mockMvc.perform(post("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestBody())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("FEE_NOT_APPLICABLE"));
    }

    @Test
    void shouldReturnAllScheduledTransfers() throws Exception {
        TransferResponse response = TransferResponse.builder()
            .id(1L)
            .status("SCHEDULED")
            .build();
        when(listTransfersService.listAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/transfers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    private Map<String, Object> validRequestBody() {
        return Map.of(
            "sourceAccount", "1234567890",
            "destinationAccount", "0987654321",
            "amount", 1000.00,
            "transferDate", LocalDate.now().plusDays(15).toString()
        );
    }
}
