package com.example.FakeCommerce.services.cache;

import java.time.Duration;
import java.util.List;
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

    private static final String KEY_ALL_PRODUCTS = "product:all";

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

    public Optional<List<GetProductResponseDto>> getAllProducts() {
        
        String responseJson = stringRedisTemplate.opsForValue().get(KEY_ALL_PRODUCTS);

        // Cache miss
        if(responseJson == null) {
            log.info("Cache miss for all products");
            return Optional.empty();
        }

        // Deserialize the JSON string back to List<GetProductResponseDto>
        // Cache hit
        log.info("Cache hit for all products");
        try {
            List<GetProductResponseDto> products = objectMapper.readValue(responseJson, objectMapper.getTypeFactory().constructCollectionType(List.class, GetProductResponseDto.class));
            return Optional.of(products);
        } catch (Exception e) {
            log.error("Error deserializing all products from Redis: {}", e.getMessage());
            stringRedisTemplate.delete(KEY_ALL_PRODUCTS); // Remove the corrupted cache entry
            return Optional.empty();
        }
    }

    public void putAllProducts(List<GetProductResponseDto> products) {

        try {
            stringRedisTemplate.opsForValue().set(KEY_ALL_PRODUCTS, objectMapper.writeValueAsString(products), CACHE_TTL);
        } catch (Exception e) {
            log.error(  "Error serializing all products to Redis: {}",
                    e.getMessage());
        }
    }
}
