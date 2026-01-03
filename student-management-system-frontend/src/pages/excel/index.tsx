import React, { useState } from 'react';
import { Card, Button, Upload, message, Space, Table, Divider } from 'antd';
import { UploadOutlined, DownloadOutlined, FileExcelOutlined } from '@ant-design/icons';
import type { UploadProps } from 'antd';
import { exportStudents, importStudents, downloadTemplate } from '@/api/excel';

const ExcelImportExport: React.FC = () => {
  const [importing, setImporting] = useState(false);
  const [exporting, setExporting] = useState(false);
  const [importedData, setImportedData] = useState<any[]>([]);

  // 导出学生数据
  const handleExport = async () => {
    setExporting(true);
    try {
      const res = await exportStudents();
      const blob = new Blob([res.data], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = '学生信息.xlsx';
      link.click();
      window.URL.revokeObjectURL(url);
      message.success('导出成功');
    } catch (error) {
      message.error('导出失败');
    } finally {
      setExporting(false);
    }
  };

  // 下载模板
  const handleDownloadTemplate = async () => {
    try {
      const res = await downloadTemplate();
      const blob = new Blob([res.data], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = '学生信息导入模板.xlsx';
      link.click();
      window.URL.revokeObjectURL(url);
      message.success('模板下载成功');
    } catch (error) {
      message.error('模板下载失败');
    }
  };

  // 上传配置
  const uploadProps: UploadProps = {
    name: 'file',
    accept: '.xlsx,.xls',
    showUploadList: false,
    beforeUpload: async (file) => {
      setImporting(true);
      try {
        const res = await importStudents(file);
        if (res.data.success) {
          setImportedData(res.data.data || []);
          message.success(`成功导入 ${res.data.data?.length || 0} 条数据`);
        } else {
          message.error(res.data.message || '导入失败');
        }
      } catch (error) {
        message.error('导入失败');
      } finally {
        setImporting(false);
      }
      return false; // 阻止默认上传
    },
  };

  const columns = [
    { title: '学号', dataIndex: 'studentNumber', key: 'studentNumber' },
    { title: '班级', dataIndex: 'className', key: 'className' },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Card title="Excel 导入导出" bordered={false}>
        <Space size="large">
          <Button
            type="primary"
            icon={<DownloadOutlined />}
            onClick={handleExport}
            loading={exporting}
          >
            导出学生数据
          </Button>

          <Upload {...uploadProps}>
            <Button icon={<UploadOutlined />} loading={importing}>
              导入学生数据
            </Button>
          </Upload>

          <Button
            icon={<FileExcelOutlined />}
            onClick={handleDownloadTemplate}
          >
            下载导入模板
          </Button>
        </Space>

        {importedData.length > 0 && (
          <>
            <Divider />
            <h4>导入结果预览</h4>
            <Table
              dataSource={importedData}
              columns={columns}
              rowKey="id"
              size="small"
              pagination={{ pageSize: 10 }}
            />
          </>
        )}
      </Card>
    </div>
  );
};

export default ExcelImportExport;
