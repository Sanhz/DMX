package com.sanhz.dmx.domain.service;

import com.sanhz.dmx.domain.enums.CreditStatus;
import com.sanhz.dmx.domain.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditStatusTransitionValidatorTest {

    private final CreditStatusTransitionValidator validator = new CreditStatusTransitionValidator();

    @Test
    void shouldAllowCreatedToUnderReview() {

        assertDoesNotThrow(() ->
                validator.validate(
                        CreditStatus.CREATED,
                        CreditStatus.UNDER_REVIEW
                )
        );

    }

    @Test
    void shouldRejectCreatedToApproved() {

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> validator.validate(
                        CreditStatus.CREATED,
                        CreditStatus.APPROVED
                )
        );

    }

    @Test
    void shouldAllowUnderReviewToApproved() {

        assertDoesNotThrow(() ->
                validator.validate(
                        CreditStatus.UNDER_REVIEW,
                        CreditStatus.APPROVED
                )
        );

    }

    @Test
    void shouldRejectApprovedToCreated() {

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> validator.validate(
                        CreditStatus.APPROVED,
                        CreditStatus.CREATED
                ));

    }
}
