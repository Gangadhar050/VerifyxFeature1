package com.verify_x.serviceImpl;

import com.verify_x.dto.ToolPlatformDto;
import com.verify_x.dto.ToolPlatformResponseDto;
import com.verify_x.dto.UpdateToolPlatformRequestDto;
import com.verify_x.entity.Candidate;

import com.verify_x.enums.ToolPlatform;
import com.verify_x.exception.BadRequestException;
import com.verify_x.exception.ResourceNotFoundException;
import com.verify_x.jwt.UserPrincipal;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.services.CandidateToolPlatformService;

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
public class CandidateToolPlatformServiceImpl
        implements CandidateToolPlatformService {

    private final CandidateRepository candidateRepository;

    @Override
    public ToolPlatformResponseDto addToolPlatforms(
            UpdateToolPlatformRequestDto request) {

        Candidate candidate = getLoggedInCandidate();

        if (request == null ||
                request.getToolPlatforms() == null ||
                request.getToolPlatforms().isEmpty()) {

            throw new BadRequestException(
                    "At least one tool/platform is required."
            );
        }

        List<ToolPlatform> requestedTools =
                request.getToolPlatforms()
                        .stream()
                        .filter(tool -> tool != null)
                        .distinct()
                        .toList();

        // No role-based validation
        // Candidate can add ANY ToolPlatform

        // Get existing selected tools
        Set<ToolPlatform> existingTools =
                getSelectedTools(candidate);

        // Add new tools without deleting existing tools
        existingTools.addAll(requestedTools);

        // Save
        candidate.setToolPlatforms(existingTools);

        candidateRepository.save(candidate);

        return getToolPlatforms();
    }
    //get all tools
    @Override
    @Transactional(readOnly = true)
    public ToolPlatformResponseDto getToolPlatforms() {

        Candidate candidate =
                getLoggedInCandidate();

        Set<ToolPlatform> selectedTools =
                getSelectedTools(candidate);

        List<ToolPlatform> recommendedTools =
                getRecommendations(
                        candidate.getAppliedRole()
                );

        List<ToolPlatform> roleScopedTools =
                recommendedTools.isEmpty()
                        ? Arrays.asList(ToolPlatform.values())
                        : recommendedTools;

        List<ToolPlatformDto> tools =
                roleScopedTools.stream()
                        .distinct()
                        .map(tool ->
                                ToolPlatformDto.builder()
                                        .value(tool.name())
                                        .name(formatName(tool))
                                        .selected(
                                                selectedTools.contains(tool)
                                        )
                                        .recommended(
                                                recommendedTools.contains(tool)
                                        )
                                        .build()
                        )
                        .toList();

        return ToolPlatformResponseDto.builder()
                .candidateId(candidate.getId())
                .appliedRole(candidate.getAppliedRole())
                .tools(tools)
                .build();
    }

//save or update tools
@Override
public ToolPlatformResponseDto updateToolPlatforms(
        UpdateToolPlatformRequestDto request) {

    Candidate candidate = getLoggedInCandidate();

    if (request == null ||
            request.getToolPlatforms() == null ||
            request.getToolPlatforms().isEmpty()) {

        throw new BadRequestException(
                "At least one tool/platform is required."
        );
    }

    List<ToolPlatform> requestedTools =
            request.getToolPlatforms()
                    .stream()
                    .filter(tool -> tool != null)
                    .distinct()
                    .toList();

    // Allow ANY tool/platform
    // No role validation

    Set<ToolPlatform> existingTools =
            getSelectedTools(candidate);

    // Add/update without deleting previous tools
    existingTools.addAll(requestedTools);

    candidate.setToolPlatforms(existingTools);

    candidateRepository.save(candidate);

    return getToolPlatforms();
}

    @Override
    @Transactional(readOnly = true)
    public List<ToolPlatform> searchToolPlatforms(
            String keyword) {

        if (keyword == null ||
                keyword.trim().isEmpty()) {

            return Arrays.stream(
                    ToolPlatform.values()
            ).toList();
        }

        String searchKeyword =
                keyword.trim()
                        .toLowerCase(Locale.ROOT);

        return Arrays.stream(
                        ToolPlatform.values()
                )
                .filter(tool -> {

                    String enumName =
                            tool.name()
                                    .toLowerCase(Locale.ROOT);

                    String displayName =
                            formatName(tool)
                                    .toLowerCase(Locale.ROOT);

                    return enumName.contains(searchKeyword) || displayName.contains(searchKeyword);}).toList();
    }

    //recomended
    @Override
    @Transactional(readOnly = true)
    public ToolPlatformResponseDto
    getRecommendedToolPlatforms() {

        Candidate candidate =
                getLoggedInCandidate();

        Set<ToolPlatform> selectedTools =
                getSelectedTools(candidate);

        List<ToolPlatform> recommendedTools =
                getRecommendations(
                        candidate.getAppliedRole()
                );

        List<ToolPlatformDto> tools =
                recommendedTools.stream()
                        .distinct()
                        .map(tool ->
                                ToolPlatformDto.builder()
                                        .value(tool.name())
                                        .name(formatName(tool))
                                        .selected(
                                                selectedTools.contains(tool)
                                        )
                                        .recommended(true)
                                        .build()
                        )
                        .toList();

        return ToolPlatformResponseDto.builder()
                .candidateId(candidate.getId())
                .appliedRole(
                        candidate.getAppliedRole()
                )
                .tools(tools)
                .build();
    }

    @Override
    public ToolPlatformResponseDto removeToolPlatform(
            ToolPlatform toolPlatform) {

        if (toolPlatform == null) {

            throw new BadRequestException(
                    "Tool/platform is required."
            );
        }

        Candidate candidate =
                getLoggedInCandidate();

        Set<ToolPlatform> selectedTools =
                getSelectedTools(candidate);

        if (!selectedTools.contains(toolPlatform)) {

            throw new ResourceNotFoundException(
                    "Tool/platform not selected: "
                            + toolPlatform.name()
            );
        }

        selectedTools.remove(toolPlatform);

        candidate.setToolPlatforms(
                selectedTools
        );

        candidateRepository.save(candidate);

        return getToolPlatforms();
    }

   //authenticate candidate
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

   //selected tools
    private Set<ToolPlatform> getSelectedTools(
            Candidate candidate) {

        if (candidate.getToolPlatforms() == null) {

            Set<ToolPlatform> tools =
                    new LinkedHashSet<>();

            candidate.setToolPlatforms(tools);

            return tools;
        }

        return new LinkedHashSet<>(
                candidate.getToolPlatforms()
        );
    }

//role validation
    private void validateToolsBelongToRole(
            List<ToolPlatform> tools,
            Candidate.AppliedRole role) {

        if (tools == null || tools.isEmpty()) {
            return;
        }

        if (role == null) {

            throw new BadRequestException(
                    "Applied role must be selected before adding tools and platforms."
            );
        }

        List<ToolPlatform> allowed =
                getRecommendations(role);

        List<ToolPlatform> invalid =
                tools.stream()
                        .filter(tool ->
                                !allowed.contains(tool)
                        )
                        .toList();

        if (!invalid.isEmpty()) {

            throw new BadRequestException(
                    "The following tools/platforms are not allowed "
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

    //role recommendation
    private List<ToolPlatform> getRecommendations(
            Candidate.AppliedRole role) {

        if (role == null) {

            return Collections.emptyList();
        }

        return switch (role) {

            case FRONTEND_DEVELOPER -> List.of(
                    ToolPlatform.VS_CODE,
                    ToolPlatform.NPM,
                    ToolPlatform.YARN,
                    ToolPlatform.PNPM,
                    ToolPlatform.GIT,
                    ToolPlatform.GITHUB,
                    ToolPlatform.FIGMA,
                    ToolPlatform.CHROME_DEVTOOLS,
                    ToolPlatform.VERCEL,
                    ToolPlatform.NETLIFY
            );

            case BACKEND_DEVELOPER -> List.of(
                    ToolPlatform.VS_CODE,
                    ToolPlatform.INTELLIJ_IDEA,
                    ToolPlatform.MAVEN,
                    ToolPlatform.GRADLE,
                    ToolPlatform.GIT,
                    ToolPlatform.GITHUB,
                    ToolPlatform.POSTMAN,
                    ToolPlatform.SWAGGER_OPENAPI,
                    ToolPlatform.TESTCONTAINERS,
                    ToolPlatform.DOCKER,
                    ToolPlatform.DOCKER_COMPOSE,
                    ToolPlatform.JENKINS,
                    ToolPlatform.GITHUB_ACTIONS,
                    ToolPlatform.SONARQUBE
            );

            case FULL_STACK_DEVELOPER -> List.of(
                    ToolPlatform.VS_CODE,
                    ToolPlatform.INTELLIJ_IDEA,
                    ToolPlatform.NPM,
                    ToolPlatform.YARN,
                    ToolPlatform.PNPM,
                    ToolPlatform.GIT,
                    ToolPlatform.GITHUB,
                    ToolPlatform.FIGMA,
                    ToolPlatform.CHROME_DEVTOOLS,
                    ToolPlatform.POSTMAN,
                    ToolPlatform.SWAGGER_OPENAPI,
                    ToolPlatform.MAVEN,
                    ToolPlatform.GRADLE,
                    ToolPlatform.DOCKER,
                    ToolPlatform.DOCKER_COMPOSE,
                    ToolPlatform.VERCEL,
                    ToolPlatform.NETLIFY
            );

            case DATA_ANALYST -> List.of(
                    ToolPlatform.VS_CODE,
                    ToolPlatform.JUPYTER_NOTEBOOK,
                    ToolPlatform.GOOGLE_COLAB,
                    ToolPlatform.MYSQL_WORKBENCH,
                    ToolPlatform.PGADMIN,
                    ToolPlatform.MONGODB_COMPASS,
                    ToolPlatform.GIT,
                    ToolPlatform.GITHUB
            );

            case DEVOPS_ENGINEER -> List.of(
                    ToolPlatform.GIT,
                    ToolPlatform.GITHUB,
                    ToolPlatform.GITHUB_ACTIONS,
                    ToolPlatform.GITLAB_CI_CD,
                    ToolPlatform.JENKINS,
                    ToolPlatform.DOCKER,
                    ToolPlatform.DOCKER_COMPOSE,
                    ToolPlatform.KUBERNETES,
                    ToolPlatform.HELM,
                    ToolPlatform.TERRAFORM,
                    ToolPlatform.ANSIBLE,
                    ToolPlatform.AWS,
                    ToolPlatform.MICROSOFT_AZURE,
                    ToolPlatform.GOOGLE_CLOUD,
                    ToolPlatform.ARGO_CD,
                    ToolPlatform.SONARQUBE,
                    ToolPlatform.PROMETHEUS,
                    ToolPlatform.GRAFANA
            );

            case QA_ENGINEER -> List.of(
                    ToolPlatform.VS_CODE,
                    ToolPlatform.POSTMAN,
                    ToolPlatform.SWAGGER_OPENAPI,
                    ToolPlatform.TESTCONTAINERS,
                    ToolPlatform.GIT,
                    ToolPlatform.GITHUB,
                    ToolPlatform.JENKINS,
                    ToolPlatform.GITHUB_ACTIONS,
                    ToolPlatform.SONARQUBE
            );
        };
    }

    //display name
    private String formatName(
            ToolPlatform tool) {

        return Arrays.stream(
                        tool.name().split("_")
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