package com.fanaka.protekt.controllers;

import com.fanaka.protekt.dto.ProtektClaimCreationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ProtektClaimController {
    ResponseEntity<Object> createClaim(ProtektClaimCreationDto claim, MultipartFile[] files) throws Exception;
    ResponseEntity<Object> updateClaim(ProtektClaimCreationDto claim, MultipartFile[] files) throws Exception;
    ResponseEntity<Object> getClaimById(Long id) throws Exception;
}