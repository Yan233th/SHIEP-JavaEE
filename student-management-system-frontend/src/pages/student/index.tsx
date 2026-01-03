import { useState, useEffect, useCallback } from 'react';
import { Table, Button, Space, Modal, Form, Input, message, Popconfirm, Upload, Select, DatePicker, Avatar, Row, Col, Card } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, UploadOutlined, UserOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { Student, DictData, Clazz, Department } from '@/types';
import { studentApi } from '@/api/student';
import { dictApi } from '@/api/dict';
import { clazzApi } from '@/api/clazz';
import { departmentApi } from '@/api/department';
import dayjs from 'dayjs';

const { Option } = Select;

const StudentPage: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingStudent, setEditingStudent] = useState<Student | null>(null);
  const [form] = Form.useForm();

  // 数据字典
  const [genderOptions, setGenderOptions] = useState<DictData[]>([]);
  const [nationOptions, setNationOptions] = useState<DictData[]>([]);
  const [politicalOptions, setPoliticalOptions] = useState<DictData[]>([]);
  const [statusOptions, setStatusOptions] = useState<DictData[]>([]);

  // 联动数据
  const [departments, setDepartments] = useState<Department[]>([]);
  const [classes, setClasses] = useState<Clazz[]>([]);
  const [selectedDeptId, setSelectedDeptId] = useState<number | null>(null);

  const fetchStudents = async () => {
    setLoading(true);
    try {
      const response = await studentApi.getAll();
      setStudents(response.data);
    } catch {
      message.error('获取学生列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchDictData = useCallback(async () => {
    try {
      const [genderRes, nationRes, politicalRes, statusRes] = await Promise.all([
        dictApi.getDataByTypeCode('gender'),
        dictApi.getDataByTypeCode('nation'),
        dictApi.getDataByTypeCode('political'),
        dictApi.getDataByTypeCode('student_status'),
      ]);
      setGenderOptions(genderRes.data.data || []);
      setNationOptions(nationRes.data.data || []);
      setPoliticalOptions(politicalRes.data.data || []);
      setStatusOptions(statusRes.data.data || []);
    } catch {
      // 初始化字典数据
      try {
        await dictApi.initDefaultDicts();
        fetchDictData();
      } catch {
        console.error('初始化字典失败');
      }
    }
  }, []);

  const fetchDepartments = async () => {
    try {
      const response = await departmentApi.getAll();
      setDepartments(response.data.data || []);
    } catch {
      console.error('获取部门失败');
    }
  };

  const fetchClassesByDept = async (deptId: number) => {
    try {
      const response = await clazzApi.getByDepartment(deptId);
      setClasses(response.data.data || []);
    } catch {
      setClasses([]);
    }
  };

  useEffect(() => {
    fetchStudents();
    fetchDictData();
    fetchDepartments();
  }, [fetchDictData]);

  const handleDeptChange = (deptId: number) => {
    setSelectedDeptId(deptId);
    form.setFieldValue('clazz', undefined);
    if (deptId) {
      fetchClassesByDept(deptId);
    } else {
      setClasses([]);
    }
  };

  const handleAdd = () => {
    setEditingStudent(null);
    form.resetFields();
    setSelectedDeptId(null);
    setClasses([]);
    setModalVisible(true);
  };

  const handleEdit = (record: Student) => {
    setEditingStudent(record);
    const deptId = record.department?.id;
    if (deptId) {
      setSelectedDeptId(deptId);
      fetchClassesByDept(deptId);
    }
    form.setFieldsValue({
      ...record,
      department: record.department?.id,
      clazz: record.clazz?.id,
      birthDate: record.birthDate ? dayjs(record.birthDate) : undefined,
      enrollDate: record.enrollDate ? dayjs(record.enrollDate) : undefined,
    });
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await studentApi.delete(id);
      message.success('删除成功');
      fetchStudents();
    } catch {
      message.error('删除失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const submitData = {
        ...values,
        department: values.department ? { id: values.department } : null,
        clazz: values.clazz ? { id: values.clazz } : null,
        birthDate: values.birthDate ? values.birthDate.format('YYYY-MM-DD') : null,
        enrollDate: values.enrollDate ? values.enrollDate.format('YYYY-MM-DD') : null,
      };

      if (editingStudent) {
        await studentApi.update(editingStudent.id, submitData);
        message.success('更新成功');
      } else {
        await studentApi.create(submitData);
        message.success('创建成功');
      }
      setModalVisible(false);
      fetchStudents();
    } catch {
      message.error('操作失败');
    }
  };

  const handleImport = async (file: File) => {
    try {
      await studentApi.import(file);
      message.success('导入成功');
      fetchStudents();
    } catch {
      message.error('导入失败');
    }
    return false;
  };

  const handleAvatarUpload = async (studentId: number, file: File) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      await studentApi.uploadAvatar(studentId, file);
      message.success('头像上传成功');
      fetchStudents();
    } catch {
      message.error('头像上传失败');
    }
    return false;
  };

  const getDictLabel = (options: DictData[], value?: string) => {
    const item = options.find(o => o.dictValue === value);
    return item?.dictLabel || value || '-';
  };

  const columns: ColumnsType<Student> = [
    {
      title: '头像',
      dataIndex: 'avatar',
      key: 'avatar',
      width: 80,
      render: (avatar, record) => (
        <Upload
          showUploadList={false}
          beforeUpload={(file) => handleAvatarUpload(record.id, file)}
          accept="image/*"
        >
          <Avatar
            size={40}
            src={avatar}
            icon={<UserOutlined />}
            style={{ cursor: 'pointer' }}
          />
        </Upload>
      ),
    },
    {
      title: '学号',
      dataIndex: 'studentNumber',
      key: 'studentNumber',
      width: 120,
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 100,
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
      width: 80,
      render: (val) => getDictLabel(genderOptions, val),
    },
    {
      title: '院系',
      dataIndex: ['department', 'name'],
      key: 'department',
      width: 120,
    },
    {
      title: '班级',
      dataIndex: ['clazz', 'className'],
      key: 'clazz',
      width: 120,
    },
    {
      title: '手机',
      dataIndex: 'phone',
      key: 'phone',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (val) => getDictLabel(statusOptions, val),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除该学生吗？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card>
        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
          <h2 style={{ margin: 0 }}>学生管理</h2>
          <Space>
            <Upload
              accept=".xlsx,.xls"
              showUploadList={false}
              beforeUpload={handleImport}
            >
              <Button icon={<UploadOutlined />}>导入Excel</Button>
            </Upload>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增学生
            </Button>
          </Space>
        </div>
        <Table
          columns={columns}
          dataSource={students}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1000 }}
        />
      </Card>

      <Modal
        title={editingStudent ? '编辑学生' : '新增学生'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={800}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="studentNumber"
                label="学号"
                rules={[{ required: true, message: '请输入学号' }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="name"
                label="姓名"
                rules={[{ required: true, message: '请输入姓名' }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item name="gender" label="性别">
                <Select placeholder="请选择性别" allowClear>
                  {genderOptions.map(opt => (
                    <Option key={opt.dictValue} value={opt.dictValue}>{opt.dictLabel}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="nation" label="民族">
                <Select placeholder="请选择民族" allowClear showSearch optionFilterProp="children">
                  {nationOptions.map(opt => (
                    <Option key={opt.dictValue} value={opt.dictValue}>{opt.dictLabel}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="political" label="政治面貌">
                <Select placeholder="请选择政治面貌" allowClear>
                  {politicalOptions.map(opt => (
                    <Option key={opt.dictValue} value={opt.dictValue}>{opt.dictLabel}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="department" label="院系">
                <Select
                  placeholder="请选择院系"
                  allowClear
                  onChange={handleDeptChange}
                >
                  {departments.map(dept => (
                    <Option key={dept.id} value={dept.id}>{dept.name}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="clazz" label="班级">
                <Select
                  placeholder={selectedDeptId ? "请选择班级" : "请先选择院系"}
                  allowClear
                  disabled={!selectedDeptId}
                >
                  {classes.map(cls => (
                    <Option key={cls.id} value={cls.id}>{cls.className}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="idCard" label="身份证号">
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="phone" label="手机号">
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="email" label="邮箱">
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="birthDate" label="出生日期">
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="enrollDate" label="入学日期">
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="status" label="状态">
                <Select placeholder="请选择状态" allowClear>
                  {statusOptions.map(opt => (
                    <Option key={opt.dictValue} value={opt.dictValue}>{opt.dictLabel}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="address" label="地址">
            <Input.TextArea rows={2} />
          </Form.Item>

          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default StudentPage;
