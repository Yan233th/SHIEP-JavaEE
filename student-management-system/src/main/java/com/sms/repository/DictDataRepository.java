package com.sms.repository;

import com.sms.model.DictData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictDataRepository extends JpaRepository<DictData, Long> {
    List<DictData> findByTypeCodeAndStatusOrderBySortOrderAsc(String typeCode, Integer status);
    List<DictData> findByTypeCodeOrderBySortOrderAsc(String typeCode);
    Optional<DictData> findByTypeCodeAndDictValue(String typeCode, String dictValue);
    void deleteByTypeCode(String typeCode);
}
