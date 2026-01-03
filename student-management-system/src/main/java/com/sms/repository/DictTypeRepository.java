package com.sms.repository;

import com.sms.model.DictType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictTypeRepository extends JpaRepository<DictType, Long> {
    Optional<DictType> findByTypeCode(String typeCode);
    List<DictType> findByStatus(Integer status);
    boolean existsByTypeCode(String typeCode);
}
