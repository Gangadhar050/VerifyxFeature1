package com.verify_x.repository;

import com.verify_x.entity.Candidate;
import com.verify_x.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    Optional<Education> findByCandidate(Candidate candidate);

    Optional<Education> findByCandidateId(Long candidateId);

    boolean existsByCandidate(Candidate candidate);

    void deleteByCandidate(Candidate candidate);

}