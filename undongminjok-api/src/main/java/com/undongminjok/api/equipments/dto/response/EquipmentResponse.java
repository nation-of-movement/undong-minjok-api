package com.undongminjok.api.equipments.dto.response;

import com.undongminjok.api.equipments.domain.Equipment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentResponse {

    private Long id;
    private String name;
    private Long partId;
    private String partName;

    public static EquipmentResponse from(Equipment e) {
        return EquipmentResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .partId(e.getPart().getId())
                .partName(e.getPart().getName())
                .build();
    }
}
