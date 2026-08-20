package com.verify_x.payload;

import com.verify_x.dto.CandidateDocumentDto;
import com.verify_x.dto.CandidateEducationDto;
import com.verify_x.dto.CandidateProfileDto;
import com.verify_x.entity.CandidateDocument;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timeStamp;

    public ApiResponse(boolean b, String s, CandidateDocument document) {
    }

    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    // Candidate Profile
    public ApiResponse(boolean success, String message, CandidateProfileDto profile) {
        this.success = success;
        this.message = message;
        this.data = (T) profile;
        this.timeStamp = LocalDateTime.now();
    }

    // Candidate Education
    public ApiResponse(boolean success, String message, CandidateEducationDto education) {
        this.success = success;
        this.message = message;
        this.data = (T) education;
        this.timeStamp = LocalDateTime.now();
    }

    // Candidate Document
    public ApiResponse(boolean success, String message, CandidateDocumentDto document) {
        this.success = success;
        this.message = message;
        this.data = (T) document;
        this.timeStamp = LocalDateTime.now();
    }
}
