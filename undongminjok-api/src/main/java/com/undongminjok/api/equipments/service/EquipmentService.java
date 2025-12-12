package com.undongminjok.api.equipments.service;

import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.equipments.dto.response.EquipmentResponse;
import com.undongminjok.api.equipments.repository.EquipmentRepository;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.parts.PartErrorCode;
import com.undongminjok.api.parts.domain.Part;
import com.undongminjok.api.parts.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final PartRepository partRepository;

    /*부위 id로 운동기구 목록 조회*/
    public List<EquipmentResponse> getEquipmentsByPart(Long partId) {

        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new BusinessException(PartErrorCode.PART_NOT_FOUND));

        return equipmentRepository.findByPart(part)
                .stream()
                .map(EquipmentResponse::from)
                .toList();
    }

  public List<EquipmentResponse> getAllEquipments() {
    return equipmentRepository.findAll()
        .stream()
        .map(EquipmentResponse::from)
        .toList();
  }
}
