package com.verify_x.serviceImpl;

import com.verify_x.dto.EmploymentDetailsDto;
import com.verify_x.entity.Candidate;
import com.verify_x.entity.Employment;

import com.verify_x.enums.CandidateType;
import com.verify_x.enums.EmploymentStatus;
import com.verify_x.enums.OfferLetterStatus;
import com.verify_x.jwt.UserPrincipal;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.repository.EmploymentRepository;

import com.verify_x.services.EmploymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmploymentServiceImpl implements EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final CandidateRepository candidateRepository;


    private EmploymentDetailsDto mapToDto(Employment employment){

        return EmploymentDetailsDto.builder()

                .previousCompanyName(employment.getPreviousCompanyName())
                .previousDesignation(employment.getPreviousDesignation())
                .totalExperience(employment.getTotalExperience())
                .lastCTC(employment.getLastCTC())
                .lastWorkingDay(employment.getLastWorkingDay())
                .uanNumber(employment.getUanNumber())

                .employmentStatus(employment.getEmploymentStatus())
                .currentCompany(employment.getCurrentCompany())
                .currentDesignation(employment.getCurrentDesignation())
                .currentCTC(employment.getCurrentCTC())
                .noticePeriod(employment.getNoticePeriod())

                .offerLetterStatus(employment.getOfferLetterStatus())
//                .offerCompanyName(employment.getOfferCompanyName())
//                .offeredCTC(employment.getOfferedCTC())
//                .joiningDate(employment.getJoiningDate())
//                .offerReferenceNumber(employment.getOfferReferenceNumber())

                .build();
    }
    @Override
    public void saveEmploymentDetails(EmploymentDetailsDto dto) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        Candidate candidate = candidateRepository.findById(principal.getUserId())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Candidate not found"));

        validateEmploymentDetails(candidate, dto);

        Employment employment = employmentRepository
                .findByCandidate(candidate)
                .orElse(new Employment());

        employment.setCandidate(candidate);


        // Candidate Type
        if (candidate.getCandidateType() == CandidateType.EXPERIENCED) {

            employment.setPreviousCompanyName(dto.getPreviousCompanyName());
            employment.setPreviousDesignation(dto.getPreviousDesignation());
            employment.setTotalExperience(dto.getTotalExperience());
            employment.setLastCTC(dto.getLastCTC());
            employment.setLastWorkingDay(dto.getLastWorkingDay());
            employment.setUanNumber(dto.getUanNumber());

            employment.setEmploymentStatus(dto.getEmploymentStatus());

            if (dto.getEmploymentStatus() == EmploymentStatus.CURRENTLY_EMPLOYED) {

                employment.setCurrentCompany(dto.getCurrentCompany());
                employment.setCurrentDesignation(dto.getCurrentDesignation());
                employment.setCurrentCTC(dto.getCurrentCTC());
                employment.setNoticePeriod(dto.getNoticePeriod());

            } else {

                employment.setCurrentCompany(null);
                employment.setCurrentDesignation(null);
                employment.setCurrentCTC(0.0);
                employment.setNoticePeriod(null);
            }

        } else {

            // Fresher
            employment.setPreviousCompanyName(null);
            employment.setPreviousDesignation(null);
            employment.setTotalExperience(null);
            employment.setLastCTC(null);
            employment.setLastWorkingDay(null);
            employment.setUanNumber(null);

            employment.setEmploymentStatus(null);

            employment.setCurrentCompany(null);
            employment.setCurrentDesignation(null);
            employment.setCurrentCTC(null);
            employment.setNoticePeriod(null);
        }


        // Offer Letter
