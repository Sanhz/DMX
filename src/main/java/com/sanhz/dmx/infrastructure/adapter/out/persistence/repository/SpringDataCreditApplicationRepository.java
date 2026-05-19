package com.sanhz.dmx.infrastructure.adapter.out.persistence.repository;

import com.sanhz.dmx.infrastructure.adapter.out.persistence.entity.CreditApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataCreditApplicationRepository extends JpaRepository<CreditApplicationEntity, UUID> {
}
