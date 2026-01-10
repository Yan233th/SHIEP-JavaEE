import { useState, useEffect } from 'react';
import { Card, Table, Select, message, Tag, Empty } from 'antd';
import { CalendarOutlined } from '@ant-design/icons';
import { getStudentSchedule } from '@/api/schedule';
import { useAuthStore } from '@/store';

interface Schedule {
  id: number;
  course: {
    id: number;
    name: string;
    credits: number;
  };
  teacher: {
    id: number;
    username: string;
    nickname: string;
  };
  weekDay: number;
  section: number;
  startTime: string;
  endTime: string;
  classroom: string;
  semester: string;
}

const weekDayMap: { [key: number]: string } = {
  1: '周一',
  2: '周二',
  3: '周三',
  4: '周四',
  5: '周五',
  6: '周六',
  7: '周日',
};

const MySchedulePage = () => {
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [loading, setLoading] = useState(false);
  const [semester, setSemester] = useState('2024-2025-1');
  const { user } = useAuthStore();

  useEffect(() => {
    fetchSchedule();
  }, [semester]);

  const fetchSchedule = async () => {
    if (!user?.studentId) return;

    setLoading(true);
    try {
      const res = await getStudentSchedule(user.studentId, semester);
      if (res.data.success) {
        setSchedules(res.data.data || []);
      }
    } catch (error) {
      message.error('获取课表失败');
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: '星期',
      dataIndex: 'weekDay',
      key: 'weekDay',
      width: 80,
      render: (weekDay: number) => (
        <Tag color="blue">{weekDayMap[weekDay] || '-'}</Tag>
      ),
    },
    {
      title: '节次',
      dataIndex: 'section',
      key: 'section',
      width: 80,
      render: (section: number) => `第${section}节`,
    },
    {
      title: '时间',
      key: 'time',
      width: 150,
      render: (_: any, record: Schedule) => (
        <span>
          {record.startTime || '-'} ~ {record.endTime || '-'}
        </span>
      ),
    },
    {
      title: '课程名称',
      dataIndex: ['course', 'name'],
      key: 'courseName',
      render: (text: string) => <strong>{text}</strong>,
    },
    {
      title: '授课教师',
      dataIndex: ['teacher'],
      key: 'teacher',
      render: (teacher: any) => teacher?.nickname || teacher?.username || '-',
    },
    {
      title: '教室',
      dataIndex: 'classroom',
      key: 'classroom',
      width: 120,
    },
  ];

  return (
    <Card
      title={
        <span>
          <CalendarOutlined /> 我的课表
        </span>
      }
      bordered={false}
      extra={
        <Select
          value={semester}
          onChange={setSemester}
          style={{ width: 150 }}
          options={[
            { label: '2024-2025-1', value: '2024-2025-1' },
            { label: '2024-2025-2', value: '2024-2025-2' },
            { label: '2025-2026-1', value: '2025-2026-1' },
          ]}
        />
      }
    >
      {schedules.length === 0 && !loading ? (
        <Empty description="暂无课表数据" />
      ) : (
        <Table
          columns={columns}
          dataSource={schedules}
          rowKey="id"
          loading={loading}
          pagination={false}
        />
      )}
    </Card>
  );
};

export default MySchedulePage;
