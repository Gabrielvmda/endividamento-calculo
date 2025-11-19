package com.projeto.EndividamentoCalculo.model;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "repayment_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Debt debt;

    private String planType;

    private Integer months;

    private BigDecimal monthlyPayment;

    private BigDecimal totalPayment;

    private LocalDateTime createdAt = LocalDateTime.now();
}