//        employment.setOfferLetterStatus(dto.getOfferLetterStatus());
//
//        if (dto.getOfferLetterStatus() == OfferLetterStatus.HOLDING_OFFER_LETTER) {
//
//            employment.setOfferCompanyName(dto.getOfferCompanyName());
//            employment.setOfferedCTC(dto.getOfferedCTC());
//            employment.setJoiningDate(dto.getJoiningDate());
//            employment.setOfferReferenceNumber(dto.getOfferReferenceNumber());
//
//        } else {
//
//            employment.setOfferCompanyName(null);
//            employment.setOfferedCTC(null);
//            employment.setJoiningDate(null);
//            employment.setOfferReferenceNumber(null);
//        }

        employmentRepository.save(employment);
    }

    @Override
    public void updateEmploymentDetails(EmploymentDetailsDto dto) {

        saveEmploymentDetails(dto);

    }

    @Override
    public EmploymentDetailsDto getEmploymentDetailsByCandidateId(Long candidateId) {

        Employment employment = employmentRepository.findByCandidateId(candidateId)
                .orElseThrow(() ->
                        new RuntimeException("Employment details not found"));

        return mapToDto(employment);
    }

    @Override
    public EmploymentDetailsDto getEmploymentDetailsByEmail(String email) {

        Candidate candidate = candidateRepository.findByEmail(email)                .orElseThrow(() ->
                        new RuntimeException("Candidate not found"));

        Employment employment = employmentRepository.findByCandidate(candidate)
                .orElseThrow(() ->
                        new RuntimeException("Employment details not found"));

        return mapToDto(employment);
    }

    @Override
    public List<EmploymentDetailsDto> searchEmploymentDetails(String keyword) {

        return candidateRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword,
                        keyword
                )
                .stream()
                .map(candidate -> employmentRepository.findByCandidate(candidate)
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void deleteEmploymentDetails(Long candidateId) {

        Employment employment = employmentRepository.findByCandidateId(candidateId)
                .orElseThrow(() ->
                        new RuntimeException("Employment details not found"));

        employmentRepository.delete(employment);
    }

    private void validateEmploymentDetails(Candidate candidate,
                                           EmploymentDetailsDto dto) {



        //  Experienced Candidate Validation

        if (candidate.getCandidateType() == CandidateType.EXPERIENCED) {

            if (dto.getPreviousCompanyName() == null ||
                    dto.getPreviousCompanyName().isBlank()) {
                throw new RuntimeException("Previous Company Name is required.");
            }

            if (dto.getPreviousDesignation() == null ||
                    dto.getPreviousDesignation().isBlank()) {
                throw new RuntimeException("Previous Designation is required.");
            }

            if (dto.getTotalExperience() == null) {
                throw new RuntimeException("Total Experience is required.");
            }

            if (dto.getLastCTC() == null) {
                throw new RuntimeException("Last CTC is required.");
            }

            if (dto.getLastWorkingDay() == null) {
                throw new RuntimeException("Last Working Day is required.");
            }

            if (dto.getUanNumber() == null ||
                    dto.getUanNumber().isBlank()) {
                throw new RuntimeException("UAN Number is required.");
            }

            // Current Employment

            if (dto.getEmploymentStatus() == null) {
                throw new RuntimeException("Employment Status is required.");
            }

            if (dto.getEmploymentStatus() == EmploymentStatus.CURRENTLY_EMPLOYED) {

                if (dto.getCurrentCompany() == null ||
                        dto.getCurrentCompany().isBlank()) {
                    throw new RuntimeException("Current Company is required.");
                }

                if (dto.getCurrentDesignation() == null ||
                        dto.getCurrentDesignation().isBlank()) {
                    throw new RuntimeException("Current Designation is required.");
                }

                if (dto.getCurrentCTC() == null) {
                    throw new RuntimeException("Current CTC is required.");
                }

                if (dto.getNoticePeriod() == null) {
                    throw new RuntimeException("Notice Period is required.");
                }
            }
        }


//      Offer Letter Validation

//        if (dto.getOfferLetterStatus() == null) {
//            throw new RuntimeException("Offer Letter Status is required.");
//        }

//        if (dto.getOfferLetterStatus() == OfferLetterStatus.HOLDING_OFFER_LETTER) {
//
//            if (dto.getOfferCompanyName() == null ||
//                    dto.getOfferCompanyName().isBlank()) {
//                throw new RuntimeException("Offer Company Name is required.");
//            }
//
//            if (dto.getOfferedCTC() == null) {
//                throw new RuntimeException("Offered CTC is required.");
//            }
//
//            if (dto.getJoiningDate() == null) {
//                throw new RuntimeException("Joining Date is required.");
//            }
//
//            if (dto.getOfferReferenceNumber() == null ||
//                    dto.getOfferReferenceNumber().isBlank()) {
//                throw new RuntimeException("Offer Reference Number is required.");
//            }
//        }
    }
}