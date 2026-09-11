package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.DirectDebit;
import java.util.List;

public interface GetDirectDebitsUseCase {
    List<DirectDebit> getDirectDebits(AccountId accountNumber);
}
