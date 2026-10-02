package com.votrebanque.infrastructure.config;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.aop.framework.ProxyFactory;

import com.votrebanque.application.port.inbound.*;
import com.votrebanque.application.port.outbound.*;
import com.votrebanque.application.service.*;
import com.votrebanque.domain.validator.PasswordPolicyValidator;

@Configuration
public class ApplicationBeansConfig {

    @Bean
    public TransactionRecorder transactionRecorder(TransactionRepositoryPort transactionRepository) {
        return new TransactionRecorder(transactionRepository);
    }

    @Bean
    public AccrueInterestUseCase accrueInterestUseCase(
            AccountRepositoryPort accountRepository,
            TransactionRecorder transactionRecorder,
            PlatformTransactionManager transactionManager) {
        return transactional(
            new AccrueInterestService(accountRepository, transactionRecorder),
            transactionManager,
            false
        );
    }

    @Bean
    public ActivateAccountUseCase activateAccountUseCase(
            CredentialsRepositoryPort credentialsRepository,
            ActivationTokenRepositoryPort tokenRepository,
            PasswordEncoderPort passwordEncoder,
            PasswordPolicyValidator passwordPolicyValidator,
            PlatformTransactionManager transactionManager) {
        return transactional(
            new ActivateAccountService(
                credentialsRepository, tokenRepository, passwordEncoder, passwordPolicyValidator
            ),
            transactionManager,
            false
        );
    }

    @Bean
    public AddBeneficiaryUseCase addBeneficiaryUseCase(
            AccountRepositoryPort accountRepository,
            BeneficiaryRepositoryPort beneficiaryRepository) {
        return new AddBeneficiaryService(accountRepository, beneficiaryRepository);
    }

    @Bean
    public CancelDirectDebitUseCase cancelDirectDebitUseCase(
            DirectDebitRepositoryPort repository,
            PlatformTransactionManager transactionManager) {
        return transactional(new CancelDirectDebitService(repository), transactionManager, false);
    }

    @Bean
    public CheckAccountAccessUseCase checkAccountAccessUseCase(
            CredentialsRepositoryPort credentialsRepository,
            LinkedSavingsAccountRepositoryPort linkedSavingsAccountRepository) {
        return new CheckAccountAccessService(credentialsRepository, linkedSavingsAccountRepository);
    }

    @Bean
    public CreateDirectDebitUseCase createDirectDebitUseCase(
            DirectDebitRepositoryPort directDebitRepository,
            AccountRepositoryPort accountRepository,
            BeneficiaryRepositoryPort beneficiaryRepository,
            PlatformTransactionManager transactionManager) {
        return transactional(
            new CreateDirectDebitService(directDebitRepository, accountRepository, beneficiaryRepository),
            transactionManager,
            false
        );
    }

    @Bean
    public ExecuteDirectDebitsUseCase executeDirectDebitsUseCase(
            DirectDebitRepositoryPort directDebitRepository,
            AccountRepositoryPort accountRepository,
            TransactionRecorder transactionRecorder,
            PlatformTransactionManager transactionManager) {
        return transactional(
            new ExecuteDirectDebitsService(directDebitRepository, accountRepository, transactionRecorder),
            transactionManager,
            false
        );
    }

    @Bean
    public GetAccountSummaryUseCase getAccountSummaryUseCase(
            AccountRepositoryPort accountRepository,
            PlatformTransactionManager transactionManager) {
        return transactional(new GetAccountSummaryService(accountRepository), transactionManager, true);
    }

    @Bean
    public GetActivationEmailPreviewUseCase getActivationEmailPreviewUseCase(EmailPreviewPort emailPreviewPort) {
        return new GetActivationEmailPreviewService(emailPreviewPort);
    }

    @Bean
    public GetBeneficiariesUseCase getBeneficiariesUseCase(
            BeneficiaryRepositoryPort beneficiaryRepository,
            AccountRepositoryPort accountRepository) {
        return new GetBeneficiariesService(beneficiaryRepository, accountRepository);
    }

    @Bean
    public GetDirectDebitsUseCase getDirectDebitsUseCase(DirectDebitRepositoryPort repository) {
        return new GetDirectDebitsService(repository);
    }

    @Bean
    public GetLinkedSavingsAccountsUseCase getLinkedSavingsAccountsUseCase(
            CredentialsRepositoryPort credentialsRepository,
            LinkedSavingsAccountRepositoryPort linkedSavingsAccountRepository,
            AccountRepositoryPort accountRepository) {
        return new GetLinkedSavingsAccountsService(credentialsRepository, linkedSavingsAccountRepository, accountRepository);
    }

    @Bean
    public GetMyAccountUseCase getMyAccountUseCase(
            CredentialsRepositoryPort credentialsRepository,
            GetAccountSummaryUseCase getAccountSummaryUseCase) {
        return new GetMyAccountService(credentialsRepository, getAccountSummaryUseCase);
    }

    @Bean
    public GetTransactionHistoryUseCase getTransactionHistoryUseCase(
            TransactionRepositoryPort transactionRepository,
            PlatformTransactionManager transactionManager) {
        return transactional(new GetTransactionHistoryService(transactionRepository), transactionManager, true);
    }

    @Bean
    public LoginUseCase loginUseCase(
            CredentialsRepositoryPort credentialsRepository,
            StaffAuthenticationPort staffAuthenticationPort,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider) {
        return new LoginService(credentialsRepository, staffAuthenticationPort, passwordEncoder, tokenProvider);
    }

    @Bean
    public OpenAccountUseCase openAccountUseCase(
            AccountRepositoryPort accountRepository,
            LinkedSavingsAccountRepositoryPort linkedSavingsAccountRepository,
            RegisterUserUseCase registerUserUseCase,
            AddBeneficiaryUseCase addBeneficiaryUseCase,
            TransactionRecorder transactionRecorder,
            PlatformTransactionManager transactionManager) {
        return transactional(
            new OpenAccountService(accountRepository, linkedSavingsAccountRepository, registerUserUseCase,
                addBeneficiaryUseCase, transactionRecorder),
            transactionManager,
            false
        );
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            CredentialsRepositoryPort credentialsRepository,
            ActivationTokenRepositoryPort tokenRepository,
            PasswordEncoderPort passwordEncoder,
            EmailSenderPort emailSender,
            @Value("${app.frontend.base-url}") String frontendBaseUrl) {
        return new RegisterUserService(
            credentialsRepository, tokenRepository, passwordEncoder, emailSender, frontendBaseUrl
        );
    }

    @Bean
    public TransferUseCase transferUseCase(
            AccountRepositoryPort accountRepository,
            BeneficiaryRepositoryPort beneficiaryRepository,
            TransactionRecorder transactionRecorder,
            PlatformTransactionManager transactionManager) {
        return transactional(
            new TransferService(accountRepository, beneficiaryRepository, transactionRecorder),
            transactionManager,
            false
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T transactional(T target, PlatformTransactionManager transactionManager, boolean readOnly) {
        DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute();
        transactionAttribute.setReadOnly(readOnly);

        TransactionAttributeSource attributeSource = (Method method, Class<?> targetClass) -> transactionAttribute;
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(attributeSource);

        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(interceptor);
        return (T) proxyFactory.getProxy();
    }
}
