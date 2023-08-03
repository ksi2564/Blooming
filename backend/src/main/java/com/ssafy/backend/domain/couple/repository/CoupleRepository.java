package com.ssafy.backend.domain.couple.repository;

import com.ssafy.backend.domain.couple.Couple;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

    Optional<Couple> findByCoupleCode(Integer coupleCode);
}
