import React, { useState } from 'react';
import { Card, Upload, Button, message, Image, Input, Space } from 'antd';
import { UploadOutlined, InboxOutlined } from '@ant-design/icons';
import type { UploadProps } from 'antd';
import { uploadFile } from '@/api/file';

const { Dragger } = Upload;

const FileUpload: React.FC = () => {
  const [uploading, setUploading] = useState(false);
  const [uploadedFiles, setUploadedFiles] = useState<{ url: string; filename: string }[]>([]);
  const [folder, setFolder] = useState('uploads');

  const uploadProps: UploadProps = {
    name: 'file',
    multiple: true,
    showUploadList: false,
    beforeUpload: async (file) => {
      setUploading(true);
      try {
        const res = await uploadFile(file, folder);
        if (res.data.success) {
          const fileInfo = res.data.data;
          setUploadedFiles((prev) => [...prev, fileInfo]);
          message.success(`${file.name} 上传成功`);
        } else {
          message.error(res.data.message || '上传失败');
        }
      } catch (error) {
        message.error(`${file.name} 上传失败`);
      } finally {
        setUploading(false);
      }
      return false;
    },
  };

  const isImage = (url: string) => {
    return /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(url);
  };

  return (
    <div style={{ padding: 24 }}>
      <Card title="文件上传" bordered={false}>
        <Space style={{ marginBottom: 16 }}>
          <span>上传目录：</span>
          <Input
            value={folder}
            onChange={(e) => setFolder(e.target.value)}
            placeholder="输入文件夹名称"
            style={{ width: 200 }}
          />
        </Space>

        <Dragger {...uploadProps} style={{ marginBottom: 24 }}>
          <p className="ant-upload-drag-icon">
            <InboxOutlined />
          </p>
          <p className="ant-upload-text">点击或拖拽文件到此区域上传</p>
          <p className="ant-upload-hint">支持单个或批量上传</p>
        </Dragger>

        {uploadedFiles.length > 0 && (
          <Card title="已上传文件" size="small" style={{ marginTop: 16 }}>
            {uploadedFiles.map((file, index) => (
              <div key={index} style={{ marginBottom: 12, padding: 8, background: '#fafafa', borderRadius: 4 }}>
                <div style={{ marginBottom: 8 }}>
                  <strong>{file.filename}</strong>
                </div>
                {isImage(file.url) ? (
                  <Image src={file.url} width={200} />
                ) : (
                  <a href={file.url} target="_blank" rel="noopener noreferrer">
                    {file.url}
                  </a>
                )}
              </div>
            ))}
          </Card>
        )}
      </Card>
    </div>
  );
};

export default FileUpload;
