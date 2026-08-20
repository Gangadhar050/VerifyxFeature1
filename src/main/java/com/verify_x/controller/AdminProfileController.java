package com.verify_x.controller;

import com.verify_x.dto.AdminProfileResponse;
import com.verify_x.dto.AdminProfileUpdateRequest;
import com.verify_x.services.AdminProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    @GetMapping
    public ResponseEntity<AdminProfileResponse> getProfile() {

        return ResponseEntity.ok(
                adminProfileService.getProfile()
        );
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @io.swagger.v3.oas.annotations.media.Encoding(
                                    name = "profile",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    }
            )
    )
    public ResponseEntity<AdminProfileResponse> updateProfile(

            @RequestPart(value = "profile", required = false)
            AdminProfileUpdateRequest request,

            @RequestPart(value = "profilePhoto", required = false)
            MultipartFile profilePhoto) {

        return ResponseEntity.ok(
                adminProfileService.updateProfile(
                        request,
                        profilePhoto
                )
        );
    }
}
