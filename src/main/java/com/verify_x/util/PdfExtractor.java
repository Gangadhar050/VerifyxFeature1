//package com.verify_x.util;
//
//import com.verify_x.exception.BadRequestException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.pdfbox.Loader;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@Component
//@Slf4j
//public class PdfExtractor {
//
//    private static final int MAX_PAGES = 15;
//
//    public String extractText(MultipartFile file) throws IOException {
//
//        if (file == null || file.isEmpty()) {
//            throw new BadRequestException("File is empty.");
//        }
//
//        String contentType = file.getContentType();
//        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
//            throw new BadRequestException(
//                    "Only PDF files are supported for auto-fill. "
//                            + "Please upload a text-based PDF, or fill the fields manually for image uploads.");
//        }
//
//        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
//
//            if (document.isEncrypted()) {
//                throw new BadRequestException("Encrypted/password-protected PDFs are not supported.");
//            }
//
//            if (document.getNumberOfPages() > MAX_PAGES) {
//                throw new BadRequestException("PDF has too many pages for a marksheet/certificate upload.");
//            }
//
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(document);
//
//            if (text == null || text.isBlank()) {
//                throw new BadRequestException(
//                        "No readable text found in this PDF. It may be a scanned image "
//                                + "without a text layer, which is not supported for auto-fill.");
//            }
//
//            log.debug("Extracted {} characters from PDF {}", text.length(), file.getOriginalFilename());
//
//            return text;
//
//        } catch (IOException e) {
//            log.error("Failed to load/parse PDF: {}", file.getOriginalFilename(), e);
//            throw new BadRequestException("Unable to read this PDF file. It may be corrupted.");
//        }
//    }
//}