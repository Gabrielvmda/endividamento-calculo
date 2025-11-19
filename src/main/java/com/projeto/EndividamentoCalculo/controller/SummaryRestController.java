package com.projeto.EndividamentoCalculo.controller;

import com.projeto.EndividamentoCalculo.dto.SummaryResponseDto;
import com.projeto.EndividamentoCalculo.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/summary")
public class SummaryRestController {

    @Autowired
    private SummaryService summaryService;


    @GetMapping("/user/{userId}")
    public ResponseEntity<SummaryResponseDto> getSummary(@PathVariable Long userId) {
        try {
            SummaryResponseDto dto = summaryService.generateSummaryForUser(userId);
            if (dto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
