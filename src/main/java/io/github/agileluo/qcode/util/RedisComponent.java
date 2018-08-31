package io.github.agileluo.qcode.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author luoml
 *
 */
@Component
public class RedisComponent {
    @Autowired
    Environment env;
  @Autowired
  private StringRedisTemplate stringRedisTemplate;

	public void set(String key, String value) {
		stringRedisTemplate.opsForValue().set(key, value);
	}

	public Long increment(String key, long value) {
		return stringRedisTemplate.opsForHash().increment("primarykey", key, value);
	}

	public void setEx(String key, String value, int seconds) {
		stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
	}

	public String get(String key) {
		return this.stringRedisTemplate.opsForValue().get(key);
	}

	public void del(String key) {
		this.stringRedisTemplate.delete(key);
	}

	public boolean exists(String key) {
		return stringRedisTemplate.hasKey(key);
	}

	public void expire(String key, int seconds) {
		stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
	}

	public void set(String key, Object value) {
		set(key, JSON.toJSONString(value));
	}

	public void setEx(String key, Object value, int seconds) {
		setEx(key, JSON.toJSONString(value), seconds);
	}

	public <T> T get(String key, Class<T> clazz) {
		String jsonObj = get(key);
		if (jsonObj != null) {
			return JSON.parseObject(jsonObj, clazz);
		}
		return null;
	}

	public <T> List<T> getList(String key, Class<T> clazz) {
		String jsonObj = get(key);
		if (jsonObj != null) {
			return JSON.parseArray(jsonObj, clazz);
		}
		return null;
	}
	public long incrementNormal(String key, long delte){
		return stringRedisTemplate.opsForValue().increment(key, delte);
	}

}
