package com.sms.service;

import com.sms.document.AttachmentDocument;
import com.sms.document.StudentDocument;
import com.sms.model.Attachment;
import com.sms.model.Student;
import com.sms.model.User;
import com.sms.repository.AttachmentSearchRepository;
import com.sms.repository.StudentSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private StudentSearchRepository studentSearchRepository;

    @Autowired
    private AttachmentSearchRepository attachmentSearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private FileContentExtractor fileContentExtractor;

    /**
     * 索引学生数据
     */
    public void indexStudent(Student student) {
        StudentDocument doc = convertToDocument(student);
        studentSearchRepository.save(doc);
    }

    /**
     * 批量索引学生数据
     */
    public void indexStudents(List<Student> students) {
        List<StudentDocument> docs = students.stream()
                .map(this::convertToDocument)
                .collect(Collectors.toList());
        studentSearchRepository.saveAll(docs);
    }

    /**
     * 删除学生索引
     */
    public void deleteStudentIndex(Long id) {
        studentSearchRepository.deleteById(id);
    }

    /**
     * 全文搜索学生
     */
    public List<StudentDocument> searchStudents(String keyword) {
        Criteria criteria = new Criteria("studentNumber").contains(keyword)
                .or("className").contains(keyword)
                .or("username").contains(keyword)
                .or("nickname").contains(keyword)
                .or("email").contains(keyword);

        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<StudentDocument> searchHits = elasticsearchOperations.search(query, StudentDocument.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 按昵称搜索
     */
    public List<StudentDocument> searchByNickname(String nickname) {
        return studentSearchRepository.findByNicknameContaining(nickname);
    }

    /**
     * 按学号搜索
     */
    public List<StudentDocument> searchByStudentNumber(String studentNumber) {
        return studentSearchRepository.findByStudentNumberContaining(studentNumber);
    }

    /**
     * 按班级搜索
     */
    public List<StudentDocument> searchByClassName(String className) {
        return studentSearchRepository.findByClassNameContaining(className);
    }

    private StudentDocument convertToDocument(Student student) {
        StudentDocument doc = new StudentDocument();
        doc.setId(student.getId());
        doc.setStudentNumber(student.getStudentNumber());
        // 使用班级名称
        if (student.getClazz() != null) {
            doc.setClassName(student.getClazz().getClassName());
        }

        // 使用学生姓名或用户昵称
        doc.setNickname(student.getName());

        User user = student.getUser();
        if (user != null) {
            doc.setUsername(user.getUsername());
            if (doc.getNickname() == null) {
                doc.setNickname(user.getNickname());
            }
            doc.setEmail(user.getEmail());
        }

        // 使用学生邮箱
        if (student.getEmail() != null) {
            doc.setEmail(student.getEmail());
        }

        return doc;
    }

    // ==================== 附件搜索功能 ====================

    /**
     * 索引附件数据（包括提取文件内容）
     */
    public void indexAttachment(Attachment attachment, byte[] fileData) {
        AttachmentDocument doc = new AttachmentDocument();
        doc.setId(attachment.getId());
        doc.setFileName(attachment.getFileName());
        doc.setFileType(attachment.getFileType());

        // Convert LocalDateTime to LocalDate
        if (attachment.getUploadTime() != null) {
            doc.setUploadTime(attachment.getUploadTime().toLocalDate());
        }

        if (attachment.getCourse() != null) {
            doc.setCourseId(attachment.getCourse().getId());
            doc.setCourseName(attachment.getCourse().getName());
        }

        // 提取文件内容
        String content = fileContentExtractor.extractContent(fileData, attachment.getFileName());
        doc.setContent(content);

        attachmentSearchRepository.save(doc);
    }

    /**
     * 删除附件索引
     */
    public void deleteAttachmentIndex(Long id) {
        attachmentSearchRepository.deleteById(id);
    }

    /**
     * 全文搜索附件
     */
    public List<AttachmentDocument> searchAttachments(String keyword) {
        Criteria criteria = new Criteria("fileName").contains(keyword)
                .or("content").contains(keyword)
                .or("courseName").contains(keyword);

        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<AttachmentDocument> searchHits = elasticsearchOperations.search(query, AttachmentDocument.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
