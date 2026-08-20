package com.verify_x.repository;

import com.verify_x.entity.Candidate;
import com.verify_x.entity.Employment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {

    Optional<Employment> findByCandidate(Candidate candidate);

    Optional<Employment> findByCandidateId(Long candidateId);

    boolean existsByCandidate(Candidate candidate);

    void deleteByCandidate(Candidate candidate);

}
