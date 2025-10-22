package com.fanaka.protekt.dto.mapper;

import com.fanaka.protekt.dto.ProviderDto;
import com.fanaka.protekt.entities.Provider;
import org.springframework.stereotype.Component;

@Component
public class ProviderMapper {
    public ProviderDto toProviderDto(Provider provider) {
        if (provider == null) {
            return null;
        }

        return ProviderDto.builder()
                .id(provider.getId())
                .name(provider.getName())
                .code(provider.getCode())
                .description(provider.getDescription())
                .contactEmail(provider.getContactEmail())
                .apiEndpoint(provider.getApiEndpoint())
                .isActive(provider.getIsActive())
                .createdAt(provider.getCreatedAt() != null ? provider.getCreatedAt().toLocalDateTime() : null)
                .updatedAt(provider.getUpdatedAt() != null ? provider.getUpdatedAt().toLocalDateTime() : null)
                .build();
    }
}