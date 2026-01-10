import { useState, useEffect, useCallback } from 'react';
import { Form, Input, Button, Card, message, Progress, Typography } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, SafetyOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '@/api/auth';

const { Text } = Typography;

interface RegisterForm {
  username: string;
  password: string;
  confirmPassword: string;
  nickname?: string;
  email?: string;
  captcha: string;
}

const RegisterPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [captchaKey, setCaptchaKey] = useState('');
  const [captchaImage, setCaptchaImage] = useState('');
  const [passwordStrength, setPasswordStrength] = useState<string>('');
  const [passwordErrors, setPasswordErrors] = useState<string[]>([]);
  const navigate = useNavigate();
  const [form] = Form.useForm();

  const loadCaptcha = useCallback(async () => {
    try {
      const response = await authApi.getCaptcha();
      if (response.data.data) {
        setCaptchaKey(response.data.data.key);
        setCaptchaImage(response.data.data.image);
      }
    } catch {
      message.error('获取验证码失败');
    }
  }, []);

  useEffect(() => {
    loadCaptcha();
  }, [loadCaptcha]);

  const checkPassword = async (password: string) => {
    if (!password || password.length < 3) {
      setPasswordStrength('');
      setPasswordErrors([]);
      return;
    }
    try {
      const response = await authApi.checkPasswordStrength(password);
      if (response.data.data) {
        setPasswordStrength(response.data.data.strength);
        setPasswordErrors(response.data.data.errors || []);
      }
    } catch {
      // ignore
    }
  };

  const getStrengthPercent = () => {
    switch (passwordStrength) {
      case '强': return 100;
      case '中': return 66;
      case '弱': return 33;
      default: return 0;
    }
  };

  const getStrengthColor = () => {
    switch (passwordStrength) {
      case '强': return '#52c41a';
      case '中': return '#faad14';
      case '弱': return '#ff4d4f';
      default: return '#d9d9d9';
    }
  };

  const onFinish = async (values: RegisterForm) => {
    setLoading(true);
    try {
      const response = await authApi.register({
        ...values,
        captchaKey,
      });
      if (response.data.code === 200) {
        message.success('注册成功，请登录');
        navigate('/login');
      } else {
        message.error(response.data.message || '注册失败');
        loadCaptcha();
      }
    } catch (error) {
      message.error(error instanceof Error ? error.message : '注册失败');
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
        title="用户注册"
        style={{ width: 420 }}
        headStyle={{ textAlign: 'center', fontSize: '20px' }}
      >
        <Form
          form={form}
          name="register"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: '请输入用户名' },
              { min: 3, max: 20, message: '用户名长度3-20位' }
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
            />
          </Form.Item>

          <Form.Item
            name="nickname"
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="昵称（选填）"
            />
          </Form.Item>

          <Form.Item
            name="email"
            rules={[
              { type: 'email', message: '请输入有效的邮箱地址' }
            ]}
          >
            <Input
              prefix={<MailOutlined />}
              placeholder="邮箱（选填）"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, max: 50, message: '密码长度6-50位' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
              onChange={(e) => checkPassword(e.target.value)}
            />
          </Form.Item>

          {passwordStrength && (
            <Form.Item style={{ marginBottom: 8 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <Text>密码强度:</Text>
                <Progress
                  percent={getStrengthPercent()}
                  strokeColor={getStrengthColor()}
                  showInfo={false}
                  style={{ flex: 1 }}
                />
                <Text style={{ color: getStrengthColor() }}>{passwordStrength}</Text>
              </div>
              {passwordErrors.length > 0 && (
                <div style={{ marginTop: 4 }}>
                  {passwordErrors.map((err, idx) => (
                    <Text key={idx} type="danger" style={{ display: 'block', fontSize: 12 }}>
                      {err}
                    </Text>
                  ))}
                </div>
              )}
            </Form.Item>
          )}

          <Form.Item
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: '请确认密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="确认密码"
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
              注册
            </Button>
          </Form.Item>

          <Form.Item style={{ marginBottom: 0, textAlign: 'center' }}>
            <Text>已有账号？</Text>
            <Link to="/login">立即登录</Link>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default RegisterPage;
