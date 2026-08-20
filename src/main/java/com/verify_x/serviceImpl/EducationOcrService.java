package com.verify_x.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.verify_x.enums.EducationDocumentType;
import com.verify_x.exception.BadRequestException;
import com.verify_x.util.GeminiEducationParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EducationOcrService {

    private final GeminiEducationParser geminiEducationParser;
    private final ObjectMapper objectMapper;

    @Value("${ocr.timeout-seconds:45}")
    private int timeoutSeconds;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/png", "image/jpg"
    );

    private static final Map<EducationDocumentType, List<String>> REQUIRED_FIELDS = Map.of(
            EducationDocumentType.TENTH_MARKS_CARD, List.of(
                    "tenthSchoolName", "tenthBoard", "tenthSchoolLocation",
                    "tenthRollNumber", "tenthPassingYear", "tenthPercentage"),
            EducationDocumentType.TWELFTH_MARKS_CARD, List.of(
                    "twelfthInstitutionName", "twelfthBoardUniversity", "twelfthStream",
                    "twelfthRegistrationNumber", "twelfthPassingYear", "twelfthPercentage"),
            EducationDocumentType.DEGREE_CERTIFICATE, List.of(
                    "degreeName", "specialization", "collegeName", "universityName",
                    "usnNumber", "degreeStartYear", "degreeEndYear", "degreePercentage"),
            EducationDocumentType.MASTERS_MARKS_CARD, List.of(
                    "mastersDegree", "mastersSpecialization", "mastersCollege", "mastersUniversity",
                    "mastersRegistrationNumber", "mastersStartYear", "mastersEndYear", "mastersPercentage"),
            EducationDocumentType.MASTERS_DEGREE_CERTIFICATE, List.of(
                    "mastersDegree", "mastersSpecialization", "mastersCollege", "mastersUniversity",
                    "mastersRegistrationNumber", "mastersStartYear", "mastersEndYear", "mastersPercentage")
    );

    private final ExecutorService executor = Executors.newCachedThreadPool();

    // Combined extraction across all marksheet/certificate types in one call
    public Map<String, Object> extractAll(
            MultipartFile tenthMarksCard,
            MultipartFile twelfthMarksCard,
            MultipartFile degreeCertificate,
            MultipartFile mastersMarksCard) {

        Map<String, MultipartFile> uploads = new LinkedHashMap<>();
        if (tenthMarksCard != null && !tenthMarksCard.isEmpty())
            uploads.put("TENTH_MARKS_CARD", tenthMarksCard);
        if (twelfthMarksCard != null && !twelfthMarksCard.isEmpty())
            uploads.put("TWELFTH_MARKS_CARD", twelfthMarksCard);
        if (degreeCertificate != null && !degreeCertificate.isEmpty())
            uploads.put("DEGREE_CERTIFICATE", degreeCertificate);
        if (mastersMarksCard != null && !mastersMarksCard.isEmpty())
            uploads.put("MASTERS_MARKS_CARD", mastersMarksCard);
//        if (mastersDegreeCertificate != null && !mastersDegreeCertificate.isEmpty())
//            uploads.put("MASTERS_DEGREE_CERTIFICATE", mastersDegreeCertificate);

        if (uploads.isEmpty()) {
            throw new BadRequestException("At least one marksheet/certificate file is required.");
        }

        Map<String, Object> sections = new LinkedHashMap<>();
        boolean overallComplete = true;

        for (Map.Entry<String, MultipartFile> entry : uploads.entrySet()) {

            EducationDocumentType type = EducationDocumentType.valueOf(entry.getKey());
            MultipartFile file = entry.getValue();

            try {
                validate(file);

                String geminiResponse = runWithTimeout(() ->
                        geminiEducationParser.parseEducationDocument(file, type));

                Map<String, Object> result = buildResult(geminiResponse, type);
                sections.put(entry.getKey(), result);

                if (!Boolean.TRUE.equals(result.get("complete"))) {
                    overallComplete = false;
                }

            } catch (Exception e) {
                log.error("Extraction failed for {} ({})", entry.getKey(), file.getOriginalFilename(), e);

                Map<String, Object> errorResult = new LinkedHashMap<>();
                errorResult.put("fields", new LinkedHashMap<>());
                errorResult.put("complete", false);
                errorResult.put("missingFields", REQUIRED_FIELDS.getOrDefault(type, List.of()));
                errorResult.put("error", "Could not process this document. Please fill manually.");

                sections.put(entry.getKey(), errorResult);
                overallComplete = false;
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sections", sections);
        response.put("allComplete", overallComplete);

        return response;
    }

    private Map<String, Object> buildResult(String geminiResponse, EducationDocumentType documentType) {

        Map<String, String> fields = parseJsonSafely(geminiResponse);

        List<String> required = REQUIRED_FIELDS.getOrDefault(documentType, List.of());
        List<String> missing = new ArrayList<>();

        for (String key : required) {
            String value = fields.get(key);
            if (value == null || value.isBlank()) {
                missing.add(key);
                fields.putIfAbsent(key, "");}
        }

        boolean complete = missing.isEmpty();

        if (!complete) {
            log.warn("Incomplete extraction for {}: missing {}", documentType, missing);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fields", fields);
        result.put("complete", complete);
        result.put("missingFields", missing);

        return result;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required for extraction.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Maximum allowed file size is 5 MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only PDF, JPG, JPEG and PNG files are supported.");
        }
    }

    private String runWithTimeout(Callable<String> task) throws Exception {
        Future<String> future = executor.submit(task);
        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        }
    }

    private Map<String, String> parseJsonSafely(String geminiResponse) {
        if (geminiResponse == null || geminiResponse.isBlank()) {
            return new LinkedHashMap<>();
        }
        int start = geminiResponse.indexOf('{');
        int end = geminiResponse.lastIndexOf('}');
        if (start == -1 || end == -1 || end < start) {
            log.warn("No JSON object found in Gemini response: {}", geminiResponse);
            return new LinkedHashMap<>();
        }
        String jsonOnly = geminiResponse.substring(start, end + 1);
        try {
            return objectMapper.readValue(jsonOnly, new TypeReference<LinkedHashMap<String, String>>() {});
        } catch (Exception e) {
            log.error("Failed to parse extracted JSON: {}", jsonOnly, e);
            return new LinkedHashMap<>();
        }
    }
}