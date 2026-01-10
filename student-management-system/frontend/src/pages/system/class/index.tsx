import { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, message, Popconfirm, Select } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { Clazz, Department } from '@/types';
import { clazzApi } from '@/api/clazz';
import { departmentApi } from '@/api/department';

const { Option } = Select;

const ClassPage: React.FC = () => {
  const [classes, setClasses] = useState<Clazz[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingClass, setEditingClass] = useState<Clazz | null>(null);
  const [form] = Form.useForm();

  // 院系列表
  const [departments, setDepartments] = useState<Department[]>([]);
  const [selectedDeptId, setSelectedDeptId] = useState<number | undefined>(undefined);

  const fetchClasses = async () => {
    setLoading(true);
    try {
      const response = await clazzApi.getAll();
      setClasses(response.data.data || []);
    } catch {
      message.error('获取班级列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchDepartments = async () => {
    try {
      const response = await departmentApi.getAll();
      setDepartments(response.data || []);
    } catch {
      message.error('获取院系列表失败');
    }
  };

  const fetchClassesByDept = async (deptId: number) => {
    setLoading(true);
    try {
      const response = await clazzApi.getByDepartment(deptId);
      setClasses(response.data.data || []);
    } catch {
      message.error('获取班级列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClasses();
    fetchDepartments();
  }, []);

  const handleDeptFilter = (deptId: number | undefined) => {
    setSelectedDeptId(deptId);
    if (deptId) {
      fetchClassesByDept(deptId);
    } else {
      fetchClasses();
    }
  };

  const handleAdd = () => {
    setEditingClass(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Clazz) => {
    setEditingClass(record);
    form.setFieldsValue({
      className: record.className,
      classCode: record.classCode,
      grade: record.grade,
      major: record.major,
      department: record.department?.id,
    });
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await clazzApi.delete(id);
      message.success('删除成功');
      if (selectedDeptId) {
        fetchClassesByDept(selectedDeptId);
      } else {
        fetchClasses();
      }
    } catch {
      message.error('删除失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const data = {
        ...values,
        department: { id: values.department },
      };

      if (editingClass) {
        await clazzApi.update(editingClass.id, data);
        message.success('更新成功');
      } else {
        await clazzApi.create(data);
        message.success('创建成功');
      }
      setModalVisible(false);
      if (selectedDeptId) {
        fetchClassesByDept(selectedDeptId);
      } else {
        fetchClasses();
      }
    } catch {
      message.error('操作失败');
    }
  };

  const columns: ColumnsType<Clazz> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '班级名称',
      dataIndex: 'className',
      key: 'className',
    },
    {
      title: '班级编码',
      dataIndex: 'classCode',
      key: 'classCode',
    },
    {
      title: '年级',
      dataIndex: 'grade',
      key: 'grade',
      width: 100,
    },
    {
      title: '专业',
      dataIndex: 'major',
      key: 'major',
    },
    {
      title: '所属院系',
      dataIndex: ['department', 'name'],
      key: 'department',
    },
    {
      title: '学生人数',
      key: 'studentCount',
      render: (_, record) => record.students?.length || 0,
      width: 100,
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
            title="确定删除该班级吗？"
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
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Space>
          <h2>班级管理</h2>
          <Select
            placeholder="筛选院系"
            allowClear
            style={{ width: 200 }}
            value={selectedDeptId}
            onChange={handleDeptFilter}
          >
            {departments.map(dept => (
              <Option key={dept.id} value={dept.id}>{dept.name}</Option>
            ))}
          </Select>
        </Space>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增班级
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={classes}
        rowKey="id"
        loading={loading}
      />
      <Modal
        title={editingClass ? '编辑班级' : '新增班级'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="className"
            label="班级名称"
            rules={[{ required: true, message: '请输入班级名称' }]}
          >
            <Input placeholder="例如：软件工程2021级1班" />
          </Form.Item>
          <Form.Item
            name="classCode"
            label="班级编码"
            rules={[{ required: true, message: '请输入班级编码' }]}
          >
            <Input placeholder="例如：SE2021-01" />
          </Form.Item>
          <Form.Item
            name="grade"
            label="年级"
            rules={[{ required: true, message: '请输入年级' }]}
          >
            <Input placeholder="例如：2021" />
          </Form.Item>
          <Form.Item
            name="major"
            label="专业"
            rules={[{ required: true, message: '请输入专业' }]}
          >
            <Input placeholder="例如：软件工程" />
          </Form.Item>
          <Form.Item
            name="department"
            label="所属院系"
            rules={[{ required: true, message: '请选择所属院系' }]}
          >
            <Select placeholder="请选择院系">
              {departments.map(dept => (
                <Option key={dept.id} value={dept.id}>{dept.name}</Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ClassPage;
