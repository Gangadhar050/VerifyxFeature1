package com.verify_x.services;

import com.verify_x.dto.AdminProfileResponse;
import com.verify_x.dto.AdminProfileUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AdminProfileService {

    AdminProfileResponse getProfile();

    AdminProfileResponse updateProfile(
            AdminProfileUpdateRequest request,
            MultipartFile profilePhoto
    );
}
