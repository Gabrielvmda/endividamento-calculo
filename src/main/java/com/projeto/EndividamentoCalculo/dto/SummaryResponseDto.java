package com.projeto.EndividamentoCalculo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponseDto {
    private Long userId;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal totalDebt;
    private BigDecimal monthlyInstallmentsSum;
    private BigDecimal debtRatio; // monthlyInstallmentsSum / totalIncome
    private String classification; // BAIXO / MEDIO / ALTO
    private List<RepaymentPlanDto> suggestedPlans;
}
