package com.sms.service;

import com.sms.model.Attachment;
import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.repository.AttachmentRepository;
import com.sms.repository.CourseRepository;
import com.sms.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private FileService fileService;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setName(courseDetails.getName());
        course.setCredits(courseDetails.getCredits());
        course.setDescription(courseDetails.getDescription());
        course.setSemester(courseDetails.getSemester());
        if (courseDetails.getTeacher() != null) {
            course.setTeacher(courseDetails.getTeacher());
        }

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Transactional
    public void enrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        course.getStudents().add(student);
        courseRepository.save(course);
    }

    @Transactional
    public void unenrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        course.getStudents().remove(student);
        courseRepository.save(course);
    }

    /**
     * 上传课程附件
     */
    @Transactional
    public Attachment uploadAttachment(Long courseId, MultipartFile file) throws Exception {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 上传文件到MinIO
        String fileUrl = fileService.uploadFile(file, "course-attachments");

        // 创建附件记录
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileUrl(fileUrl);
        attachment.setFileSize(file.getSize());
        attachment.setFileType(file.getContentType());
        attachment.setCourse(course);
        attachment.setUploadTime(LocalDateTime.now());

        return attachmentRepository.save(attachment);
    }

    /**
     * 获取课程附件列表
     */
    public List<Attachment> getCourseAttachments(Long courseId) {
        return attachmentRepository.findByCourseId(courseId);
    }

    /**
     * 删除课程附件
     */
    @Transactional
    public void deleteAttachment(Long attachmentId) throws Exception {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("附件不存在"));

        // 从MinIO删除文件（可选，如果需要的话）
        // 注意：当前FileService没有提供根据URL删除文件的方法
        // 如果需要，可以添加该功能

        // 删除数据库记录
        attachmentRepository.delete(attachment);
    }
}
