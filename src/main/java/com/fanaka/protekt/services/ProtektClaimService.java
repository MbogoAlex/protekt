package com.fanaka.protekt.services;

import com.fanaka.protekt.dto.ProtektClaimDto;
import com.fanaka.protekt.dto.ProtektClaimCreationDto;
import org.springframework.web.multipart.MultipartFile;

public interface ProtektClaimService {
    ProtektClaimDto createClaim(ProtektClaimCreationDto claim, MultipartFile[] files);
    ProtektClaimDto updateClaim(ProtektClaimCreationDto claim, MultipartFile[] files);
    ProtektClaimDto getClaimById(Long id);
}
