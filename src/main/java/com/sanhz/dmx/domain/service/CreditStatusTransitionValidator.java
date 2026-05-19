package com.sanhz.dmx.domain.service;

import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.exception.InvalidStatusTransitionException;

import java.util.Map;
import java.util.Set;

public class CreditStatusTransitionValidator {

    private static final Map<CreditStatus, Set<CreditStatus>>
            ALLOWED_TRANSITIONS =
            Map.of(
                    CreditStatus.CREATED,
                    Set.of(
                            CreditStatus.UNDER_REVIEW,
                            CreditStatus.CANCELLED
                    ),

                    CreditStatus.UNDER_REVIEW,
                    Set.of(
                            CreditStatus.APPROVED,
                            CreditStatus.REJECTED,
                            CreditStatus.CANCELLED
                    )
            );

    public void validate(
            CreditStatus currentStatus,
            CreditStatus newStatus
    ) {

        Set<CreditStatus> allowed =
                ALLOWED_TRANSITIONS.get(currentStatus);

        if (
                allowed == null
                        || !allowed.contains(newStatus)
        ) {

            throw new InvalidStatusTransitionException(
                    "Invalid transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

}
