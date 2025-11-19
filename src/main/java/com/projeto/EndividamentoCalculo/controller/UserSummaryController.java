package com.projeto.EndividamentoCalculo.controller;

import com.projeto.EndividamentoCalculo.dto.SummaryResponseDto;
import com.projeto.EndividamentoCalculo.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserSummaryController {

    private final SummaryService summaryService;

    @Autowired
    public UserSummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    // GET /api/users/{id}/summary
    @GetMapping("/{id}/summary")
    public ResponseEntity<SummaryResponseDto> getUserSummary(@PathVariable Long id) {
        SummaryResponseDto summary = summaryService.generateSummaryForUser(id);
        return ResponseEntity.ok(summary);
    }
}
