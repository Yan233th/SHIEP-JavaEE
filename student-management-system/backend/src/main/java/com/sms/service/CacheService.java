package com.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_PREFIX = "user:";
    private static final String MENU_CACHE_PREFIX = "menu:";
    private static final String ROLE_CACHE_PREFIX = "role:";

    // 通用缓存操作
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    // 用户缓存
    public void cacheUser(Long userId, Object user) {
        set(USER_CACHE_PREFIX + userId, user, 30, TimeUnit.MINUTES);
    }

    public Object getCachedUser(Long userId) {
        return get(USER_CACHE_PREFIX + userId);
    }

    public void evictUser(Long userId) {
        delete(USER_CACHE_PREFIX + userId);
    }

    // 菜单缓存
    public void cacheUserMenus(Long userId, Object menus) {
        set(MENU_CACHE_PREFIX + "user:" + userId, menus, 1, TimeUnit.HOURS);
    }

    public Object getCachedUserMenus(Long userId) {
        return get(MENU_CACHE_PREFIX + "user:" + userId);
    }

    public void evictUserMenus(Long userId) {
        delete(MENU_CACHE_PREFIX + "user:" + userId);
    }

    // 角色缓存
    public void cacheRole(Long roleId, Object role) {
        set(ROLE_CACHE_PREFIX + roleId, role, 1, TimeUnit.HOURS);
    }

    public Object getCachedRole(Long roleId) {
        return get(ROLE_CACHE_PREFIX + roleId);
    }

    public void evictRole(Long roleId) {
        delete(ROLE_CACHE_PREFIX + roleId);
    }

    // 清除所有用户相关缓存
    public void evictAllUserCache(Long userId) {
        evictUser(userId);
        evictUserMenus(userId);
    }
}
