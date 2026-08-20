package com.verify_x.serviceImpl;

import com.verify_x.dto.TechnicalSkillDto;
import com.verify_x.dto.TechnicalSkillResponseDto;
import com.verify_x.dto.UpdateTechnicalSkillRequestDto;
import com.verify_x.entity.Candidate;
import com.verify_x.enums.AppliedRole;
import com.verify_x.enums.TechnicalSkill;
import com.verify_x.exception.BadRequestException;
import com.verify_x.exception.ResourceNotFoundException;
import com.verify_x.jwt.UserPrincipal;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.services.CandidateSkillService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateSkillServiceImpl
        implements CandidateSkillService {

    private final CandidateRepository candidateRepository;

    //get all technical skills
    @Override
    @Transactional(readOnly = true)
    public TechnicalSkillResponseDto getTechnicalSkills() {

        Candidate candidate = getLoggedInCandidate();

        Set<TechnicalSkill> selectedSkills =
                getSelectedSkills(candidate);

        List<TechnicalSkill> recommendedSkills =
                getRecommendations(candidate.getAppliedRole());

        List<TechnicalSkill> roleScopedSkills =
                recommendedSkills.isEmpty()
                        ? Arrays.asList(TechnicalSkill.values())
                        : recommendedSkills;

        List<TechnicalSkillDto> skills =
                roleScopedSkills.stream()
                        .distinct()
                        .map(skill ->
                                TechnicalSkillDto.builder()
                                        .value(skill.name())
                                        .name(formatSkillName(skill))
                                        .selected(
                                                selectedSkills.contains(skill)
                                        )
                                        .recommended(
                                                recommendedSkills.contains(skill)
                                        )
                                        .build()
                        )
                        .toList();

        return TechnicalSkillResponseDto.builder()
                .candidateId(candidate.getId())
                .appliedRole(
                        candidate.getAppliedRole() != null
                                ? candidate.getAppliedRole().name()
                                : null
                )
                .skills(skills)
                .build();
    }
    @Override
    public TechnicalSkillResponseDto addTechnicalSkills(
            UpdateTechnicalSkillRequestDto request) {

        Candidate candidate = getLoggedInCandidate();

        if (request == null ||
                request.getTechnicalSkills() == null ||
                request.getTechnicalSkills().isEmpty()) {

            throw new BadRequestException(
                    "At least one technical skill is required."
            );
        }

        List<TechnicalSkill> requestedSkills =
                request.getTechnicalSkills()
                        .stream()
                        .filter(skill -> skill != null)
                        .distinct()
                        .toList();

        // Get existing skills
        Set<TechnicalSkill> existingSkills =
                getSelectedSkills(candidate);

        // Add ANY requested technical skills
        existingSkills.addAll(requestedSkills);

        candidate.setTechnicalSkills(existingSkills);

        candidateRepository.save(candidate);

        return getTechnicalSkills();
    }

    @Override
    public TechnicalSkillResponseDto updateTechnicalSkills(
            UpdateTechnicalSkillRequestDto request) {

        Candidate candidate = getLoggedInCandidate();

        if (request == null ||
                request.getTechnicalSkills() == null) {

            throw new BadRequestException(
                    "Technical skills are required."
            );
        }

        List<TechnicalSkill> requestedSkills =
                request.getTechnicalSkills()
                        .stream()
                        .filter(skill -> skill != null)
                        .distinct()
                        .toList();

        // No role-based validation
        // Candidate can select ANY TechnicalSkill enum value
        Set<TechnicalSkill> uniqueSkills =
                new LinkedHashSet<>(requestedSkills);

        // Replace existing skills
        candidate.setTechnicalSkills(uniqueSkills);

        candidateRepository.save(candidate);

        return getTechnicalSkills();
    }

   //REMOVE SINGLE TECHNICAL SKILL
    @Override
    public TechnicalSkillResponseDto removeTechnicalSkill(
            TechnicalSkill skill) {

        if (skill == null) {

            throw new BadRequestException(
                    "Technical skill is required."
            );
        }

        Candidate candidate =
                getLoggedInCandidate();

        Set<TechnicalSkill> selectedSkills =
                getSelectedSkills(candidate);

        if (!selectedSkills.contains(skill)) {

            throw new ResourceNotFoundException(
                    "Technical skill not selected: "
                            + skill.name()
            );
        }

        selectedSkills.remove(skill);

        candidate.setTechnicalSkills(
                selectedSkills
        );

        candidateRepository.save(candidate);

        return getTechnicalSkills();
    }

   //SEARCH TECHNICAL SKILLS
    @Override
    @Transactional(readOnly = true)
    public List<TechnicalSkill> searchTechnicalSkills(
            String keyword) {

        if (keyword == null ||
                keyword.trim().isEmpty()) {

            return Arrays.stream(
                    TechnicalSkill.values()
            ).toList();
        }

        String searchKeyword =
                keyword.trim()
                        .toLowerCase(Locale.ROOT);

        return Arrays.stream(
                        TechnicalSkill.values()
                )
                .filter(skill -> {

                    String enumName =
                            skill.name()
                                    .toLowerCase(Locale.ROOT);

                    String displayName =
                            formatSkillName(skill)
                                    .toLowerCase(Locale.ROOT);

                    return enumName.contains(
                            searchKeyword
                    ) ||
                            displayName.contains(
                                    searchKeyword
                            );
                })
                .toList();
    }

    //GET RECOMMENDED SKILLS
    @Override
    @Transactional(readOnly = true)
    public TechnicalSkillResponseDto getRecommendedSkills() {

        Candidate candidate =
                getLoggedInCandidate();

        Set<TechnicalSkill> selectedSkills =
                getSelectedSkills(candidate);

        List<TechnicalSkill> recommendedSkills =
                getRecommendations(
                        candidate.getAppliedRole()
                );

        List<TechnicalSkillDto> skills =
                recommendedSkills.stream()
                        .distinct()
                        .map(skill ->
                                TechnicalSkillDto.builder()
                                        .value(skill.name())
                                        .name(formatSkillName(skill))
                                        .selected(
                                                selectedSkills.contains(skill)
                                        )
                                        .recommended(true)
                                        .build()
                        )
                        .toList();

        return TechnicalSkillResponseDto.builder()
                .candidateId(candidate.getId())
                .appliedRole(
                        candidate.getAppliedRole() != null
                                ? candidate.getAppliedRole().name()
                                : null
                )
                .skills(skills)
                .build();
    }

   //GET AUTHENTICATED CANDIDATE
    private Candidate getLoggedInCandidate() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new BadRequestException(
                    "Authentication is required."
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal)) {

            throw new BadRequestException(
                    "Invalid authenticated candidate."
            );
        }

        UserPrincipal userPrincipal =
                (UserPrincipal) principal;

        Long userId =
                userPrincipal.getUserId();

        if (userId == null) {

            throw new BadRequestException(
                    "Candidate ID is missing from authentication."
            );
        }

        return candidateRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Candidate not found."
                        )
                );
    }

  //GET SELECTED SKILLS
    private Set<TechnicalSkill> getSelectedSkills(
            Candidate candidate) {

        if (candidate.getTechnicalSkills() == null) {

            Set<TechnicalSkill> skills =
                    new LinkedHashSet<>();

            candidate.setTechnicalSkills(skills);

            return skills;
        }

        return new LinkedHashSet<>(
                candidate.getTechnicalSkills()
        );
    }

