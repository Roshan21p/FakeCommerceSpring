package com.example.FakeCommerce.services.cache;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.GetProductResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRedisCache {

    private static final String KEY_SUMMARY = "product:summary:";

    private static final Duration CACHE_TTL = Duration.ofMinutes(1);

    private  final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    public Optional<GetProductResponseDto> getSummary(Long id) {

          String responseJson = stringRedisTemplate.opsForValue().get(KEY_SUMMARY + id);
        
          // Cache miss
          if(responseJson == null) {
            log.info("Cache miss for product id: {}", id);
            return Optional.empty();
          }

          // Deserialize the JSON string back to GetProductResponseDto
          // Cache hit
          log.info("Cache hit for product id: {}", id);
          try {
              GetProductResponseDto responseDto = objectMapper.readValue(responseJson, GetProductResponseDto.class);
              return Optional.of(responseDto);
          } catch (Exception e) {
              log.error("Error deserializing product summary from Redis for id {}: {}", id, e.getMessage());
              stringRedisTemplate.delete(KEY_SUMMARY + id); // Remove the corrupted cache entry
              return Optional.empty();
          }

    }

    public void putSummary(Long id, GetProductResponseDto response) {
          try {
               stringRedisTemplate.opsForValue().set(KEY_SUMMARY + id, objectMapper.writeValueAsString(response),CACHE_TTL);
          }
          catch (Exception e) {
              log.error("Error serializing product summary to Redis for id {}: {}", id, e.getMessage());
          }
    }
}
