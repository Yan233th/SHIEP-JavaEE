package com.sms.repository;

import com.sms.model.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz, Long> {
    Optional<Clazz> findByClassCode(String classCode);

    List<Clazz> findByDepartmentId(Long deptId);

    List<Clazz> findByGrade(String grade);

    List<Clazz> findByMajor(String major);

    List<Clazz> findByStatus(Integer status);

    @Query("SELECT c FROM Clazz c WHERE c.department.id = :deptId AND c.grade = :grade")
    List<Clazz> findByDeptAndGrade(@Param("deptId") Long deptId, @Param("grade") String grade);

    @Query("SELECT DISTINCT c.grade FROM Clazz c ORDER BY c.grade DESC")
    List<String> findAllGrades();

    @Query("SELECT DISTINCT c.major FROM Clazz c WHERE c.department.id = :deptId")
    List<String> findMajorsByDeptId(@Param("deptId") Long deptId);

    boolean existsByClassCode(String classCode);
}
