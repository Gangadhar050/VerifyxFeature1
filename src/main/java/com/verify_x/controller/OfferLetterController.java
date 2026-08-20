package com.verify_x.controller;

import com.verify_x.dto.OfferLetterRequestDTO;

import com.verify_x.dto.OfferLetterResponseDTO;
import com.verify_x.services.OfferLetterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/candidates/{candidateId}/offer-letters")
@RequiredArgsConstructor
public class OfferLetterController {

    private final OfferLetterService offerLetterService;


    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<OfferLetterResponseDTO>
    createOfferLetter(

            @PathVariable Long candidateId,

            @Valid
            @ModelAttribute("data")
            OfferLetterRequestDTO request,

            @ModelAttribute("file")
            MultipartFile file
    ) {

        OfferLetterResponseDTO response =
                offerLetterService.createOfferLetter(
                        candidateId,
                        request,
                        file
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<List<OfferLetterResponseDTO>>
    getOfferLetters(

            @PathVariable Long candidateId
    ) {

        return ResponseEntity.ok(
                offerLetterService.getOfferLetters(
                        candidateId
                )
        );
    }


    @GetMapping("/{offerLetterId}")
    public ResponseEntity<OfferLetterResponseDTO>
    getOfferLetter(

            @PathVariable Long candidateId,

            @PathVariable Long offerLetterId
    ) {

        return ResponseEntity.ok(
                offerLetterService.getOfferLetter(
                        candidateId,
                        offerLetterId
                )
        );
    }

    @PutMapping(
            value = "/{offerLetterId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<OfferLetterResponseDTO>
    updateOfferLetter(

            @PathVariable Long candidateId,

            @PathVariable Long offerLetterId,

            @Valid
            @RequestPart("data")
            OfferLetterRequestDTO request,

            @RequestPart(
                    value = "file",
                    required = false
            )
            MultipartFile file
    ) {

        return ResponseEntity.ok(
                offerLetterService.updateOfferLetter(
                        candidateId,
                        offerLetterId,
                        request,
                        file));
    }

    @GetMapping("/{offerLetterId}/document")
    public ResponseEntity<ByteArrayResource>
    downloadDocument(

            @PathVariable Long candidateId,

            @PathVariable Long offerLetterId
    ) {

        OfferLetterService.DocumentDownload document =
                offerLetterService.getDocument(
                        candidateId,
                        offerLetterId
                );

        ByteArrayResource resource =
                new ByteArrayResource(
                        document.data()
                );

        MediaType mediaType;

        try {

            mediaType = MediaType.parseMediaType(
                    document.contentType()
            );

        } catch (Exception e) {

            mediaType =
                    MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(
                        document.data().length
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .inline()
                                .filename(
                                        document.fileName())
                                .build()
                                .toString())
                .body(resource);
    }

    @DeleteMapping("/{offerLetterId}")
    public ResponseEntity<Void>
    deleteOfferLetter(

            @PathVariable Long candidateId,

            @PathVariable Long offerLetterId
    ) {

        offerLetterService.deleteOfferLetter(
                candidateId,
                offerLetterId
        );

        return ResponseEntity.noContent().build();
    }
}