package com.sms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sms.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 按教师查询课表
    List<Schedule> findByTeacherId(Long teacherId);

    // 按课程查询课表
    List<Schedule> findByCourseId(Long courseId);

    // 按学期查询课表
    List<Schedule> findBySemester(String semester);

    // 按星期几查询课表
    List<Schedule> findByWeekDay(Integer weekDay);

    // 按课程和学期查询课表
    List<Schedule> findByCourseIdAndSemester(Long courseId, String semester);

    // 查询学生的课表（通过选课记录）
    @Query("SELECT s FROM Schedule s WHERE s.course.id IN " +
           "(SELECT e.course.id FROM CourseEnrollment e WHERE e.student.id = :studentId " +
           "AND e.status = 'ENROLLED') AND s.semester = :semester")
    List<Schedule> findByStudentIdAndSemester(@Param("studentId") Long studentId,
                                               @Param("semester") String semester);
}
