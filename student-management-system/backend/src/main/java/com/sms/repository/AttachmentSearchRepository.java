package com.sms.repository;

import com.sms.document.AttachmentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentSearchRepository extends ElasticsearchRepository<AttachmentDocument, Long> {

    List<AttachmentDocument> findByFileNameContaining(String fileName);

    List<AttachmentDocument> findByContentContaining(String content);

    List<AttachmentDocument> findByCourseNameContaining(String courseName);

    List<AttachmentDocument> findByCourseId(Long courseId);
}
