package com.verify_x.serviceImpl;

import com.verify_x.dto.HRDocumentDto;
import com.verify_x.dto.PagedResponse;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;
import com.verify_x.repository.CandidateDocumentRepository;
import com.verify_x.services.HRDocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HRDocumentServiceImpl implements HRDocumentService {

    private final CandidateDocumentRepository candidateDocumentRepository;

    private HRDocumentDto mapToDto(CandidateDocument document) {
        return HRDocumentDto.builder()
                .documentId(document.getId())
                .candidateId(document.getCandidate().getId())
                .candidateName(document.getCandidate().getUsername())
                .candidateEmail(document.getCandidate().getEmail())
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .status(document.getStatus())
                .rejectionReason(document.getRejectionReason())
                .uploadedAt(document.getUploadedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    @Override
    public PagedResponse<HRDocumentDto> getAllDocuments(
            String keyword,
            DocumentStatus status,
            DocumentType documentType,
            int page,
            int size
    ) {
        String normalizedKeyword = (keyword == null || keyword.isBlank())
                ? null
                : keyword.trim().toLowerCase();

        List<HRDocumentDto> filtered = candidateDocumentRepository.findAll()
                .stream()
                .filter(doc -> status == null || doc.getStatus() == status)
                .filter(doc -> documentType == null || doc.getDocumentType() == documentType)
                .filter(doc -> normalizedKeyword == null
                        || doc.getCandidate().getUsername().toLowerCase().contains(normalizedKeyword)
                        || doc.getCandidate().getEmail().toLowerCase().contains(normalizedKeyword)
                        || doc.getFileName().toLowerCase().contains(normalizedKeyword))
                .map(this::mapToDto)
                .sorted(Comparator.comparing(HRDocumentDto::getUploadedAt).reversed())
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : size;

        int fromIndex = Math.min(safePage * safeSize, filtered.size());
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());

        List<HRDocumentDto> pageContent = filtered.subList(fromIndex, toIndex);

        int totalPages = (int) Math.ceil((double) filtered.size() / safeSize);

        return PagedResponse.<HRDocumentDto>builder()
                .content(pageContent)
                .page(safePage)
                .size(safeSize)
                .totalElements(filtered.size())
                .totalPages(totalPages)
                .build();
    }
}
