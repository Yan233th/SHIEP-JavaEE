package com.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final int CAPTCHA_LENGTH = 4;
    private static final int CAPTCHA_EXPIRE_MINUTES = 5;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    /**
     * 生成验证码
     */
    public Map<String, String> generateCaptcha() {
        // 生成随机验证码
        String code = generateRandomCode();
        String key = UUID.randomUUID().toString().replace("-", "");

        // 存储到Redis
        redisTemplate.opsForValue().set(
                CAPTCHA_PREFIX + key,
                code.toLowerCase(),
                CAPTCHA_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        // 生成图片
        String base64Image = generateCaptchaImage(code);

        Map<String, String> result = new HashMap<>();
        result.put("key", key);
        result.put("image", "data:image/png;base64," + base64Image);

        return result;
    }

    /**
     * 验证验证码
     */
    public boolean verifyCaptcha(String key, String code) {
        if (key == null || code == null) {
            return false;
        }

        String storedCode = (String) redisTemplate.opsForValue().get(CAPTCHA_PREFIX + key);
        if (storedCode == null) {
            return false;
        }

        // 验证后删除
        redisTemplate.delete(CAPTCHA_PREFIX + key);

        return storedCode.equalsIgnoreCase(code.trim());
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        Random random = new Random();
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
            int x = 15 + i * 25;
            int y = 28 + random.nextInt(5);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
        }

        // 绘制噪点
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillRect(x, y, 1, 1);
        }

        g.dispose();

        // 转为Base64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
}
