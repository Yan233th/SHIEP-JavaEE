package com.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sms.model.Student;
import com.sms.model.Clazz;
import com.sms.repository.StudentRepository;
import com.sms.repository.ClazzRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClazzRepository clazzRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private FileService fileService;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public List<Student> getStudentsByClassId(Long classId) {
        return studentRepository.findByClazzId(classId);
    }

    public List<Student> getStudentsByDeptId(Long deptId) {
        return studentRepository.findByDepartmentId(deptId);
    }

    public Optional<Student> getStudentByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber);
    }

    @Transactional
    public Student createStudent(Student student) {
        student.setCreateTime(LocalDateTime.now());
        Student saved = studentRepository.save(student);

        // 更新班级学生数
        if (saved.getClazz() != null) {
            updateClassStudentCount(saved.getClazz().getId(), 1);
        }

        // 同步到Elasticsearch
        try {
            searchService.indexStudent(saved);
        } catch (Exception e) {
            System.err.println("ES索引失败: " + e.getMessage());
        }
        return saved;
    }

    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Long oldClassId = student.getClazz() != null ? student.getClazz().getId() : null;
        Long newClassId = studentDetails.getClazz() != null ? studentDetails.getClazz().getId() : null;

        student.setStudentNumber(studentDetails.getStudentNumber());
        student.setName(studentDetails.getName());
        student.setGender(studentDetails.getGender());
        student.setNation(studentDetails.getNation());
        student.setPolitical(studentDetails.getPolitical());
        student.setIdCard(studentDetails.getIdCard());
        student.setPhone(studentDetails.getPhone());
        student.setEmail(studentDetails.getEmail());
        student.setBirthDate(studentDetails.getBirthDate());
        student.setAddress(studentDetails.getAddress());
        student.setEnrollDate(studentDetails.getEnrollDate());
        student.setStatus(studentDetails.getStatus());
        student.setClazz(studentDetails.getClazz());
        student.setDepartment(studentDetails.getDepartment());
        student.setRemark(studentDetails.getRemark());
        student.setUpdateTime(LocalDateTime.now());

        if (studentDetails.getAvatar() != null) {
            student.setAvatar(studentDetails.getAvatar());
        }
        if (studentDetails.getUser() != null) {
            student.setUser(studentDetails.getUser());
        }

        Student saved = studentRepository.save(student);

        // 更新班级学生数
        if (oldClassId != null && !oldClassId.equals(newClassId)) {
            updateClassStudentCount(oldClassId, -1);
        }
        if (newClassId != null && !newClassId.equals(oldClassId)) {
            updateClassStudentCount(newClassId, 1);
        }

        // 同步到Elasticsearch
        try {
            searchService.indexStudent(saved);
        } catch (Exception e) {
            System.err.println("ES索引更新失败: " + e.getMessage());
        }
        return saved;
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            // 更新班级学生数
            if (student.getClazz() != null) {
                updateClassStudentCount(student.getClazz().getId(), -1);
            }
            studentRepository.deleteById(id);
            // 从Elasticsearch删除
            try {
                searchService.deleteStudentIndex(id);
            } catch (Exception e) {
                System.err.println("ES索引删除失败: " + e.getMessage());
            }
        }
    }

    /**
     * 上传学生头像
     */
    public String uploadAvatar(Long studentId, MultipartFile file) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        try {
            String avatarUrl = fileService.uploadFile(file, "student-avatars");
            student.setAvatar(avatarUrl);
            student.setUpdateTime(LocalDateTime.now());
            studentRepository.save(student);
            return avatarUrl;
        } catch (Exception e) {
            throw new RuntimeException("头像上传失败: " + e.getMessage(), e);
        }
    }

    private void updateClassStudentCount(Long classId, int delta) {
        clazzRepository.findById(classId).ifPresent(clazz -> {
            int count = clazz.getStudentCount() == null ? 0 : clazz.getStudentCount();
            clazz.setStudentCount(Math.max(0, count + delta));
            clazzRepository.save(clazz);
        });
    }

    /**
     * 重建所有学生的ES索引
     */
    public void rebuildSearchIndex() {
        List<Student> allStudents = studentRepository.findAll();
        try {
            searchService.indexStudents(allStudents);
        } catch (Exception e) {
            System.err.println("ES索引重建失败: " + e.getMessage());
        }
    }
}
