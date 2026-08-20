package com.verify_x.dto;

import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriticalDocumentDto {

    private DocumentType documentType;

    private DocumentStatus status;

    private String statusMessage;
}