package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.ExecuteDirectDebitsUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.DirectDebitRepositoryPort;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.exception.InsufficientFundsException;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.DirectDebit;
import com.votrebanque.domain.model.TransactionCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class ExecuteDirectDebitsService implements ExecuteDirectDebitsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteDirectDebitsService.class);

    private final DirectDebitRepositoryPort directDebitRepository;
    private final AccountRepositoryPort accountRepository;
    private final TransactionRecorder transactionRecorder;

    public ExecuteDirectDebitsService(DirectDebitRepositoryPort directDebitRepository,
                                       AccountRepositoryPort accountRepository,
                                       TransactionRecorder transactionRecorder) {
        this.directDebitRepository = directDebitRepository;
        this.accountRepository = accountRepository;
        this.transactionRecorder = transactionRecorder;
    }

    @Override
    public int executeDueDirectDebits() {
        LocalDate today = LocalDate.now();
        List<DirectDebit> dueDirectDebits = directDebitRepository.findAllDueOn(today);
        int executedCount = 0;

        for (DirectDebit directDebit : dueDirectDebits) {
            try {
                executeOne(directDebit);
                directDebit.advanceToNextExecution();
                directDebitRepository.save(directDebit);
                executedCount++;
            } catch (InsufficientFundsException | AccountNotFoundException e) {
                log.warn("Direct debit {} skipped this cycle: {}", directDebit.id(), e.getMessage());
                // The direct debit remains active: a new attempt will be made on the next scheduled due date.
            }
        }

        return executedCount;
    }

    private void executeOne(DirectDebit directDebit) {
        Account source = accountRepository.findByNumber(directDebit.sourceAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Direct debit source account not found."));
        Account destination = accountRepository.findByNumber(directDebit.beneficiaryAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Direct debit beneficiary account not found."));

        source.debit(directDebit.amount());
        destination.credit(directDebit.amount());

        accountRepository.save(source);
        accountRepository.save(destination);

        transactionRecorder.recordTransfer(
            source.accountNumber(), source.balance(),
            destination.accountNumber(), destination.balance(),
            directDebit.amount(), source.owner(), destination.owner(),
            TransactionCategory.DIRECT_DEBIT,
            "Direct debit: " + directDebit.beneficiaryLabel()
        );
    }
}
