package com.undongminjok.api.parts.dto.response;

import com.undongminjok.api.parts.domain.Part;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartResponse {
    private Long id;
    private String name;

    public static PartResponse from(Part part) {
        return PartResponse.builder()
                .id(part.getId())
                .name(part.getName())
                .build();
    }
}
