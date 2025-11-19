package com.projeto.EndividamentoCalculo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentPlanDto {
    private String planType;
    private Integer months;
    private BigDecimal monthlyPayment;
    private BigDecimal totalPayment;
}
