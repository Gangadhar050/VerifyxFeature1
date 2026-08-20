package com.verify_x.services;

import com.verify_x.dto.OfferLetterRequestDTO;

import com.verify_x.dto.OfferLetterResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OfferLetterService {

    OfferLetterResponseDTO createOfferLetter(
            Long candidateId,
            OfferLetterRequestDTO request,
            MultipartFile file
    );

    List<OfferLetterResponseDTO> getOfferLetters(
            Long candidateId
    );

    OfferLetterResponseDTO getOfferLetter(
            Long candidateId,
            Long offerLetterId
    );

    OfferLetterResponseDTO updateOfferLetter(
            Long candidateId,
            Long offerLetterId,
            OfferLetterRequestDTO request,
            MultipartFile file
    );

    void deleteOfferLetter(
            Long candidateId,
            Long offerLetterId
    );

    DocumentDownload getDocument(
            Long candidateId,
            Long offerLetterId
    );

    record DocumentDownload(
            byte[] data,
            String fileName,
            String contentType
    ) {
    }
}