package com.janindu.ticket.repository;

import com.janindu.ticket.model.MainConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository extends JpaRepository<MainConfig, Long> {
}
