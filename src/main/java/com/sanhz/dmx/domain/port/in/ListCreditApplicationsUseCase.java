package com.sanhz.dmx.domain.port.in;

import com.sanhz.dmx.domain.model.CreditApplicationPage;

public interface ListCreditApplicationsUseCase {

    CreditApplicationPage list(
            int page,
            int size
    );

}
