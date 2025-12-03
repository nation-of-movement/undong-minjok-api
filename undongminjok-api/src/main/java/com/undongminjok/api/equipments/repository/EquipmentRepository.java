package com.undongminjok.api.equipments.repository;

import com.undongminjok.api.equipments.domain.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

}
