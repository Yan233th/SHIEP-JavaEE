package com.sms.repository;

import com.sms.document.StudentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSearchRepository extends ElasticsearchRepository<StudentDocument, Long> {

    List<StudentDocument> findByNicknameContaining(String nickname);

    List<StudentDocument> findByStudentNumberContaining(String studentNumber);

    List<StudentDocument> findByClassNameContaining(String className);
}
