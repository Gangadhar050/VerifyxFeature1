package com.verify_x.repository;

import com.verify_x.entity.OfferLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferLetterRepository
        extends JpaRepository<OfferLetter, Long> {

    List<OfferLetter> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);

    Optional<OfferLetter> findByIdAndCandidateId(Long id, Long candidateId);

    long countByCandidateId(Long candidateId);
}