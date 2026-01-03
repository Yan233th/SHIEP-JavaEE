import React, { useState } from 'react';
import { Input, List, Card, Empty, Spin, Tag } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { searchStudents } from '@/api/search';

const { Search } = Input;

interface StudentDocument {
  id: number;
  studentNumber: string;
  className: string;
  username: string;
  nickname: string;
  email: string;
}

const GlobalSearch: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<StudentDocument[]>([]);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (value: string) => {
    if (!value.trim()) {
      setResults([]);
      setSearched(false);
      return;
    }

    setLoading(true);
    setSearched(true);
    try {
      const res = await searchStudents(value);
      if (res.data.success) {
        setResults(res.data.data || []);
      }
    } catch (error) {
      console.error('搜索失败:', error);
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <Card title="全文搜索" bordered={false}>
        <Search
          placeholder="输入学号、班级、用户名、昵称或邮箱搜索..."
          allowClear
          enterButton={<><SearchOutlined /> 搜索</>}
          size="large"
          onSearch={handleSearch}
          style={{ marginBottom: 24 }}
        />

        <Spin spinning={loading}>
          {searched && results.length === 0 ? (
            <Empty description="未找到匹配的学生" />
          ) : (
            <List
              dataSource={results}
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
          )}
        </Spin>
      </Card>
    </div>
  );
};

export default GlobalSearch;
