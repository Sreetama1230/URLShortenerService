package com.api.url.shortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	public <T> T get(String key, Class<T> customClass) {
		Object o = redisTemplate.opsForValue().get(key);
		if (o == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(o.toString(), customClass);
	}
	
	public void set(String key, Object o) {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonValue = objectMapper.writeValueAsString(o);
		redisTemplate.opsForValue().set(key, jsonValue);
	}
	
	public Long increment(String key) {
		return redisTemplate.opsForValue().increment(key);
	}
}
