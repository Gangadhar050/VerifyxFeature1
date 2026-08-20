package com.verify_x.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDocumentRequest {

    private MultipartFile resume;

    private MultipartFile offerLetter;

    private MultipartFile salarySlip;

    private MultipartFile relievingLetter;

    private MultipartFile experienceLetter;

    private MultipartFile panCard;

    private MultipartFile aadhaarCard;

    private MultipartFile uanProof;

}