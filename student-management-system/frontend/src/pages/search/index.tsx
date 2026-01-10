import React, { useState } from 'react';
import { Input, List, Card, Empty, Spin, Tag, Radio, Space } from 'antd';
import { SearchOutlined, FileOutlined } from '@ant-design/icons';
import { searchStudents, searchAttachments } from '@/api/search';

const { Search } = Input;

interface StudentDocument {
  id: number;
  studentNumber: string;
  className: string;
  username: string;
  nickname: string;
  email: string;
}

interface AttachmentDocument {
  id: number;
  fileName: string;
  fileType: string;
  content: string;
  courseName: string;
  courseId: number;
  uploadTime: string;
}

type SearchType = 'student' | 'attachment';

const GlobalSearch: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [searchType, setSearchType] = useState<SearchType>('student');
  const [studentResults, setStudentResults] = useState<StudentDocument[]>([]);
  const [attachmentResults, setAttachmentResults] = useState<AttachmentDocument[]>([]);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (value: string) => {
    if (!value.trim()) {
      setStudentResults([]);
      setAttachmentResults([]);
      setSearched(false);
      return;
    }

    setLoading(true);
    setSearched(true);
    try {
      if (searchType === 'student') {
        const res = await searchStudents(value);
        if (res.data.success) {
          setStudentResults(res.data.data || []);
        }
      } else {
        const res = await searchAttachments(value);
        if (res.data.success) {
          setAttachmentResults(res.data.data || []);
        }
      }
    } catch (error) {
      console.error('搜索失败:', error);
      setStudentResults([]);
      setAttachmentResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleTypeChange = (e: any) => {
    setSearchType(e.target.value);
    setStudentResults([]);
    setAttachmentResults([]);
    setSearched(false);
  };

  return (
    <div style={{ padding: 24 }}>
      <Card title="全文搜索" bordered={false}>
        <Space direction="vertical" style={{ width: '100%' }} size="large">
          <Radio.Group value={searchType} onChange={handleTypeChange}>
            <Radio.Button value="student">学生搜索</Radio.Button>
            <Radio.Button value="attachment">课程附件搜索</Radio.Button>
          </Radio.Group>

          <Search
            placeholder={
              searchType === 'student'
                ? '输入学号、班级、用户名、昵称或邮箱搜索...'
                : '输入文件名或文件内容关键词搜索...'
            }
            allowClear
            enterButton={<><SearchOutlined /> 搜索</>}
            size="large"
            onSearch={handleSearch}
          />

          <Spin spinning={loading}>
            {searchType === 'student' ? (
              searched && studentResults.length === 0 ? (
                <Empty description="未找到匹配的学生" />
              ) : (
                <List
                  dataSource={studentResults}
                  renderItem={(item) => (
                    <List.Item>
                      <List.Item.Meta
                        title={
                          <span>
                            {item.nickname || item.username}
                            <Tag color="blue" style={{ marginLeft: 8 }}>{item.studentNumber}</Tag>
                          </span>
                        }
                        description={
                          <span>
                            班级: {item.className || '-'} |
                            用户名: {item.username || '-'} |
                            邮箱: {item.email || '-'}
                          </span>
                        }
                      />
                    </List.Item>
                  )}
                />
              )
            ) : (
              searched && attachmentResults.length === 0 ? (
                <Empty description="未找到匹配的附件" />
              ) : (
                <List
                  dataSource={attachmentResults}
                  renderItem={(item) => (
                    <List.Item>
                      <List.Item.Meta
                        avatar={<FileOutlined style={{ fontSize: 24, color: '#1890ff' }} />}
                        title={
                          <span>
                            {item.fileName}
                            <Tag color="green" style={{ marginLeft: 8 }}>{item.courseName}</Tag>
                          </span>
                        }
                        description={
                          <div>
                            <div>文件类型: {item.fileType || '-'}</div>
                            <div>上传时间: {item.uploadTime ? new Date(item.uploadTime).toLocaleString() : '-'}</div>
                            {item.content && (
                              <div style={{ marginTop: 8, color: '#666' }}>
                                内容预览: {item.content.substring(0, 200)}...
                              </div>
                            )}
                          </div>
                        }
                      />
                    </List.Item>
                  )}
                />
              )
            )}
          </Spin>
        </Space>
      </Card>
    </div>
  );
};

export default GlobalSearch;