//VALIDATE SKILLS AGAINST ROLE
    private void validateSkillsBelongToRole(
            List<TechnicalSkill> skills,
            AppliedRole role) {

        if (skills == null || skills.isEmpty()) {
            return;
        }

        if (role == null) {

            throw new BadRequestException(
                    "Applied role must be selected before adding technical skills."
            );
        }

        List<TechnicalSkill> allowed =
                getRecommendations(role);

        List<TechnicalSkill> invalid =
                skills.stream()
                        .filter(skill ->
                                !allowed.contains(skill)
                        )
                        .toList();

        if (!invalid.isEmpty()) {

            throw new BadRequestException(
                    "The following technical skills are not allowed "
                            + "for "
                            + role.name()
                            + ": "
                            + invalid.stream()
                            .map(Enum::name)
                            .collect(
                                    Collectors.joining(", ")
                            )
            );
        }
    }

 //ROLE RECOMMENDATIONS
    private List<TechnicalSkill> getRecommendations(
            AppliedRole role) {

        if (role == null) {

            return Collections.emptyList();
        }

        return switch (role) {

            case FRONTEND_DEVELOPER -> List.of(
                    TechnicalSkill.HTML,
                    TechnicalSkill.CSS,
                    TechnicalSkill.JAVASCRIPT,
                    TechnicalSkill.TYPESCRIPT,
                    TechnicalSkill.REACT_JS,
                    TechnicalSkill.NEXT_JS,
                    TechnicalSkill.ANGULAR,
                    TechnicalSkill.VUE_JS,
                    TechnicalSkill.NUXT_JS,
                    TechnicalSkill.SVELTE,
                    TechnicalSkill.BOOTSTRAP,
                    TechnicalSkill.TAILWIND_CSS,
                    TechnicalSkill.MATERIAL_UI
            );

            case BACKEND_DEVELOPER -> List.of(
                    TechnicalSkill.JAVA,
                    TechnicalSkill.SPRING,
                    TechnicalSkill.SPRING_BOOT,
                    TechnicalSkill.SPRING_MVC,
                    TechnicalSkill.SPRING_SECURITY,
                    TechnicalSkill.SPRING_CLOUD,
                    TechnicalSkill.HIBERNATE,
                    TechnicalSkill.JPA,
                    TechnicalSkill.PYTHON,
                    TechnicalSkill.NODE_JS,
                    TechnicalSkill.EXPRESS_JS,
                    TechnicalSkill.SQL,
                    TechnicalSkill.MYSQL,
                    TechnicalSkill.POSTGRESQL,
                    TechnicalSkill.MONGODB,
                    TechnicalSkill.REDIS,
                    TechnicalSkill.REST_API,
                    TechnicalSkill.GRAPHQL,
                    TechnicalSkill.APACHE_KAFKA,
                    TechnicalSkill.RABBITMQ,
                    TechnicalSkill.DOCKER,
                    TechnicalSkill.GIT,
                    TechnicalSkill.MAVEN,
                    TechnicalSkill.GRADLE,
                    TechnicalSkill.MICROSERVICES
            );

            case FULL_STACK_DEVELOPER -> List.of(
                    TechnicalSkill.HTML,
                    TechnicalSkill.CSS,
                    TechnicalSkill.JAVASCRIPT,
                    TechnicalSkill.TYPESCRIPT,
                    TechnicalSkill.REACT_JS,
                    TechnicalSkill.NEXT_JS,
                    TechnicalSkill.ANGULAR,
                    TechnicalSkill.VUE_JS,
                    TechnicalSkill.JAVA,
                    TechnicalSkill.SPRING_BOOT,
                    TechnicalSkill.NODE_JS,
                    TechnicalSkill.EXPRESS_JS,
                    TechnicalSkill.PYTHON,
                    TechnicalSkill.SQL,
                    TechnicalSkill.MYSQL,
                    TechnicalSkill.POSTGRESQL,
                    TechnicalSkill.MONGODB,
                    TechnicalSkill.REDIS,
                    TechnicalSkill.REST_API,
                    TechnicalSkill.GRAPHQL,
                    TechnicalSkill.DOCKER,
                    TechnicalSkill.GIT,
                    TechnicalSkill.MICROSERVICES
            );

            case DATA_ANALYST -> List.of(
                    TechnicalSkill.PYTHON,
                    TechnicalSkill.SQL,
                    TechnicalSkill.MYSQL,
                    TechnicalSkill.POSTGRESQL,
                    TechnicalSkill.MONGODB,
                    TechnicalSkill.PANDAS,
                    TechnicalSkill.NUMPY,
                    TechnicalSkill.MATPLOTLIB,
                    TechnicalSkill.JUPYTER,
                    TechnicalSkill.POWER_BI,
                    TechnicalSkill.TABLEAU
            );

            case DEVOPS_ENGINEER -> List.of(
                    TechnicalSkill.LINUX,
                    TechnicalSkill.BASH,
                    TechnicalSkill.POWERSHELL,
                    TechnicalSkill.GIT,
                    TechnicalSkill.GITHUB,
                    TechnicalSkill.GITLAB,
                    TechnicalSkill.DOCKER,
                    TechnicalSkill.KUBERNETES,
                    TechnicalSkill.HELM,
                    TechnicalSkill.JENKINS,
                    TechnicalSkill.GITHUB_ACTIONS,
                    TechnicalSkill.GITLAB_CI_CD,
                    TechnicalSkill.TERRAFORM,
                    TechnicalSkill.ANSIBLE,
                    TechnicalSkill.AWS,
                    TechnicalSkill.AWS_EC2,
                    TechnicalSkill.AWS_S3,
                    TechnicalSkill.AZURE,
                    TechnicalSkill.GOOGLE_CLOUD
            );

            case QA_ENGINEER -> List.of(
                    TechnicalSkill.MANUAL_TESTING,
                    TechnicalSkill.AUTOMATION_TESTING,
                    TechnicalSkill.JUNIT,
                    TechnicalSkill.TESTNG,
                    TechnicalSkill.MOCKITO,
                    TechnicalSkill.SELENIUM,
                    TechnicalSkill.CYPRESS,
                    TechnicalSkill.PLAYWRIGHT,
                    TechnicalSkill.POSTMAN,
                    TechnicalSkill.REST_ASSURED,
                    TechnicalSkill.JAVA,
                    TechnicalSkill.PYTHON,
                    TechnicalSkill.SQL
            );
        };
    }

    //DISPLAY NAME
    private String formatSkillName(
            TechnicalSkill skill) {

        return Arrays.stream(
                        skill.name().split("_")
                )
                .map(word -> {

                    if (word.isEmpty()) {
                        return word;
                    }

                    return word.substring(0, 1)
                            + word.substring(1)
                            .toLowerCase(Locale.ROOT);
                })
                .collect(
                        Collectors.joining(" ")
                );
    }
}