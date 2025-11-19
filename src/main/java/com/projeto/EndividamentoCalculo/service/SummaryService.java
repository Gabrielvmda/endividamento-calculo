package com.projeto.EndividamentoCalculo.service;

import com.projeto.EndividamentoCalculo.dto.RepaymentPlanDto;
import com.projeto.EndividamentoCalculo.dto.SummaryResponseDto;
import com.projeto.EndividamentoCalculo.model.Debt;
import com.projeto.EndividamentoCalculo.model.Income;
import com.projeto.EndividamentoCalculo.model.Expense;
import com.projeto.EndividamentoCalculo.model.User;
import com.projeto.EndividamentoCalculo.repository.DebtRepository;
import com.projeto.EndividamentoCalculo.repository.IncomeRepository;
import com.projeto.EndividamentoCalculo.repository.ExpenseRepository;
import com.projeto.EndividamentoCalculo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    private final UserRepository userRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final DebtRepository debtRepository;

    @Autowired
    public SummaryService(UserRepository userRepository,
                          IncomeRepository incomeRepository,
                          ExpenseRepository expenseRepository,
                          DebtRepository debtRepository) {
        this.userRepository = userRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.debtRepository = debtRepository;
    }

    /**
     * Gera o resumo financeiro e os planos FLEX (6, 12, 24, 36 meses).
     */
    @Transactional
    public SummaryResponseDto generateSummaryForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        // Total de renda mensal
        List<Income> incomes = incomeRepository.findAll()
                .stream()
                .filter(i -> i.getUser() != null && i.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        BigDecimal totalIncome = incomes.stream()
                .map(Income::getMonthlyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total de despesas mensais
        List<Expense> expenses = expenseRepository.findAll()
                .stream()
                .filter(e -> e.getUser() != null && e.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getMonthlyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Dívidas
        List<Debt> debts = debtRepository.findAll()
                .stream()
                .filter(d -> d.getUser() != null && d.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        BigDecimal totalDebt = debts.stream()
                .map(Debt::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Soma das parcelas mínimas atuais
        BigDecimal monthlyInstallmentsSum = debts.stream()
                .map(d -> Optional.ofNullable(d.getMinimumInstallment()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Índice de comprometimento mensal
        BigDecimal debtRatio = BigDecimal.ZERO;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            debtRatio = monthlyInstallmentsSum.divide(totalIncome, 4, RoundingMode.HALF_UP);
        }

        String classification = classifyDebtRatio(debtRatio);

        // Gerar somente os planos FLEX
        List<RepaymentPlanDto> plans = generateFlexiblePlans(debts);

        return new SummaryResponseDto(
                userId,
                totalIncome.setScale(2, RoundingMode.HALF_UP),
                totalExpenses.setScale(2, RoundingMode.HALF_UP),
                totalDebt.setScale(2, RoundingMode.HALF_UP),
                monthlyInstallmentsSum.setScale(2, RoundingMode.HALF_UP),
                debtRatio.setScale(4, RoundingMode.HALF_UP),
                classification,
                plans
        );
    }

    private String classifyDebtRatio(BigDecimal ratio) {
        BigDecimal low = new BigDecimal("0.3");
        BigDecimal mid = new BigDecimal("0.5");

        if (ratio.compareTo(low) < 0) return "BAIXO";
        if (ratio.compareTo(mid) <= 0) return "MEDIO";
        return "ALTO";
    }

    /**
     * Gera planos FLEX (6, 12, 24 e 36 meses) usando taxa média ponderada das dívidas.
     */
    private List<RepaymentPlanDto> generateFlexiblePlans(List<Debt> debts) {
        List<RepaymentPlanDto> flexiblePlans = new ArrayList<>();

        if (debts == null || debts.isEmpty()) {
            flexiblePlans.add(new RepaymentPlanDto("FLEX_6", 6, BigDecimal.ZERO, BigDecimal.ZERO));
            flexiblePlans.add(new RepaymentPlanDto("FLEX_12", 12, BigDecimal.ZERO, BigDecimal.ZERO));
            flexiblePlans.add(new RepaymentPlanDto("FLEX_24", 24, BigDecimal.ZERO, BigDecimal.ZERO));
            flexiblePlans.add(new RepaymentPlanDto("FLEX_36", 36, BigDecimal.ZERO, BigDecimal.ZERO));
            return flexiblePlans;
        }

        // Soma total dos principais
        BigDecimal totalPrincipal = debts.stream()
                .map(d -> Optional.ofNullable(d.getTotalAmount()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Média ponderada de juros
        BigDecimal weightedRateSum = BigDecimal.ZERO;
        for (Debt d : debts) {
            BigDecimal principal = Optional.ofNullable(d.getTotalAmount()).orElse(BigDecimal.ZERO);
            BigDecimal rate = Optional.ofNullable(d.getAnnualInterestRate()).orElse(BigDecimal.ZERO);
            weightedRateSum = weightedRateSum.add(rate.multiply(principal));
        }

        BigDecimal avgAnnualRate = BigDecimal.ZERO;
        if (totalPrincipal.compareTo(BigDecimal.ZERO) > 0) {
            avgAnnualRate = weightedRateSum.divide(totalPrincipal, 8, RoundingMode.HALF_UP);
        }

        int[] options = {6, 12, 24, 36};
        for (int months : options) {
            BigDecimal monthly = calculateMonthlyPayment(totalPrincipal, avgAnnualRate, months);
            BigDecimal totalPayment = monthly.multiply(new BigDecimal(months));

            flexiblePlans.add(new RepaymentPlanDto(
                    "FLEX_" + months,
                    months,
                    monthly.setScale(2, RoundingMode.HALF_UP),
                    totalPayment.setScale(2, RoundingMode.HALF_UP)
            ));
        }

        return flexiblePlans;
    }

    /**
     * Fórmula de amortização para calcular prestação fixa.
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal annualRate, int months) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0 || months <= 0) {
            return BigDecimal.ZERO;
        }

        double monthlyRate = annualRate == null ? 0.0 : annualRate.doubleValue() / 12.0;
        if (monthlyRate == 0.0) {
            return principal.divide(new BigDecimal(months), 8, RoundingMode.HALF_UP);
        }

        double P = principal.doubleValue();
        double r = monthlyRate;
        double n = months;

        double denominator = 1 - Math.pow(1 + r, -n);
        if (denominator == 0) {
            return BigDecimal.valueOf(P / n).setScale(8, RoundingMode.HALF_UP);
        }

        double A = P * r / denominator;
        return BigDecimal.valueOf(A).setScale(8, RoundingMode.HALF_UP);
    }
}
