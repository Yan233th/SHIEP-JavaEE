import { useState, useEffect, useCallback } from 'react';
import { Form, Input, Button, Card, message } from 'antd';
import { UserOutlined, LockOutlined, SafetyOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { useAuthStore } from '@/store';
import { authApi } from '@/api/auth';

interface LoginForm {
  username: string;
  password: string;
  captcha: string;
}

const LoginPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [captchaKey, setCaptchaKey] = useState('');
  const [captchaImage, setCaptchaImage] = useState('');
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  const loadCaptcha = useCallback(async () => {
    try {
      const response = await authApi.getCaptcha();
      if (response.data.data) {
        setCaptchaKey(response.data.data.key);
        setCaptchaImage(response.data.data.image);
      }
    } catch {
      // 验证码加载失败时静默处理
    }
  }, []);

  useEffect(() => {
    loadCaptcha();
  }, [loadCaptcha]);

  const onFinish = async (values: LoginForm) => {
    setLoading(true);
    try {
      await login(values.username, values.password, values.captcha, captchaKey);
      message.success('登录成功');
      navigate('/');
    } catch (error) {
      message.error(error instanceof Error ? error.message : '登录失败');
      loadCaptcha();
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      height: '100vh',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    }}>
      <Card
        title="学生信息管理系统"
        style={{ width: 400 }}
        headStyle={{ textAlign: 'center', fontSize: '20px' }}
      >
        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
            />
          </Form.Item>

          <Form.Item
            name="captcha"
            rules={[{ required: true, message: '请输入验证码' }]}
          >
            <div style={{ display: 'flex', gap: 8 }}>
              <Input
                prefix={<SafetyOutlined />}
                placeholder="验证码"
                style={{ flex: 1 }}
              />
              <img
                src={captchaImage}
                alt="验证码"
                style={{ height: 40, cursor: 'pointer', borderRadius: 4 }}
                onClick={loadCaptcha}
                title="点击刷新验证码"
              />
            </div>
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
            >
              登录
            </Button>
          </Form.Item>

          <Form.Item style={{ marginBottom: 0, textAlign: 'center' }}>
            <Link to="/register">没有账号？立即注册</Link>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default LoginPage;
