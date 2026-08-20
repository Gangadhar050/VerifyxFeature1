package com.verify_x.services;

import com.verify_x.dto.CandidateSummaryDto;
import com.verify_x.dto.VerificationQueueItemDto;

import java.util.List;

public interface VerificationService {


    List<VerificationQueueItemDto> getVerificationQueue();
}
