package com.verify_x.util;

import com.verify_x.enums.EducationDocumentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Slf4j
public class GeminiEducationParser {

    private final ChatClient chatClient;

    public GeminiEducationParser(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String parseEducationDocument(MultipartFile file, EducationDocumentType documentType) {

        String promptText = switch (documentType) {
            case TENTH_MARKS_CARD -> tenthPrompt();
            case TWELFTH_MARKS_CARD -> twelfthPrompt();
            case DEGREE_CERTIFICATE -> degreePrompt();
            case MASTERS_MARKS_CARD, MASTERS_DEGREE_CERTIFICATE -> mastersPrompt();
        };

        try {
            Media media = Media.builder()
                    .mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
                    .data(file.getResource())
                    .build();

            String response = chatClient.prompt()
                    .user(u -> u.text(promptText).media(media))
                    .call()
                    .content();

            if (response == null || response.isBlank()) {
                throw new RuntimeException("Gemini returned an empty response.");
            }

            response = response.replace("```json", "").replace("```", "").trim();

            log.debug("Gemini response for {}: {}", documentType, response);

            return response;

        } catch (Exception e) {
            log.error("Gemini call failed for documentType {}", documentType, e);
            throw new RuntimeException("Failed to parse education document using Gemini: " + e.getMessage(), e);
        }
    }

    private String commonRules() {
        return """
                Rules:
                1. Your ENTIRE response must be a single valid JSON object and NOTHING else.
                2. Do NOT return markdown, ```json fences, explanations, or any text before/after the JSON.
                3. Start your response with { and end with } - no other characters.
                4. You MUST include EVERY key listed below in your response, even if the value is
                   an empty string "". Never omit a key.
                5. Look carefully at the ENTIRE document before answering - check headers, tables,
                   and footer/signature sections, not just the first visible line of text.
                6. Return only 4-digit years for any year field (e.g. "2017"), as plain numbers with no extra text.
                7. For percentage fields, return only the numeric value with no % sign and no text
                   (e.g. "89.60" not "89.60%"). If the document shows CGPA/SGPA only, return that raw number.
                8. Do not confuse Date of Birth with Passing Year/Start Year/End Year. Passing Year
                   is specifically near labels like "Year of Passing", "Month & Year of Exam", or a
                   board seal/issue date - never a birthdate field.
                9. Institution/College/University name must be the actual name, not boilerplate
                   header text like "SCHOOL CODE, NAME AND ADDRESS".
                """;
    }

    private String tenthPrompt() {
        return """
                You are an AI that extracts information from Indian 10th/SSLC marksheets.
                Look directly at the attached document image/PDF and extract the details.
                
                Return ONLY a valid JSON object with exactly these keys:
                {
                  "tenthSchoolName": "",
                  "tenthBoard": "",
                  "tenthSchoolLocation": "",
                  "tenthRollNumber": "",
                  "tenthPassingYear": "",
                  "tenthPercentage": ""
                }
                
                """ + commonRules();
    }

    private String twelfthPrompt() {
        return """
                You are an AI that extracts information from Indian 12th/PUC marksheets.
                Look directly at the attached document image/PDF and extract the details.
                
                Return ONLY a valid JSON object with exactly these keys:
                {
                  "twelfthInstitutionName": "",
                  "twelfthBoardUniversity": "",
                  "twelfthStream": "",
                  "twelfthLocation": "",
                  "twelfthRegistrationNumber": "",
                  "twelfthPassingYear": "",
                  "twelfthPercentage": ""
                }
                
                """ + commonRules();
    }

    private String degreePrompt() {
        return """
                You are an AI that extracts information from Indian degree/bachelor's certificates
                or marksheets. Look directly at the attached document image/PDF and extract the details.
                
                Return ONLY a valid JSON object with exactly these keys:
                {
                  "degreeName": "",
                  "specialization": "",
                  "collegeName": "",
                  "universityName": "",
                  "degreeLocation": "",
                  "usnNumber": "",
                  "degreeStartYear": "",
                  "degreeEndYear": "",
                  "degreePercentage": ""
                }
                
                """ + commonRules();
    }

    private String mastersPrompt() {
        return """
                You are an AI that extracts information from Indian master's degree certificates
                or marksheets. Look directly at the attached document image/PDF and extract the details.
                
                Return ONLY a valid JSON object with exactly these keys:
                {
                  "mastersDegree": "",
                  "mastersSpecialization": "",
                  "mastersCollege": "",
                  "mastersUniversity": "",
                  "mastersLocation": "",
                  "mastersRegistrationNumber": "",
                  "mastersStartYear": "",
                  "mastersEndYear": "",
                  "mastersPercentage": ""
                }
                
                """ + commonRules();
    }
}