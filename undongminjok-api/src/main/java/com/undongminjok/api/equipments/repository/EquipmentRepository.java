package com.undongminjok.api.equipments.repository;

import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.parts.domain.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByPart(Part part);
}
