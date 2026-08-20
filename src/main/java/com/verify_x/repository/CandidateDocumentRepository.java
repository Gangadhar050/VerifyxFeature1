package com.verify_x.repository;

import com.verify_x.entity.Candidate;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateDocumentRepository extends JpaRepository<CandidateDocument, Long>{

    List<CandidateDocument> findByCandidate(Candidate candidate);

    Optional<CandidateDocument> findByCandidateAndDocumentType(Candidate candidate, DocumentType documentType);

    List<CandidateDocument> findByCandidateId(Long candidateId);

    List<CandidateDocument> findByStatus(DocumentStatus status);

    List<CandidateDocument> findByCandidateUsernameContainingIgnoreCaseOrCandidateEmailContainingIgnoreCase(
            String username,
            String email);

    boolean existsByCandidateAndDocumentType(Candidate candidate, DocumentType documentType);

    void deleteByCandidateAndDocumentType(Candidate candidate, DocumentType documentType);

    // Reports Dashboard
    long countByStatus(DocumentStatus status);

    Long countByCandidate(Candidate candidate);
}
