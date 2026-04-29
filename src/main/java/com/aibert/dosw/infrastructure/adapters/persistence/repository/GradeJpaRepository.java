package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeJpaRepository extends JpaRepository<GradeEntity, Long> {

    List<GradeEntity> findByCutId(Long cutId);
}
