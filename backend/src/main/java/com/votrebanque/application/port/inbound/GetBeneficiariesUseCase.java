package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import java.util.List;

public interface GetBeneficiariesUseCase {
    List<BeneficiarySummary> getBeneficiaries(AccountId accountId);
}
