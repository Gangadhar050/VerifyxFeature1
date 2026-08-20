package com.verify_x.services;

import com.verify_x.dto.EducationRequest;
import com.verify_x.dto.EducationResponse;
//import com.verify_x.dto.DocumentFormDto;
import com.verify_x.enums.EducationDocumentType;
import com.verify_x.enums.TechnicalSkill;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EducationService {

//    EducationResponse saveEducation(EducationRequest request);
//
//    EducationResponse updateEducation(EducationRequest request);
//
//    EducationResponse getMyEducation();
//
//    EducationResponse getEducationByCandidateId(Long candidateId);
//
//    Resource viewDocument(Long educationId, EducationDocumentType documentType);
//
////    DocumentFormDto getDocumentForm(Long educationId, EducationDocumentType documentType);
//
//    List<TechnicalSkill> getMyTechnicalSkills();
//
//    void deleteEducation();
    EducationResponse autoFillFromDocument(MultipartFile file, EducationDocumentType documentType,EducationRequest request);
    EducationResponse extractAllAndSave(
            MultipartFile tenthMarksCard,
            MultipartFile twelfthMarksCard,
            MultipartFile degreeCertificate,
            MultipartFile mastersMarksCard);

//    EducationResponse updateEducation(EducationRequest request);

    EducationResponse getMyEducation();

    void deleteEducation();

   EducationResponse updateEducation(EducationRequest request,
                                      MultipartFile tenthMarksCard,
                                      MultipartFile twelfthMarksCard,
                                      MultipartFile degreeCertificate,
                                      MultipartFile mastersMarksCard);
}